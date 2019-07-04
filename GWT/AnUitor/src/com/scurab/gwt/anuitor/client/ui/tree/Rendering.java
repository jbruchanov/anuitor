package com.scurab.gwt.anuitor.client.ui.tree;

import java.util.List;


import com.scurab.gwt.anuitor.client.model.ViewTreeNode;
import com.scurab.gwt.anuitor.client.util.DoublePair;

public interface Rendering {
    DoublePair node(ViewTreeNode node);    
    DoublePair svgSize(List<Integer> levelItems);
    DoublePair treeSize(List<Integer> levelItems);
}