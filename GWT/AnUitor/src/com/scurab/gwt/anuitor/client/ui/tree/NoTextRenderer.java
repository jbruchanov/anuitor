package com.scurab.gwt.anuitor.client.ui.tree;

import java.util.List;

import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;
import com.scurab.gwt.anuitor.client.model.ViewTreeNode;
import com.scurab.gwt.anuitor.client.util.DoublePair;

public class NoTextRenderer implements RenderDelegate {
    private final Rendering mRendering = new VerticalRendering();
    private final double SIZE_COEF_X = 4;
    private final double SIZE_COEF_Y = 5;
    @Override
    public String getType(ViewNodeJSO value) {
        return null;
    }

    @Override
    public String getId(ViewNodeJSO value) {
        return null;
    }

    @Override
    public int getCircleRadius() { 
        return 8;
    }

    @Override
    public DoublePair node(ViewTreeNode node) {        
        return mRendering.node(node);
    }

    @Override
    public DoublePair svgSize(List<Integer> levelItems) {
        double x = getCircleRadius() * SIZE_COEF_X;
        double y = getCircleRadius() * SIZE_COEF_Y;
        return mRendering.svgSize(levelItems).multiply(x, y);
    }

    @Override
    public DoublePair treeSize(List<Integer> levelItems) {
        double x = getCircleRadius() * SIZE_COEF_X;
        double y = getCircleRadius() * SIZE_COEF_Y;
        return mRendering.treeSize(levelItems).multiply(x, y);
    }
    
    @Override
    public int getOrientation() { 
        return ORIENTATION_VERTICAL;
    }

    @Override
    public double getTextOffset() {
        return 0;
    }
}