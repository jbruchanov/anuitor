package com.scurab.gwt.anuitor.client.viewmodel;

import java.util.List;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

/**
 * Tree view model for tree view hieararchy control
 * @author jbruchanov
 *
 */
public class ViewHierarchyTreeViewModel implements TreeViewModel {

    private ViewNodeJSO mRoot;
    
    public ViewHierarchyTreeViewModel(ViewNodeJSO root) {
        if(root == null){
            throw new IllegalArgumentException("root == null!");
        }
        mRoot = root;
    }

    /**
     * Get the {@link NodeInfo} that provides the children of the specified
     * value.
     */

    public <T> NodeInfo<?> getNodeInfo(T value) {
        //root
        if (value == null && mRoot != null) {
            ListDataProvider<ViewNodeJSO> dataProvider = new ListDataProvider<ViewNodeJSO>();
            dataProvider.getList().add(mRoot);
            return new DefaultNodeInfo<ViewNodeJSO>(dataProvider, mViewNodeCell);
        }
      
        //childs
        ListDataProvider<ViewNodeJSO> dataProvider = new ListDataProvider<ViewNodeJSO>();
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

        return new DefaultNodeInfo<ViewNodeJSO>(dataProvider, mViewNodeCell);
    }

    /**
     * Check if the specified value represents a leaf node. Leaf nodes cannot be
     * opened.
     */
    public boolean isLeaf(Object value) { 
        return value != null && (((ViewNodeJSO) value).getNodes().length() == 0);
    }

    private ViewNodeCell mViewNodeCell = new ViewNodeCell();

    /**
     * Simple ViewNodeJSO -> String renderer
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
