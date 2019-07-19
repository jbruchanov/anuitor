package com.scurab.gwt.anuitor.client.ui.tree;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;
import com.scurab.gwt.anuitor.client.model.ViewTreeNode;
import com.scurab.gwt.anuitor.client.util.DoublePair;
import com.scurab.gwt.anuitor.client.util.StringTools;

public class ShortNameRenderer implements RenderDelegate {    
    private final Rendering mRendering = new HorizontalRendering();
    private final DoublePair SIZING = new DoublePair(50, 20);

    @Override
    public String getType(ViewNodeJSO value) {
        return StringTools.getCapitals(value.getSimpleType());
    }

    @Override
    public String getId(ViewNodeJSO value) {
        return null;
    }

    @Override
    public int getCircleRadius() {
        return 3;
    }

    @Override
    public DoublePair node(ViewTreeNode node) {
        return mRendering.node(node);
    }

    @Override
    public DoublePair svgSize(List<Integer> levelItems) {
        return mRendering.svgSize(levelItems)              
                .multiply(SIZING.first, SIZING.second)
                .atLeast(Window.getClientWidth() - 50, Window.getClientHeight() - 50);
    }

    @Override
    public DoublePair treeSize(List<Integer> levelItems) {
        return mRendering.treeSize(levelItems)
                .multiply(SIZING.second, SIZING.first)
                .atLeast(Window.getClientHeight() / 2, Window.getClientWidth() / 2);
    }
    
    @Override
    public int getOrientation() { 
        return ORIENTATION_HORIZONTAL;
    }

    @Override
    public double getTextOffset() {        
        return 2.5;
    }
}
