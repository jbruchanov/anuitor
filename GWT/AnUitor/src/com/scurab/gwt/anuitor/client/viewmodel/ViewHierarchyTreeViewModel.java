package com.scurab.gwt.anuitor.client.viewmodel;

import java.util.List;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
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

    public interface OnSelectionChangedListener {
        void onSelectionChanged(ViewNodeJSO viewNode, boolean selected);
    }

    /* Root of tree view hierarchy */
    private ViewNodeJSO mRoot;

    private OnSelectionChangedListener mOnSelectionChangedListener;

    /*
     * Selection Model, must be singleton for whole viewmodel to avoid multiple
     * selection!
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
     * Get the {@link NodeInfo} that provides the children of the specified
     * value.
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
     * Check if the specified value represents a leaf node. Leaf nodes cannot be
     * opened.
     */
    public boolean isLeaf(Object value) {
        return value != null && (((ViewNodeJSO) value).getNodes().length() == 0);
    }

    /**
     * Set on selection changed listener
     * 
     * @param onSelectionChangedListener
     */
    public void setOnSelectionChangedListener(OnSelectionChangedListener onSelectionChangedListener) {
        mOnSelectionChangedListener = onSelectionChangedListener;
    }

    private void notifySelectionChanged(ViewNodeJSO viewNode, boolean selected) {
        if (mOnSelectionChangedListener != null) {
            mOnSelectionChangedListener.onSelectionChanged(viewNode, selected);
        }
    }

    /**
     * Selection handler implementation to notify listener
     */
    private Handler mSelectionChangedHandler = new Handler() {
        private ViewNodeJSO mLastSelected = null;

        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            @SuppressWarnings("unchecked")
            SingleSelectionModel<ViewNodeJSO> ssm = (SingleSelectionModel<ViewNodeJSO>) event.getSource();
            if (mLastSelected != null) {
                notifySelectionChanged(mLastSelected, false);
            }
            ViewNodeJSO mLastSelected = ssm.getSelectedObject();
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
    private static class ViewNodeCell extends AbstractSafeHtmlCell<ViewNodeJSO> {
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
            });
        }

        @Override
        protected void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb) {
            sb.append(data);
        }
    }
}
