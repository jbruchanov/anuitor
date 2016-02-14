package com.scurab.gwt.anuitor.client.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Helk class for traversing view tree
 * @author jbruchanov
 *
 */
public class ViewNodeHelper {
    
    public interface Action<T>{
        /**
         * 
         * @param value
         * @param parent
         * @return false if you want to stop traversing
         */
        boolean doAction(T value, T parent);
    }       
    
    public interface HasNodes<T extends JavaScriptObject> {
        JsArray<T> getNodes();
    }

    /**
     * Find views in view hieararchy based on position
     * @param root
     * @param x
     * @param y
     * @return
     */
    public static List<ViewNodeJSO> findViewsByPosition(ViewNodeJSO root, int x, int y) {
        List<ViewNodeJSO> result = new ArrayList<ViewNodeJSO>();
        findByPosition(root, x, y, result);
        return result;
    }    

    /**
     * Traverse tree and fill candindates
     * @param root
     * @param x
     * @param y
     * @param found
     */
    private static void findByPosition(ViewNodeJSO root, int x, int y, List<ViewNodeJSO> found) {
        if (Rect.fromView(root).contains(x, y)) {
            int n = root.getNodes() != null ? root.getNodes().length() : 0;
            if (n > 0) {                
                for (int i = n - 1; i >= 0; i--) {
                    ViewNodeJSO child = root.getNodes().get(i);
                    findByPosition(child, x, y, found);
                }
            }
            found.add(root);            
        }
    }

    /**
     * Find front visible only view
     * @param root
     * @param x
     * @param y
     * @param ignore optional set of views to ignore
     * @return
     */
    public static ViewNodeJSO findFrontVisibleView(ViewNodeJSO root, int x, int y, Set<ViewNodeJSO> ignore) {
        if(((int)root.getDouble(ViewFields.Internal.VISIBILITY)) != 0){//not visible
            return null;
        }
        
        if (Rect.fromView(root, true, false).contains(x, y)) {
            int n = root.getNodes() != null ? root.getNodes().length() : 0;
            if (n > 0) {
                for (int i = n - 1; i >= 0; i--) {
                    ViewNodeJSO child = root.getNodes().get(i);
                    ViewNodeJSO candidate = findFrontVisibleView(child, x, y, ignore);
                    if (candidate != null) {
                        return candidate;
                    }
                }
                
            } 
            if (ignore != null && ignore.contains(root)) {
                return null;
            }
            return root;            
        }
        return null;
    }
    
    /**
     * Sort order based on view level (descending)
     */
    private static final Comparator<ViewNodeJSO> LEVEL_COMPARATOR = new Comparator<ViewNodeJSO>() {
        @Override
        public int compare(ViewNodeJSO o1, ViewNodeJSO o2) {
            return -(o1.getLevel() - o2.getLevel());
        }
    };

    /**
     * Iterate whole tree and call function for every node
     * @param root
     * @param function
     */
    public static <T extends JavaScriptObject & HasNodes<T>> void forEachNodePreOrder(T root, Action<T> function) {
        forEachNodePreOrder(root, null, function);
    }
    
    private static <T extends JavaScriptObject & HasNodes<T>> boolean forEachNodePreOrder(T root, T parent, Action<T> function) {
        if (root == null) {
            return true;
        }

        if(!function.doAction(root, parent)){
            return false;
        }

        int n = root.getNodes() != null ? root.getNodes().length() : 0;
        if (n > 0) {
            for (int i = n - 1; i >= 0; i--) {
                T child = root.getNodes().get(i);
                boolean cont = forEachNodePreOrder(child, root, function);
                if(!cont){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Tranform tree into same tree with {@link ViewTreeNode} classes
     * @param root
     * @return
     */
    public static ViewTreeNode convertToViewTreeNodes(ViewNodeJSO root){
        return convertToViewTreeNodes(root, null);
    }
    
    /**
     * Tranform tree into same tree with {@link ViewTreeNode} classes
     * @param root
     * @param outItemsInLevels optional reference to list to fill how many items are per level (size() = levels, value = items)
     * @return
     */
    public static ViewTreeNode convertToViewTreeNodes(ViewNodeJSO root, List<Integer> outItemsInLevels){        
        return convertToViewTreeNodesImpl(root, null, 0, outItemsInLevels);
    }
    
    private static ViewTreeNode convertToViewTreeNodesImpl(ViewNodeJSO root, ViewTreeNode parent, int level, List<Integer> itemsInLevels){
        if (root == null) {
            return null;
        }
        
        if (itemsInLevels != null) {
            if (itemsInLevels.size() <= level) {
                itemsInLevels.add(1);
            } else {
                itemsInLevels.set(level, itemsInLevels.get(level) + 1);
            }
        }

        ViewTreeNode node = ViewTreeNode.createObject();
        node.setParent(parent);
        node.setView(root);        
        
        int n = root.getNodes() != null ? root.getNodes().length() : 0;        
        if (n > 0) {
            for (int i = 0; i < n; i++) {
                ViewNodeJSO child = root.getNodes().get(i);
                ViewTreeNode vtn = convertToViewTreeNodesImpl(child, node, level + 1, itemsInLevels);
                if (vtn != null) {                    
                    node.addChildren(vtn);
                }
            }
        }
        return node;
    }
}
