package com.scurab.gwt.anuitor.client.ui;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.model.Pair;
import com.scurab.gwt.anuitor.client.model.ViewNodeHelper;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;
import com.scurab.gwt.anuitor.client.style.CustomTreeResources;
import com.scurab.gwt.anuitor.client.util.CanvasTools;
import com.scurab.gwt.anuitor.client.util.CellTreeTools;
import com.scurab.gwt.anuitor.client.util.HTMLColors;
import com.scurab.gwt.anuitor.client.util.TableTools;
import com.scurab.gwt.anuitor.client.viewmodel.ViewHierarchyTreeViewModel;
import com.scurab.gwt.anuitor.client.viewmodel.ViewHierarchyTreeViewModel.OnSelectionChangedListener;
import com.scurab.gwt.anuitor.client.viewmodel.ViewHierarchyTreeViewModel.OnViewNodeMouseOverListener;

public class TestPage extends Composite {

    private static TestPageUiBinder uiBinder = GWT.create(TestPageUiBinder.class);
    @UiField Button btnUp;
    @UiField Button btnDown;
    @UiField Image image;
    @UiField Button btnReload;
    @UiField Label mousePosition;
    @UiField FlowPanel flowPanel;
    @UiField Button btnTest;
    @UiField VerticalPanel testPanel;
    @UiField Label text;    
    @UiField(provided=true) CellTable<Pair> cellTable = new CellTable<Pair>();

    private float mScale = 1;
    private Canvas mCanvas;

    private Canvas mCanvasPreview;
    private int mCanvasPreviewSize = 250;

    private int mImageWidth;
    private int mImageHeight;

    interface TestPageUiBinder extends UiBinder<Widget, TestPage> {
    }

    public TestPage() {
        initWidget(uiBinder.createAndBindUi(this));
        image.setVisible(false);
        bind();
        initTable();
    }

    private void bind() {
        btnUp.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onScaleUp();
            }
        });

        btnDown.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onScaleDown();
            }
        });
        btnReload.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onReloadImage();
            }
        });

        btnTest.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                onTest();
            }
        });

        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                btnReload.setEnabled(true);
                mCanvas.setCoordinateSpaceWidth((mImageWidth = image.getWidth()));
                mCanvas.setCoordinateSpaceHeight((mImageHeight = image.getHeight()));
                reloadCanvas();
            }
        });
        image.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(ErrorEvent event) {
                btnReload.setEnabled(true);
            }
        });

        
        mCanvas = Canvas.createIfSupported();
        mCanvas.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.CROSSHAIR);
        if (mCanvas != null) {
            mCanvas.addMouseMoveHandler(new MouseMoveHandler() {
                @Override
                public void onMouseMove(MouseMoveEvent event) { 
                    if(mTreeViewModel != null && mTreeViewModel.getSelectedNode() != null){
                        return;//dont do anything here if we have selected node
                    }
                    int x = event.getRelativeX(mCanvas.getElement());
                    int y = event.getRelativeY(mCanvas.getElement());
                    onUpdateImageMousePosition(x, y);

                    int scaledX = (int) (x / mScale);
                    int scaledY = (int) (y / mScale);
                    onUpdateZoomCanvas(scaledX, scaledY);
                    if (mRoot != null) {
                        mTimer.schedule(scaledX, scaledY);
                    }
                }
            });
            
            mCanvas.addMouseOutHandler(new MouseOutHandler() {                
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    clearCanvas();
                    mTreeViewModel.clearHighlightedNode();
                }
            });
            
            mCanvas.addMouseDownHandler(new MouseDownHandler() {                
                @Override
                public void onMouseDown(MouseDownEvent event) {               
                    if (mTreeViewModel != null && mRoot != null) {
                        int x = event.getRelativeX(mCanvas.getElement());
                        int y = event.getRelativeY(mCanvas.getElement());                        
                        int scaledX = (int) (x / mScale);
                        int scaledY = (int) (y / mScale);
                        ViewNodeJSO vs = ViewNodeHelper.findFrontVisibleView(mRoot, scaledX, scaledY);
                        if(vs != null){                            
                            if (vs == mTreeViewModel.getSelectedNode()) {
                                mTreeViewModel.clearSelectedNode();
                            } else {
                                mTreeViewModel.selectNode(vs);
                            }                            
                        }
                    }
                }
            });
            flowPanel.add(mCanvas);

            onInitZoomCanvas();
        }
    }

    private MyTimer mTimer = new MyTimer();

    private class MyTimer extends Timer {
        private int mX;
        private int mY;

        @Override
        public void run() {
            
            clearCanvas();
            if(true){
                ViewNodeJSO vs = ViewNodeHelper.findFrontVisibleView(mRoot, mX, mY);
                drawRectForView(vs, mCanvas, mScale, HTMLColors.RED, COLORS[0]);
                if(mTreeViewModel != null){
                    mTreeViewModel.highlightNode(vs);
                }
                return;
            }
            
            List<ViewNodeJSO> views = ViewNodeHelper.findViewsByPosition(mRoot, mX, mY);
            
            for (int i = 0, n = views.size(); i < n; i++) {
                ViewNodeJSO v = views.get(i);                
                drawRectForView(v, mCanvas, mScale, HTMLColors.RED, COLORS[i % COLORS.length]);
            }
        }

        public void schedule(int x, int y) {
            cancel();
            mX = x;
            mY = y;
            schedule(5);
        }
    };

    private String[] COLORS = new String[] { HTMLColors.RED,HTMLColors.MAGENTA, HTMLColors.GREEN, HTMLColors.CYAN, HTMLColors.YELLOW, HTMLColors.BLUE};

    private void onInitZoomCanvas() {
        if (true) {// disabled for now
            return;
        }
        mCanvasPreview = Canvas.createIfSupported();
        mCanvasPreview.setCoordinateSpaceHeight(mCanvasPreviewSize);
        mCanvasPreview.setCoordinateSpaceWidth(mCanvasPreviewSize);
        testPanel.add(mCanvasPreview);
    }

    protected void onUpdateZoomCanvas(int x, int y) {
        if (mCanvasPreview == null) {
            return;
        }
        Context2d c = mCanvasPreview.getContext2d();
        c.save();
        double scale = 1.5;
        c.scale(scale, scale);
        c.setFillStyle(HTMLColors.WHITE);
        c.fillRect(0, 0, mCanvasPreviewSize, mCanvasPreviewSize);
        int half = (int) ((mCanvasPreviewSize >> 1) / scale);
        c.drawImage(ImageElement.as(image.getElement()), -x + half, -y + half);
        c.restore();
    }

    private void reloadCanvas() {
        mScale = 1f;
        updateImageSize(mScale);
    }

    private void clearCanvas() {
        updateImageSize(mScale);
    }

    private ViewNodeJSO mRoot;

    protected void onTest() {
        List<ViewNodeJSO> views = ViewNodeHelper.findViewsByPosition(mRoot, 420, 400);
        int s = views.size();
    }
    
    private void initTable(){
        TableTools.initTableForPairs(cellTable);                    
    }
    
    private void onViewTreeNodeSelectionChanged(ViewNodeJSO viewNode, boolean selected){
        clearCanvas();
        if (selected) {
            onShowTableDetail(viewNode);
            drawRectForView(viewNode);
        }        
    }        

    private void onShowTableDetail(ViewNodeJSO viewNode) {        
        TableTools.createDataProvider(viewNode).addDataDisplay(cellTable);
    }

    public static void drawRectangle(Canvas c, int x, int y, int w, int h) {
        CanvasTools.drawRectangle(c, x, y, w, h, HTMLColors.RED, HTMLColors.YELLOW);
    }
    
    private void drawRectForView(ViewNodeJSO view) {
        clearCanvas();
        drawRectForView(view, mCanvas, mScale, HTMLColors.RED, HTMLColors.YELLOW);
    }

    private void drawRectForView(ViewNodeJSO view, Canvas canvas, float scale, String stroke, String fill) {
        if (view == null) {
            return;
        }       
        text.setText("ID:" + view.getID() + " Name:" + view.getIDName());
        CanvasTools.drawRectForView(view, canvas, scale, stroke, fill);
    }
   
    protected void onUpdateImageMousePosition(int x, int y) {
        ImageData data = mCanvas.getContext2d().getImageData(x, y, 1, 1);
        String color = HTMLColors.getColorFromImageData(data);
        mousePosition.setText("M x:" + x + " y:" + y + " " + color);
    }
   
    protected void onReloadImage() {
        btnReload.setEnabled(false);
        image.setUrl("/screen.png?time=" + System.currentTimeMillis());
        loadTree();
    }
    
    private ViewHierarchyTreeViewModel mTreeViewModel;
    
    CellTree mCellTree;
    private void loadTree(){
        DataProvider.getTreeHierarchy(new DataProvider.AsyncCallback<ViewNodeJSO>() {

            @Override
            public void onError(Request r, Throwable t) {

            }

            @Override
            public void onDownloaded(ViewNodeJSO result) {
                mRoot = result;                
                CellTree.Resources res = GWT.create(CustomTreeResources.class);
                mTreeViewModel = new ViewHierarchyTreeViewModel(result);
                mTreeViewModel.setOnSelectionChangedListener(new OnSelectionChangedListener() {                    
                    @Override
                    public void onSelectionChanged(ViewNodeJSO viewNode, boolean selected) {                       
                        TestPage.this.onViewTreeNodeSelectionChanged(viewNode, selected);
                    }
                });
                mTreeViewModel.setOnViewNodeMouseOverListener(new OnViewNodeMouseOverListener() {
                    @Override
                    public void onMouseOver(ViewNodeJSO viewNode) {
                        if (mTreeViewModel.getSelectedNode() == null) {
                            clearCanvas();
                            drawRectForView(viewNode);
                        }
                    }
                });
                
                if(mCellTree != null){
                    mCellTree.removeFromParent();
                }
                mCellTree = new CellTree(mTreeViewModel, null, res);                   
                testPanel.add(mCellTree);                
                CellTreeTools.expandAll(mCellTree.getRootTreeNode());
                mCellTree.setAnimationEnabled(true);
            }                        
        });
    }

    protected void onScaleDown() {
        mScale -= 0.1f;
        updateImageSize(mScale);
    }

    protected void onScaleUp() {
        mScale += 0.1f;
        updateImageSize(mScale);
    }

    private void updateImageSize(float scale) {
        int w = (int) (mImageWidth * scale);
        int h = (int) (mImageHeight * scale);
        mCanvas.setCoordinateSpaceWidth(w);
        mCanvas.setCoordinateSpaceHeight(h);
        mCanvas.getContext2d().drawImage(ImageElement.as(image.getElement()), 0, 0, w, h);
    }      
}
