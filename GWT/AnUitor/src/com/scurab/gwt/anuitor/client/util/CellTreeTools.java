package com.scurab.gwt.anuitor.client.util;

import com.google.gwt.user.cellview.client.TreeNode;

public final class CellTreeTools {

    /**
     * Expand all nodes in celltree
     * @param node
     */
    public static void expandAll(TreeNode node) {
        changeState(node, true);
    }

    /**
     * Collapse all nodes in celltree
     * @param node
     */
    public static void collapseAll(TreeNode node) {
        changeState(node, false);
    }

    private static void changeState(TreeNode node, boolean open) {
        for (int i = 0; i < node.getChildCount(); i++) {
            if (!node.isChildLeaf(i)) {
                changeState(node.setChildOpen(i, open), open);
            }
        }
    }
}
