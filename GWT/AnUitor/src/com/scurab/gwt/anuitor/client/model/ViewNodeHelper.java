package com.scurab.gwt.anuitor.client.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
     * @return
     */
    public static ViewNodeJSO findFrontVisibleView(ViewNodeJSO root, int x, int y) {
        if(((int)root.getDouble(ViewFields.Internal.VISIBILITY)) != 0){//not visible
            return null;
        }
        if (Rect.fromView(root).contains(x, y)) {
            int n = root.getNodes() != null ? root.getNodes().length() : 0;
            if (n > 0) {
                for (int i = n - 1; i >= 0; i--) {
                    ViewNodeJSO child = root.getNodes().get(i);
                    ViewNodeJSO candidate = findFrontVisibleView(child, x, y);
                    if (candidate != null) {
                        return candidate;
                    }
                }
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
    public static void forEachNodePreOrder(ViewNodeJSO root, Action<ViewNodeJSO> function) {
        forEachNodePreOrder(root, null, function);
    }
    
    private static boolean forEachNodePreOrder(ViewNodeJSO root, ViewNodeJSO parent, Action<ViewNodeJSO> function) {
        if (root == null) {
            return true;
        }

        if(!function.doAction(root, parent)){
            return false;
        }

        int n = root.getNodes() != null ? root.getNodes().length() : 0;
        if (n > 0) {
            for (int i = n - 1; i >= 0; i--) {
                ViewNodeJSO child = root.getNodes().get(i);
                boolean cont = forEachNodePreOrder(child, root, function);
                if(!cont){
                    return false;
                }
            }
        }
        return true;
    }
}
