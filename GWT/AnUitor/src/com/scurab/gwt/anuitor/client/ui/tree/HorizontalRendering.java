package com.scurab.gwt.anuitor.client.ui.tree;

import java.util.Collections;
import java.util.List;

import com.scurab.gwt.anuitor.client.model.ViewTreeNode;
import com.scurab.gwt.anuitor.client.util.DoublePair;

public class HorizontalRendering implements Rendering {

    @Override
    public DoublePair node(ViewTreeNode node) {
        return new DoublePair(node.y(), node.x());
    }

    @Override
    public DoublePair svgSize(List<Integer> levelItems) {
        return new DoublePair((double) levelItems.size(), (double) Collections.max(levelItems));
    }

    @Override
    public DoublePair treeSize(List<Integer> levelItems) {        
        return svgSize(levelItems).swap();
    }
}
