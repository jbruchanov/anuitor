package com.scurab.gwt.anuitor.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtd3.api.Coords;
import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.arrays.Array;
import com.github.gwtd3.api.arrays.ForEachCallback;
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.core.Transition;
import com.github.gwtd3.api.core.UpdateSelection;
import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.functions.KeyFunction;
import com.github.gwtd3.api.layout.Link;
import com.github.gwtd3.api.layout.Node;
import com.github.gwtd3.api.layout.TreeLayout;
import com.github.gwtd3.api.svg.Diagonal;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.DataProvider.AsyncCallback;
import com.scurab.gwt.anuitor.client.event.ViewHoverChangedEvent;
import com.scurab.gwt.anuitor.client.event.ViewHoverChangedEventHandler;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEvent;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEventHandler;
import com.scurab.gwt.anuitor.client.model.ViewNodeHelper;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;
import com.scurab.gwt.anuitor.client.model.ViewTreeNode;
import com.scurab.gwt.anuitor.client.style.TreeViewResources;
import com.scurab.gwt.anuitor.client.style.TreeViewStyle;
import com.scurab.gwt.anuitor.client.ui.tree.DefaultRenderer;
import com.scurab.gwt.anuitor.client.ui.tree.RenderDelegate;
import com.scurab.gwt.anuitor.client.util.DoublePair;
import com.scurab.gwt.anuitor.client.util.PBarHelper;

/**
 * Tree view panel for view hierarchy
 * This was made based on examples {@link http://bl.ocks.org/mbostock/4063570}, 
 * {@link http://gwt-d3.appspot.com/}, 
 * {@link https://github.com/gwtd3/gwt-d3/blob/master/gwt-d3-demo/src/main/java/com/github/gwtd3/demo/client/democases/TreeDemo.java}
 * @author jbruchanov
 *
 */
public class TreeView extends FlowPanel {

    private static final String ORIGIN_Y = "y0";
    private static final String ORIGIN_X = "x0";

    private static final TreeViewStyle CSS = TreeViewResources.INSTANCE.css();

    /* Animation duration for expand/collapse animation */   
    private static final int DURATION = 750;
    
    private static final int TEXT_X_OFFSET = 10;
    private static final int TEXT_Y_2ND_LINE_OFFSET = 10;
    
    private RenderDelegate mRenderDelegate = new DefaultRenderer();
          
    private ViewNodeJSO mLoadedData = null;
    /* Root for transformed tree from ViewNodeJSO */   
    private ViewTreeNode mViewTreeRoot = null;
    /* SVG graph */
    private Selection mSvg = null;
    /* Tree layout object */
    private TreeLayout mTreeLayout = null;
    /* Global projection for lines between nodes */
    private Diagonal mDiagonal = null;
    /* Currently selected/last clicked node*/
    private ViewTreeNode mSelectedNode;
    /* EventBus for click/hover events */
    private HandlerManager mEventBus = new HandlerManager(this);      

    public TreeView(int screenIndex) {
        super();        
        TreeViewResources.INSTANCE.css().ensureInjected();  
        PBarHelper.show();
        DataProvider.getTreeHierarchy(screenIndex, new AsyncCallback<ViewNodeJSO>() {
            @Override
            public void onError(Request req, Response res, Throwable t) {
                PBarHelper.hide();
                Window.alert(t.getMessage());
            }
            
            @Override
            public void onDownloaded(ViewNodeJSO result) {
                onDataLoaded(result);
                PBarHelper.hide();
            };
        });
    }
    
    public void setRenderer(RenderDelegate renderer) {  
        getElement().removeAllChildren();
        if (renderer == null) {
            renderer = new DefaultRenderer();
        }
        mRenderDelegate = renderer;
        if (mLoadedData != null) {
            onDataLoaded(mLoadedData);
        }
    }
    
    protected void onDataLoaded(ViewNodeJSO srcroot) {
        mLoadedData = srcroot;        
        List<Integer> levelItems = new ArrayList<Integer>();        
        mViewTreeRoot = ViewNodeHelper.convertToViewTreeNodes(srcroot, levelItems);                
                
        final DoublePair treeSize = mRenderDelegate.treeSize(levelItems);
        final DoublePair svgSize = mRenderDelegate.svgSize(levelItems);
        // get tree layout
        mTreeLayout = D3.layout().tree().size(treeSize.first, treeSize.second);//rotated to grow to right => height = width and width = height

        //init SVG
        mSvg = initSVG(svgSize.first, svgSize.second);
        
        // set the global way to draw paths
        mDiagonal = initDiagonal();

        // get the root of the tree and initialize it
        mViewTreeRoot.setAttr(ORIGIN_X, treeSize.first / 2);
        mViewTreeRoot.setAttr(ORIGIN_Y, 0);
        
        //collapse all of them if necessary
        boolean collapseAll = false;
        if (collapseAll && mViewTreeRoot.children() != null) {
            mViewTreeRoot.children().forEach(new Collapse());
        }
        //update tree layout
        update(mViewTreeRoot);        
    }
    
    /**
     * Init SVG object
     * @param width
     * @param height
     * @return
     */
    private Selection initSVG(double width, double height) {
        int marginHorizontalHalf = 10;
        int marginVerticalHalf = 10; 
        Selection svg = D3.select(this)
                .append("svg")                
                .attr("width", width + (marginHorizontalHalf * 2))//margins
                .attr("height", height + (marginVerticalHalf * 2))
                .append("g")
                    .attr("transform", "translate(" + marginHorizontalHalf +", " + marginVerticalHalf + ")");//move because of margins
        return svg;
    }
    
    /**
     * Init diagonal for global path rendering
     * @return
     */
    private Diagonal initDiagonal(){
        Diagonal diagonal = D3.svg()
                .diagonal()
                //line rendering
                .projection(new DatumFunction<Array<Double>>() {
                    @Override                    
                    public Array<Double> apply(Element context, Value d, int index) {
                        ViewTreeNode data = d.<ViewTreeNode> as();
                        //don't use Array.fromDoubles(args), it doesn't work after compilation...
                        //seems it calls toString() but after compilation returns object reference instead of proper comma separated values
                        return mRenderDelegate.node(data).toArray();
                    }
                });
        return diagonal;
    }
    
    
    
    /**
     * Update tree layout
     * @param source
     */
    private void update(final ViewTreeNode source) {
        Array<Node> nodes = mTreeLayout.nodes(mViewTreeRoot).reverse();
        Array<Link> links = mTreeLayout.links(nodes);
        final String originX;
        final String originY;
        //only few places, has to accomodate switched axis
        switch (mRenderDelegate.getOrientation()) {
        case RenderDelegate.ORIENTATION_HORIZONTAL:
            originX = ORIGIN_Y;
            originY = ORIGIN_X;
            break;
        case RenderDelegate.ORIENTATION_VERTICAL:
            originX = ORIGIN_X;
            originY = ORIGIN_Y;
            break;
        default:
            throw new IllegalStateException("Unsupported orientation:" + mRenderDelegate.getOrientation());
        }

        // assign ids to nodes
        UpdateSelection node = mSvg.selectAll("g." + CSS.node()).data(nodes, new KeyFunction<Integer>() {
                    private int i;
                    @Override
                    public Integer map(Element context, Array<?> newDataArray, Value datum, int index) {
                        ViewTreeNode d = datum.<ViewTreeNode> as();
                        return ((d.id() == -1) ? d.id(++i) : d.id());
                    }
                });

        // add click function on node click
        Selection nodeEnter = node
                .enter()
                .append("g")
                .attr("class", CSS.node())
                .attr("transform", "translate(" + source.getNumAttr(originX) + "," + source.getNumAttr(originY) + ")")
                .on("click", new NodeClickHandler())
                .on("dblclick", new CollapseExpandHandler())
                .on("mouseenter", new HoverHandler(true))
                .on("mouseleave", new HoverHandler(false));

        // add circles to all entering nodes
        nodeEnter.append("circle")
                .attr("r", 1e-6)
                .style("fill", new DatumFunction<String>() {
                    @Override
                    public String apply(Element context, Value d, int index) {
                        JavaScriptObject node = d.<ViewTreeNode> as().getObjAttr("_children");
                        return (node != null) ? "lightsteelblue" : "#fff";
                    }
                });
        
        //add text
        Selection textNode = nodeEnter.append("text")
                .attr("y", mRenderDelegate.getTextOffset());
        
            textNode.append("tspan")//2nd line                    
                    .attr("x", TEXT_X_OFFSET)
                    .attr("font-weight", "bold")
                    .text(new DatumFunction<String>() {                        
                        @Override
                        public String apply(Element context, Value d, int index) {
                            ViewTreeNode as = d.<ViewTreeNode> as();                        
                            return mRenderDelegate.getType(as.getView());                                                
                        }
                    });
                textNode.append("tspan")//2nd line                    
                    .attr("x", TEXT_X_OFFSET)
                    .attr("y", TEXT_Y_2ND_LINE_OFFSET)                    
                    .attr("font-weight", "100")
                    .text(new DatumFunction<String>() {                        
                        @Override
                        public String apply(Element context, Value d, int index) {                            
                            ViewTreeNode as = d.<ViewTreeNode> as();
                            ViewNodeJSO view = as.getView();
                            return mRenderDelegate.getId(view);                            
                        }
                    });


        // transition entering nodes
        Transition nodeUpdate = node.transition()
                .duration(DURATION)
                    .attr("transform", new DatumFunction<String>() {
                        @Override
                        public String apply(Element context, Value d, int index) {
                            ViewTreeNode data = d.<ViewTreeNode>as();
                            DoublePair node = mRenderDelegate.node(data);
                            return "translate(" + node.first + "," + node.second + ")";
                        }
                    });

        nodeUpdate.select("circle")
                .attr("r", mRenderDelegate.getCircleRadius())
                    .style("fill", new DatumFunction<String>() {
                        @Override
                        public String apply(Element context, Value d, int index) {
                            ViewTreeNode node = d.<ViewTreeNode>as();
                            JavaScriptObject object = node.getObjAttr("_children");
                            boolean isCollapsed = object != null;
                            if (node.isSelected()) {
                                return isCollapsed ? "DarkRed" :"Coral";
                            } else {                                                                    
                                return isCollapsed ? "lightsteelblue" : "#fff";
                            }
                        }
                    });

        // transition exiting nodes
        Transition nodeExit = node.exit().transition().duration(DURATION)
                .attr("transform", new DatumFunction<String>() {
                    @Override
                    public String apply(Element context, Value d, int index) {
                        DoublePair dp = mRenderDelegate.node(source);
                        return "translate(" + dp.first + "," + dp.second + ")";
                    }
                }).remove();

        nodeExit.select("circle").attr("r", 1e-6);

        // update svg paths for new node locations
        UpdateSelection link = mSvg.selectAll("path." + CSS.link()).data(links,
                new KeyFunction<Integer>() {
                    @Override
                    public Integer map(Element context, Array<?> newDataArray, Value datum, int index) {
                        return datum.<Link> as().target().<ViewTreeNode> cast().id();
                    }
                });

        link.enter().insert("svg:path", "g").attr("class", CSS.link())
                .attr("d", new DatumFunction<String>() {
                    @Override
                    public String apply(Element context, Value d, int index) {
                      //no need for switching
                        Coords o = Coords.create(source.getNumAttr(ORIGIN_X), source.getNumAttr(ORIGIN_Y));
                        return mDiagonal.generate(Link.create(o, o));
                    }
                });

        link.transition().duration(DURATION).attr("d", mDiagonal);

        link.exit().transition().duration(DURATION).attr("d", new DatumFunction<String>() {
                    @Override
                    public String apply(Element context, Value d, int index) {
                        Coords o = Coords.create(source.x(), source.y());
                        return mDiagonal.generate(Link.create(o, o));
                    }
                }).remove();

        // update locations on node
        nodes.forEach(new ForEachCallback<Void>() {
            @Override
            public Void forEach(Object thisArg, Value element, int index,Array<?> array) {
                ViewTreeNode data = element.<ViewTreeNode> as();
                //no need for switching
                data.setAttr(ORIGIN_X, data.x());
                data.setAttr(ORIGIN_Y, data.y());
                return null;
            }
        });
    }

    /**
     * Collapse for each callback
     * Just use it as root.children().forEach(new Collapse());
     * @author jbruchanov
     *
     */
    private class Collapse implements ForEachCallback<Void> {
        @Override
        public Void forEach(Object thisArg, Value element, int index,
                Array<?> array) {
            ViewTreeNode datum = element.<ViewTreeNode> as();
            Array<Node> children = datum.children();
            if (children != null) {
                datum.setAttr("_children", children);
                datum.getObjAttr("_children").<Array<Node>> cast().forEach(this);
                datum.setAttr("children", null);
            }
            return null;
        }
    }

    /**
     * Collapse/Expand click handler
     * @author jbruchanov
     *
     */
    private class CollapseExpandHandler implements DatumFunction<Void> {
        @Override
        public Void apply(Element context, Value d, int index) {
            ViewTreeNode node = d.<ViewTreeNode> as();
            if (node.children() != null) {
                node.setAttr("_children", node.children());
                node.setAttr("children", null);
            } else {
                node.setAttr("children", node.getObjAttr("_children"));
                node.setAttr("_children", null);
            }
            update(node);
            return null;
        }
    }
    
    /**
     * Mouse enter/leave handler
     * @author jbruchanov
     *
     */
    private class HoverHandler implements DatumFunction<Void> {
        private boolean mEntered;
        public HoverHandler(boolean enter) {
            mEntered = enter;
        }
        @Override
        public Void apply(Element context, Value d, int index) { 
            ViewTreeNode node = d.<ViewTreeNode> as();
            ViewNodeJSO v = node.getView();
            mEventBus.fireEvent(new ViewHoverChangedEvent(v, mEntered));
            return null;
        }        
    }
    
    /**
     * Node click handler fires events and change selection
     * @author jbruchanov
     *
     */
    private class NodeClickHandler implements DatumFunction<Void> {                 
        @Override
        public Void apply(Element context, Value d, int index) {
            ViewTreeNode node = d.<ViewTreeNode> as();
            if(mSelectedNode != null){
                mSelectedNode.setSelected(false);
                update(mSelectedNode);
            }            
            mSelectedNode = node;
            mSelectedNode.setSelected(true);
            
            ViewNodeJSO v = mSelectedNode.getView();
            mEventBus.fireEvent(new ViewNodeClickEvent(v));
            update(node);
            return null;
        }        
    }
    
    public void addClickHandler(ViewNodeClickEventHandler handler) {
        mEventBus.addHandler(ViewNodeClickEvent.TYPE, handler);
    }

    public void removeClickHandler(ViewNodeClickEventHandler handler) {
        mEventBus.removeHandler(ViewNodeClickEvent.TYPE, handler);
    }
    
    public void addHoverChangedHandler(ViewHoverChangedEventHandler handler) {
        mEventBus.addHandler(ViewHoverChangedEvent.TYPE, handler);
    }

    public void removeHoverChangedHandler(ViewHoverChangedEventHandler handler) {
        mEventBus.removeHandler(ViewHoverChangedEvent.TYPE, handler);
    }
}