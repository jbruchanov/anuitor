package com.scurab.gwt.anuitor.client.ui.tree;

import java.util.List;


import com.scurab.gwt.anuitor.client.model.ViewTreeNode;
import com.scurab.gwt.anuitor.client.util.DoublePair;

public interface Rendering {
    DoublePair node(ViewTreeNode node);    
    /**
     * SVG element size, if smaller then diagram is cut, bigger allows scrollbars to scroll out of anything visible 
     * @param levelItems
     * @return
     */
    DoublePair svgSize(List<Integer> levelItems);
    
    /**
     * Tree size, has influence for spacing of nodes
     * @param levelItems
     * @return
     */
    DoublePair treeSize(List<Integer> levelItems);
}