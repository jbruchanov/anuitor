package com.scurab.gwt.anuitor.client.ui.tree;

import java.util.Collections;
import java.util.List;

import com.scurab.gwt.anuitor.client.model.ViewTreeNode;
import com.scurab.gwt.anuitor.client.util.DoublePair;

public class VerticalRendering implements Rendering {

    public int width(List<Integer> levelItems) {
        return Collections.max(levelItems);
    }

    public int height(List<Integer> levelItems) {
        return levelItems.size();
    }

    @Override
    public DoublePair node(ViewTreeNode node) {
        return new DoublePair(node.x(), node.y());
    }

    @Override
    public DoublePair svgSize(List<Integer> levelItems) {
        return new DoublePair((double) Collections.max(levelItems), (double) levelItems.size());
    }

    @Override
    public DoublePair treeSize(List<Integer> levelItems) {
        return svgSize(levelItems);
    }

    @Override
    public boolean swapsSizes() {
        return false;
    }
}
