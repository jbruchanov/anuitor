package com.scurab.gwt.anuitor.client.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.scurab.gwt.anuitor.client.AnUitor;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.model.Pair;
import com.scurab.gwt.anuitor.client.model.ViewNodeHelper;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;
import com.scurab.gwt.anuitor.client.model.ViewNodeHelper.Action;
import com.scurab.gwt.anuitor.client.style.CustomTreeResources;
import com.scurab.gwt.anuitor.client.util.CanvasTools;
import com.scurab.gwt.anuitor.client.util.CellTreeTools;
import com.scurab.gwt.anuitor.client.util.CollectionTools;
import com.scurab.gwt.anuitor.client.util.ConfigHelper;
import com.scurab.gwt.anuitor.client.util.DebounceTimer;
import com.scurab.gwt.anuitor.client.util.HTMLColors;
import com.scurab.gwt.anuitor.client.util.PBarHelper;
import com.scurab.gwt.anuitor.client.util.StringTools;
import com.scurab.gwt.anuitor.client.util.TableTools;
import com.scurab.gwt.anuitor.client.util.TableTools.Filter;
import com.scurab.gwt.anuitor.client.util.ViewMesh;
import com.scurab.gwt.anuitor.client.viewmodel.ViewHierarchyTreeViewModel;
import com.scurab.gwt.anuitor.client.viewmodel.ViewHierarchyTreeViewModel.OnSelectionChangedListener;
import com.scurab.gwt.anuitor.client.viewmodel.ViewHierarchyTreeViewModel.OnViewNodeMouseOverListener;
import com.scurab.gwt.anuitor.client.widget.ScaleSliderBar;

import thothbot.parallax.core.shared.Log;

public class ScreenPreviewPage extends Composite {

    private static TestPageUiBinder uiBinder = GWT.create(TestPageUiBinder.class);
    @UiField
    Image image;
    @UiField
    Label mousePosition;
    @UiField
    FlowPanel flowPanel;
    @UiField
    VerticalPanel centerPanel;
    @UiField
    Label hoveredViewID;
    @UiField
    VerticalPanel topImagePanel;
    @UiField
    HorizontalPanel topRow;
    @UiField
    CheckBox ignoreCheckBox;
    @UiField
    CheckBox showGridCheckbox;
    @UiField
    CheckBox gridRenderOutToCenter;
    @UiField
    TextBox gridSize;
    @UiField
    SplitLayoutPanel splitLayoutPanel;
    @UiField
    TextBox filter;
    @UiField(provided = true)    
    CellTable<Pair> cellTable = new CellTable<Pair>();

    /*
     * Current color set for highlighting multiple views below the mouse cursor, currently disabled look for TAG_COLORS
     */
    private static final String[] COLORS = new String[] { HTMLColors.RED, HTMLColors.MAGENTA, HTMLColors.GREEN,
            HTMLColors.CYAN, HTMLColors.YELLOW, HTMLColors.BLUE };

    private static final boolean ZOOM_CANVAS_FEATURE = false;
    /* Zoom canvas scale constant */
    private static final double ZOOM_CANVAS_SCALE = 1.5;
    /* Percent part for image preview */
    private static final double PREVIEW_SCREEN_WITH_PERC = 0.55;
    /* Main canvas scale */
    private float mScale = 1;
    /* Main canvas widget */
    private Canvas mCanvas;
    /* Main canvas widget with image, only for getting color for mouse position pixel */
    private Canvas mCanvasClear;
    /* Preview canvas widget */
    private Canvas mCanvasPreview;
    /* Grid canvas */
    private Canvas mCanvasGrid;
    /* Zoomed canvas preview size, currently disabled */
    private int mCanvasPreviewSize = 250;
    /* Px for grid size */
    private int mGridSize = 48;// 16dp for xxhdpi

    /* Current screenshot dimensions */
    private int mImageWidth;
    private int mImageHeight;

    /* Scale slider bar */
    private ScaleSliderBar mScaleSliderBar;
    /* Minimum scale for canvas */
    private static final int SCALE_MIN = 30;// pct
    /* DataSet for tree view hierarchy */
    private CellTree mCellTree;
    /* Tree view model */
    private ViewHierarchyTreeViewModel mTreeViewModel;
    /* Hover timer */
    private MyTimer mTimer = new MyTimer();
    /* There is a selected view on screen */
    private boolean mSelectedView = false;
    /* Root for view hierarchy */
    private ViewNodeJSO mRoot;
    /* Last known selected node */
    private ViewNodeJSO mSelectedNode;
    /* show cross base on image, not perfect UX, new canvas just for it would be better... */
    private boolean mDrawCross = false;
    /* ignore for mouse position traversing, e.g. for disabling touch_blockers */
    private Set<ViewNodeJSO> mIgnored = new HashSet<ViewNodeJSO>();
    private int mScreenId = 0;
    private String mSelectionColor;
    private final DebounceTimer<String> mFilterDebounce = new DebounceTimer<String>(new DebounceTimer.Callback<String>() {
        @Override
        public void onAction(String filter) {            
            updateFilter(filter, false);
        }       
    });

    interface TestPageUiBinder extends UiBinder<Widget, ScreenPreviewPage> {
    }

    public ScreenPreviewPage(int screenId) {
        initWidget(uiBinder.createAndBindUi(this));
        image.setVisible(false);
        mScreenId = screenId;
        mSelectionColor = ConfigHelper.getSelectionColor();

        mScaleSliderBar = new ScaleSliderBar(200 - SCALE_MIN /* max(200) - min(30) = 170 */, "400px");
        topRow.insert(mScaleSliderBar, 0);

        bind();
        initTable();
        onReloadImage();
    }

    private void bind() {
        int dw = Window.getClientWidth();
        splitLayoutPanel.setWidgetSize(splitLayoutPanel.getWidget(0), (int) (dw * PREVIEW_SCREEN_WITH_PERC));// screenshot
        splitLayoutPanel.setWidgetSize(splitLayoutPanel.getWidget(2), (int) (dw * 0.25));// properties

        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                mCanvas.setCoordinateSpaceWidth((mImageWidth = image.getWidth()));
                mCanvas.setCoordinateSpaceHeight((mImageHeight = image.getHeight()));

                mCanvasGrid.setCoordinateSpaceWidth((mImageWidth = image.getWidth()));
                mCanvasGrid.setCoordinateSpaceHeight((mImageHeight = image.getHeight()));
                // init clear canvas for getting colors
                mCanvasClear.setCoordinateSpaceWidth(mImageWidth);
                mCanvasClear.setCoordinateSpaceHeight(mImageHeight);
                mCanvasClear.getContext2d().drawImage(ImageElement.as(image.getElement()), 0, 0, mImageWidth,
                        mImageHeight);

                int maxH = Window.getClientHeight() - 80;// top panel + margins
                // scale if height is bigger then window
                float scale = 1f;
                if (mImageWidth > mImageHeight) {
                    int maxW = ((int) (Window.getClientWidth() * PREVIEW_SCREEN_WITH_PERC)) - 20;// margins
                    scale = maxW / (float) mImageWidth;
                    scale = Math.max(SCALE_MIN / 100f, scale);
                } else if (mImageHeight > maxH) {
                    scale = maxH / (float) mImageHeight;
                    scale = Math.max(SCALE_MIN / 100f, scale);
                }
                // update slider
                mScaleSliderBar
                        .setValue((int) (((mScaleSliderBar.getMaxValue() + SCALE_MIN) / 2f) * scale) - SCALE_MIN);
                updateGridCanvas(1);
                // finish loading and render
                reloadCanvas(scale);
            }
        });

        mScaleSliderBar.addBarValueChangedHandler(new BarValueChangedHandler() {
            @Override
            public void onBarValueChanged(BarValueChangedEvent event) {
                int value = event.getValue() + SCALE_MIN;// min 30%
                mScale = value / 100f;
                updateImageSize(mScale, true);
            }
        });

        mCanvas = Canvas.createIfSupported();
        mCanvasGrid = Canvas.createIfSupported();
        mCanvasClear = Canvas.createIfSupported();
        if (mCanvas == null) {
            Window.alert("Canvas is not supported!?");
            return;
        }

        mCanvas.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.CROSSHAIR);
        mCanvasGrid.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.CROSSHAIR);

        mCanvasGrid.setStyleName("canvas-grid");
        Canvas canvasForMouse = mCanvasGrid;
        // add mouse move handler, handled view highlighting
        canvasForMouse.addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (mSelectedView || mTreeViewModel != null && mTreeViewModel.getSelectedNode() != null) {
                    return;// dont do anything here if we have selected node
                }
                int x = event.getRelativeX(mCanvas.getElement());
                int y = event.getRelativeY(mCanvas.getElement());
                int scaledX = (int) (x / mScale);
                int scaledY = (int) (y / mScale);

                String color = null;
                if (mCanvasClear != null) {
                    ImageData data = mCanvasClear.getContext2d().getImageData(scaledX, scaledY, 1, 1);
                    color = HTMLColors.getColorFromImageData(data);
                }

                onUpdateImageMouseColor(scaledX, scaledY, color);

                onUpdateZoomCanvas(scaledX, scaledY);
                if (mRoot != null) {
                    mTimer.schedule(x, y, mScale);
                }

                onDrawMouseCross(x, y);
            }
        });

        // mouse out handler just clears selection if necessary
        canvasForMouse.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                if (!mSelectedView) {
                    clearCanvas();
                    if (mTreeViewModel != null) {
                        mTreeViewModel.clearHighlightedNode();
                    }
                    ignoreCheckBox.setValue(false, false);
                }
            }
        });

        // click handler
        canvasForMouse.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (mTreeViewModel != null && mRoot != null) {
                    int x = event.getRelativeX(mCanvas.getElement());
                    int y = event.getRelativeY(mCanvas.getElement());
                    int scaledX = (int) (x / mScale);
                    int scaledY = (int) (y / mScale);
                    ViewNodeJSO vs = ViewNodeHelper.findFrontVisibleView(mRoot, scaledX, scaledY, mIgnored);
                    updateIngoredCheckBox(vs);
                    if (vs != null) {
                        if (vs == mTreeViewModel.getSelectedNode()) {
                            mTreeViewModel.clearSelectedNode();
                            mSelectedView = false;
                        } else {
                            mTreeViewModel.selectNode(vs);
                            mSelectedView = true;
                        }
                    }
                }
            }
        });

        ignoreCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (mTreeViewModel != null) {
                    ViewNodeJSO node = mTreeViewModel.getSelectedNode();
                    if (node == null) {
                        return;// dont do anything here if we have selected node
                    }

                    if (ignoreCheckBox.getValue()) {
                        mIgnored.add(node);
                        mTreeViewModel.highlightAsIgnoredNode(node);
                    } else {
                        mIgnored.remove(node);
                        mTreeViewModel.clearIgnoredNode(node);
                    }
                }
            }
        });

        showGridCheckbox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateGridCanvas(mScale);
                gridSize.setVisible(showGridCheckbox.getValue());
                gridRenderOutToCenter.setVisible(showGridCheckbox.getValue());
            }
        });
        
        gridRenderOutToCenter.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {           
                updateGridCanvas(mScale);
            }
        });

        gridSize.setText(Integer.toString(mGridSize));
        gridSize.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (!Character.isDigit(event.getCharCode())
                        && event.getNativeEvent().getKeyCode() != KeyCodes.KEY_BACKSPACE) {
                    ((TextBox) event.getSource()).cancelKey();
                }
            }
        });
        gridSize.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                enqueuGridUpdate();
            }
        });
        
        filter.addKeyUpHandler(new KeyUpHandler() {            
            @Override
            public void onKeyUp(KeyUpEvent event) {
                mFilterDebounce.postAction(filter.getText());
            }
        });

        AbsolutePanel ap = new AbsolutePanel();
        flowPanel.add(ap);

        // add canvas to document
        ap.add(mCanvas);
        ap.add(mCanvasGrid);
        onInitZoomCanvas();
    }

    protected void onDrawMouseCross(int x, int y) {
        if (mDrawCross) {
            Context2d context = mCanvas.getContext2d();
            context.setStrokeStyle("#FF00FF");
            context.setGlobalAlpha(0.5);
            CanvasTools.drawVerticalLine(context, x, 0, (int) (mImageHeight * mScale));
            CanvasTools.drawHorizontalLine(context, 0, y, (int) (mImageHeight * mScale));
            context.setGlobalAlpha(1);
        }
    }

    private Timer mGridUpdateTimer;
    private int mLastGridSize = -1;

    protected void enqueuGridUpdate() {
        if (mGridUpdateTimer != null) {
            mGridUpdateTimer.cancel();
        }
        mGridUpdateTimer = new Timer() {
            @Override
            public void run() {
                String text = gridSize.getValue();
                if (text.length() > 0) {
                    int size = Integer.parseInt(text);
                    if (size != mLastGridSize) {
                        String color = "red";
                        double ratio = (Math.min(mImageHeight, mImageWidth) * 0.01);
                        if (ratio < size) {
                            mGridSize = size;
                            updateGridCanvas(mScale);
                            color = "black";
                        }
                        mLastGridSize = size;
                        gridSize.getElement().getStyle().setProperty("color", color);
                    }
                }
            }
        };
        mGridUpdateTimer.schedule(100);
    }

    /**
     * Init zoom canvas feature, depends on {@link #ZOOM_CANVAS_FEATURE}
     */
    protected void onInitZoomCanvas() {
        if (!ZOOM_CANVAS_FEATURE) {
            return;
        }
        mCanvasPreview = Canvas.createIfSupported();
        mCanvasPreview.setCoordinateSpaceHeight(mCanvasPreviewSize);
        mCanvasPreview.setCoordinateSpaceWidth(mCanvasPreviewSize);

        centerPanel.add(mCanvasPreview);
    }

    /**
     * Redraw zoom canvas base on position
     * 
     * @param x
     * @param y
     */
    protected void onUpdateZoomCanvas(int x, int y) {
        if (mCanvasPreview == null) {
            return;
        }
        double scale = mScale * ZOOM_CANVAS_SCALE;
        Context2d c = mCanvasPreview.getContext2d();
        c.save();
        c.scale(scale, scale);
        c.setFillStyle(HTMLColors.WHITE);
        c.fillRect(0, 0, mCanvasPreviewSize, mCanvasPreviewSize);
        int half = (int) ((mCanvasPreviewSize >> 1) / scale);
        c.drawImage(ImageElement.as(image.getElement()), -x + half, -y + half);
        c.restore();
    }

    /**
     * Reload canvas with scale = 1
     */
    private void reloadCanvas() {
        reloadCanvas(1f);
    }

    /**
     * Reload canvas with specific scale
     * 
     * @param scale
     */
    private void reloadCanvas(float scale) {
        mScale = scale;
        updateImageSize(mScale, false);
    }

    /**
     * Clear canvas, all highlights will be removed
     */
    private void clearCanvas() {
        updateImageSize(mScale, false);
    }

    /**
     * Init table widget
     */
    private void initTable() {
        TableTools.initTableForPairs(cellTable, mScreenId);
    }

    /**
     * Called when tree node has been changed
     * 
     * @param viewNode
     * @param selected
     */
    protected void onViewTreeNodeSelectionChanged(ViewNodeJSO viewNode, boolean selected) {
        clearCanvas();
        mSelectedView = selected;
        updateIngoredCheckBox(viewNode);
        if (selected) {
            onShowTableDetail(viewNode);
            drawRectForView(viewNode);
        }
    }

    /**
     * Show table detail based on node view
     * 
     * @param viewNode
     */
    private void onShowTableDetail(ViewNodeJSO viewNode) {
        mSelectedNode = viewNode;
        updateFilter(filter.getText(), false);
        //TableTools.createDataProvider(viewNode).addDataDisplay(cellTable);        
    }

    /**
     * Draw rectangle on canvas
     * 
     * @param c
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void drawRectangle(Canvas c, int x, int y, int w, int h) {
        CanvasTools.drawRectangle(c, x, y, w, h, mSelectionColor, mSelectionColor);
    }

    /**
     * Draw rectangle on canvas to highlight view
     * 
     * @param view
     */
    private void drawRectForView(ViewNodeJSO view) {
        clearCanvas();
        if (view.hasCustomRenderSize()) {
            drawRectForView(view, mCanvas, mScale, HTMLColors.ORANGE, HTMLColors.TRANSPARENT, true);
        }
        drawRectForView(view, mCanvas, mScale, mSelectionColor, mSelectionColor, false);
    }

    /**
     * Draw rectangle on canvas to highlight view with specific colors
     * 
     * @param view
     * @param canvas
     * @param scale
     * @param stroke
     * @param fill
     */
    private void drawRectForView(ViewNodeJSO view, Canvas canvas, float scale, String stroke, String fill) {
        drawRectForView(view, canvas, scale, stroke, fill, true);
    }

    private void drawRectForView(ViewNodeJSO view, Canvas canvas, float scale, String stroke, String fill,
            boolean renderArea) {
        if (view == null) {
            return;
        }
        hoveredViewID.setText("ID:" + view.getID() + " Name:" + view.getIDName());
        CanvasTools.drawRectForView(view, canvas, scale, stroke, fill, renderArea);
    }

    /**
     * Called when is necessary to update mouse position
     * 
     * @param x
     * @param y
     */
    protected void onUpdateImageMouseColor(int x, int y, String color) {
        mousePosition.setText("X:" + x + " Y:" + y + " " + (color != null ? color : ""));
    }

    /**
     * Called when image is necessary to reload
     */
    protected void onReloadImage() {
        String url = DataProvider.SCREEN + DataProvider.SCREEN_INDEX_QRY + mScreenId + "&time="
                + System.currentTimeMillis();
        image.setUrl(url); // just to avoid caching
        loadTree();
    }

    private void updateIngoredCheckBox(ViewNodeJSO node) {
        ignoreCheckBox.setValue(node != null && mIgnored.contains(node), false);
    }

    /**
     * Load tree view hierarchy
     */
    private void loadTree() {
        PBarHelper.show();
        DataProvider.getTreeHierarchy(mScreenId, new DataProvider.AsyncCallback<ViewNodeJSO>() {
            @Override
            public void onError(Request req, Response res, Throwable t) {
                PBarHelper.hide();
                Window.alert(t.getMessage());
            }

            @Override
            public void onDownloaded(ViewNodeJSO result) {
                mRoot = result;
                CellTree.Resources res = GWT.create(CustomTreeResources.class);
                // create tree model
                mTreeViewModel = new ViewHierarchyTreeViewModel(result);
                // add selection handler to select view on canvas
                mTreeViewModel.setOnSelectionChangedListener(new OnSelectionChangedListener() {
                    @Override
                    public void onSelectionChanged(ViewNodeJSO viewNode, boolean selected) {
                        ScreenPreviewPage.this.onViewTreeNodeSelectionChanged(viewNode, selected);
                    }
                });
                // add hover listener just to highlight view
                mTreeViewModel.setOnViewNodeMouseOverListener(new OnViewNodeMouseOverListener() {
                    @Override
                    public void onMouseOver(ViewNodeJSO viewNode) {
                        if (mTreeViewModel.getSelectedNode() == null) {
                            clearCanvas();
                            updateIngoredCheckBox(viewNode);
                            drawRectForView(viewNode);
                        }
                    }
                });
                // remove old tree if necessary, currently there is no reload support (reload page only)
                if (mCellTree != null) {
                    mCellTree.removeFromParent();
                }
                mCellTree = new CellTree(mTreeViewModel, null, res);
                mCellTree.setDefaultNodeSize(1000);// no show more button
                centerPanel.add(mCellTree);
                CellTreeTools.expandAll(mCellTree.getRootTreeNode());
                mCellTree.setAnimationEnabled(true);
                
                //preset views for ignoring                
                final Set<Integer> ignoreViewIds = CollectionTools.jsonArrayAsIntegerSet((JSONArray) AnUitor.getConfig().get("PointerIgnoreIds"));                
                if (ignoreViewIds != null && !ignoreViewIds.isEmpty()) {
                    ViewNodeHelper.forEachNodePreOrder(mRoot, new Action<ViewNodeJSO>() {
                        @Override
                        public boolean doAction(ViewNodeJSO value, ViewNodeJSO parent) {
                            int viewId = value.getID();
                            if(ignoreViewIds.contains(viewId)) {
                                mIgnored.add(value);
                                mTreeViewModel.highlightAsIgnoredNode(value);
                            }
                            return true;
                        }
                    });
                }
                PBarHelper.hide();
            }
        });
    }

    /**
     * Update image size based on scale
     * 
     * @param scale
     *            1f = 100%
     */
    private void updateImageSize(float scale, boolean updateGrid) {
        int w = (int) (mImageWidth * scale);
        int h = (int) (mImageHeight * scale);
        mCanvas.setCoordinateSpaceWidth(w);
        mCanvas.setCoordinateSpaceHeight(h);
        mCanvas.getContext2d().drawImage(ImageElement.as(image.getElement()), 0, 0, w, h);

        if (updateGrid) {
            updateGridCanvas(scale);
        }
    }

    private void updateGridCanvas(float scale) {
        int w = (int) (.5f + mImageWidth * scale);
        int h = (int) (.5f + mImageHeight * scale);
        //update element size, otherwise mouse handler doesn't work if it's bigger then fullhd 
        mCanvasGrid.setWidth(w + "px");
        mCanvasGrid.setHeight(h + "px");
        if (showGridCheckbox.getValue()) {
            mCanvasGrid.setCoordinateSpaceWidth(w);
            mCanvasGrid.setCoordinateSpaceHeight(h);
            if (gridRenderOutToCenter.getValue()) {
                CanvasTools.drawGridOuter(mCanvasGrid, mGridSize * scale);
            } else {
                CanvasTools.drawGrid(mCanvasGrid, mGridSize * scale);
            }
        } else {
            mCanvasGrid.getContext2d().clearRect(0, 0, w, h);
        }
    }
    
    private void updateFilter(final String filter, boolean regexp) {
        if (mSelectedNode != null) {
            final String filterExpr = StringTools.filterExpression(filter);            
            TableTools.createDataProvider(mSelectedNode, new Filter<Pair>() {
                @Override
                public boolean accept(Pair item) {
                    if(filterExpr != null) {
                        return item.key.toLowerCase().contains(filterExpr)
                         || StringTools.emptyIfNull(item.value).contains(filterExpr);
                    } else {
                        return true;
                    }
                }
            }).addDataDisplay(cellTable);
        }
    }      

    /**
     * Timer for little delay between mouse move and "hover", performance issue
     * 
     * @author jbruchanov
     *
     */
    private class MyTimer extends Timer {
        private int mX;
        private int mY;
        private float mScale;

        @Override
        public void run() {

            int scaledX = (int) (mX / mScale);
            int scaledY = (int) (mY / mScale);

            clearCanvas();
            onDrawMouseCross(mX, mY);
            if (true) {
                ViewNodeJSO vs = ViewNodeHelper.findFrontVisibleView(mRoot, scaledX, scaledY, mIgnored);
                if(vs == null) {
                    return;
                }
                if (vs.hasCustomRenderSize()) {
                    drawRectForView(vs, mCanvas, mScale, HTMLColors.ORANGE, HTMLColors.TRANSPARENT, true);
                }
                drawRectForView(vs, mCanvas, mScale, mSelectionColor, mSelectionColor, false);
                updateIngoredCheckBox(vs);
                if (mTreeViewModel != null) {
                    mTreeViewModel.highlightNode(vs);
                }
                return;
            }

            // disabled for now to show rest of views below a cursor TAG_COLORS
            List<ViewNodeJSO> views = ViewNodeHelper.findViewsByPosition(mRoot, scaledX, scaledY);

            for (int i = 0, n = views.size(); i < n; i++) {
                ViewNodeJSO vs = views.get(i);
                if (vs.hasCustomRenderSize()) {
                    drawRectForView(vs, mCanvas, mScale, HTMLColors.ORANGE, HTMLColors.TRANSPARENT, true);
                }
                drawRectForView(vs, mCanvas, mScale, mSelectionColor, COLORS[0], false);
            }
        }

        public void schedule(int x, int y, float scale) {
            cancel();
            mX = x;
            mY = y;
            mScale = scale;
            schedule(5);
        }
    };
}
