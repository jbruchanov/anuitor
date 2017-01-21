package com.scurab.gwt.anuitor.client.viewmodel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

/**
 * Tree view model for tree view hieararchy control
 * 
 * @author jbruchanov
 * 
 */
public class ViewHierarchyTreeViewModel implements TreeViewModel {

    private static final String CSS_HIGHLIGHTED_NODE = "treeNode_highlighted";
    private static final String CSS_IGNORED_NODE = "treeNode_ignored";

    public interface OnSelectionChangedListener {
        void onSelectionChanged(ViewNodeJSO viewNode, boolean selected);
    }

    public interface OnViewNodeMouseOverListener {
        void onMouseOver(ViewNodeJSO viewNode);
    }

    /* Root of tree view hierarchy */
    private ViewNodeJSO mRoot;

    private OnSelectionChangedListener mOnSelectionChangedListener;

    private OnViewNodeMouseOverListener mOnViewNodeMouseOverListener;

    private Set<String> mIgnored = new HashSet<String>();

    /*
     * Selection Model, must be singleton for whole viewmodel to avoid multiple selection!
     */
    private SingleSelectionModel<ViewNodeJSO> mSelectionModel = new SingleSelectionModel<ViewNodeJSO>();

    public ViewHierarchyTreeViewModel(ViewNodeJSO root) {
        if (root == null) {
            throw new IllegalArgumentException("root == null!");
        }
        mRoot = root;
        // add our selection changed handler
        mSelectionModel.addSelectionChangeHandler(mSelectionChangedHandler);
    }

    /**
     * Get the {@link NodeInfo} that provides the children of the specified value.
     */
    public <T> NodeInfo<?> getNodeInfo(T value) {
        ListDataProvider<ViewNodeJSO> dataProvider = new ListDataProvider<ViewNodeJSO>();

        if (value == null && mRoot != null) { // root
            dataProvider.getList().add(mRoot);
        } else { // childs
            ViewNodeJSO root = (ViewNodeJSO) value;
            JsArray<ViewNodeJSO> childs = root.getNodes();

            int len = 0;
            if (childs != null && (len = childs.length()) > 0) {
                List<ViewNodeJSO> list = dataProvider.getList();
                for (int i = 0, n = len; i < n; i++) {
                    ViewNodeJSO node = childs.get(i);
                    list.add(node);
                }
            }
        }

        return createNodeInfo(dataProvider);
    }

    /**
     * Create node info for particular data provider
     * 
     * @param dataProvider
     * @return
     */
    private DefaultNodeInfo<ViewNodeJSO> createNodeInfo(ListDataProvider<ViewNodeJSO> dataProvider) {
        return new DefaultNodeInfo<ViewNodeJSO>(dataProvider, mViewNodeCell, mSelectionModel, null);
    }

    /**
     * Check if the specified value represents a leaf node. Leaf nodes cannot be opened.
     */
    public boolean isLeaf(Object value) {
        if (value != null) {
            ViewNodeJSO root = (ViewNodeJSO) value;
            int n = root.getNodes() != null ? root.getNodes().length() : 0;
            return n == 0;
        }
        return false;
    }

    /**
     * Set on selection changed listener
     * 
     * @param onSelectionChangedListener
     */
    public void setOnSelectionChangedListener(OnSelectionChangedListener onSelectionChangedListener) {
        mOnSelectionChangedListener = onSelectionChangedListener;
    }

    public void setOnViewNodeMouseOverListener(OnViewNodeMouseOverListener onViewNodeMouseOverListener) {
        mOnViewNodeMouseOverListener = onViewNodeMouseOverListener;
    }

    private void notifySelectionChanged(ViewNodeJSO viewNode, boolean selected) {
        if (mOnSelectionChangedListener != null) {
            mOnSelectionChangedListener.onSelectionChanged(viewNode, selected);
        }
    }

    private SelectionChangeHandler mSelectionChangedHandler = new SelectionChangeHandler();

    /**
     * Selection handler implementation to notify listener
     */
    private class SelectionChangeHandler implements Handler {
        private ViewNodeJSO mLastSelected = null;

        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            @SuppressWarnings("unchecked")
            SingleSelectionModel<ViewNodeJSO> ssm = (SingleSelectionModel<ViewNodeJSO>) event.getSource();
            if (mLastSelected != null) {
                notifySelectionChanged(mLastSelected, false);
            }
            mLastSelected = ssm.getSelectedObject();
            notifySelectionChanged(mLastSelected, ssm.isSelected(mLastSelected));
        }
    };

    private ViewNodeCell mViewNodeCell = new ViewNodeCell();

    /**
     * Simple ViewNodeJSO -> String rendering
     * 
     * @author jbruchanov
     * 
     */
    private class ViewNodeCell extends AbstractSafeHtmlCell<ViewNodeJSO> {
        private static final String EVENT_MOUSEOVER = "mouseover";
        private static final String EVENT_CLICK = "click";

        public ViewNodeCell() {
            super(new SafeHtmlRenderer<ViewNodeJSO>() {
                @Override
                public SafeHtml render(ViewNodeJSO object) {
                    return SafeHtmlUtils.fromString(object.getSimpleType());
                }

                @Override
                public void render(ViewNodeJSO object, SafeHtmlBuilder builder) {
                    builder.append(render(object));
                }
            }, EVENT_MOUSEOVER, EVENT_CLICK);
        }

        @Override
        protected void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb) {
            Object o = context.getKey();
            String key;
            if (o instanceof ViewNodeJSO) {
                key = getNodeJSOId((ViewNodeJSO) o);
            } else {
                key = Long.toString(System.currentTimeMillis());
            }
            sb.appendHtmlConstant("<label class=\"treeNode" + (mIgnored.contains(key) ? (" " + CSS_IGNORED_NODE) : "")
                    + "\" id=\"" + key + "\">");
            if (data != null) {
                sb.append(data);
            }
            sb.appendHtmlConstant("</label>");
        }

        @Override
        public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, ViewNodeJSO value,
                NativeEvent event, ValueUpdater<ViewNodeJSO> valueUpdater) {

            dispatchBrowserEvent(value, event);
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
        }

        private void dispatchBrowserEvent(ViewNodeJSO value, NativeEvent event) {
            String type = event.getType();
            if (EVENT_MOUSEOVER.equals(type) && mOnViewNodeMouseOverListener != null) {
                mOnViewNodeMouseOverListener.onMouseOver(value);
            } else if (EVENT_CLICK.equals(type)) { // unselect by click
                if (mSelectionChangedHandler.mLastSelected == value) {
                    mSelectionModel.clear();
                    // don't call notify now, it's called by selection handler
                }
            }
        }
    }

    /**
     * Get current selected node, null if nothing is selected
     * 
     * @return
     */
    public ViewNodeJSO getSelectedNode() {
        return mSelectionModel.getSelectedObject();
    }

    /**
     * Select node
     * 
     * @param vs
     */
    public void selectNode(ViewNodeJSO vs) {
        if (mSelectionChangedHandler.mLastSelected != null) {
            mSelectionModel.setSelected(mSelectionChangedHandler.mLastSelected, false);
        }
        mSelectionModel.setSelected(vs, true);
    }

    /**
     * Clear any selected node
     * 
     * @return true if there was a selection
     */
    public boolean clearSelectedNode() {
        if (mSelectionChangedHandler.mLastSelected != null) {
            mSelectionModel.setSelected(mSelectionChangedHandler.mLastSelected, false);
            return true;
        }
        return false;
    }

    /* Last highlighted element */
    private Element mHighlightedElement;

    /**
     * Highlight element by viewnode
     * 
     * @param vs
     */
    public void highlightNode(ViewNodeJSO vs) {
        if (vs == null) {
            return;
        }
        String key = "ViewNodeJSO_id_" + vs.hashCode();
        clearHighlightedNode();
        mHighlightedElement = DOM.getElementById(key);
        if (mHighlightedElement != null) {
            mHighlightedElement.addClassName(CSS_HIGHLIGHTED_NODE);
        }
    }

    /**
     * Remove highlight for any viewnode
     * 
     * @return true if there was a highlight
     */
    public boolean clearHighlightedNode() {
        if (mHighlightedElement != null) {
            mHighlightedElement.removeClassName(CSS_HIGHLIGHTED_NODE);
            mHighlightedElement = null;
            return true;
        }
        return false;
    }

    public void highlightAsIgnoredNode(ViewNodeJSO vs) {
        if (vs == null) {
            return;
        }
        String key = getNodeJSOId(vs);
        mIgnored.add(key);
        Element el = DOM.getElementById(key);
        if (el != null) {
            el.addClassName(CSS_IGNORED_NODE);
        }
    }

    public void clearIgnoredNode(ViewNodeJSO vs) {
        if (vs == null) {
            return;
        }
        String key = getNodeJSOId(vs);
        mIgnored.remove(key);
        Element el = DOM.getElementById(key);
        if (el != null) {
            el.removeClassName(CSS_IGNORED_NODE);
        }
    }

    private static String getNodeJSOId(ViewNodeJSO node) {
        return node == null ? null : "ViewNodeJSO_id_" + node.hashCode();
    }

}
