package com.scurab.gwt.anuitor.client.ui.tree;

import java.util.List;

import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;
import com.scurab.gwt.anuitor.client.model.ViewTreeNode;
import com.scurab.gwt.anuitor.client.util.DoublePair;
import com.scurab.gwt.anuitor.client.util.StringTools;

public class DefaultRenderer implements RenderDelegate {
    private static final int MAX_TEXT_LEN = 38;    
    private final Rendering mRendering = new HorizontalRendering();
    private final DoublePair SIZING = new DoublePair(190, 45);
    @Override
    public String getType(ViewNodeJSO value) {
        return StringTools.ellipsizeMid(value.getSimpleType(), MAX_TEXT_LEN);
    }

    @Override
    public String getId(ViewNodeJSO value) {
        String id = StringTools.emptyIfNull(value.getID() > 0 ? value.getIDName() : "");
        return StringTools.ellipsizeMid(id.replaceAll("@id/", ""), MAX_TEXT_LEN);
    }

    @Override
    public int getCircleRadius() {            
        return 5;
    }

    @Override
    public DoublePair node(ViewTreeNode node) {        
        return mRendering.node(node).multiply(1, 1);
    }

    @Override
    public DoublePair svgSize(List<Integer> levelItems) {
        return mRendering.svgSize(levelItems)
                .multiply(SIZING.first, SIZING.second)
                .plus(SIZING.first, 0);
    }

    @Override
    public DoublePair treeSize(List<Integer> levelItems) {
        return mRendering.treeSize(levelItems)
                .multiply(SIZING.second, SIZING.first);
    }

    @Override
    public int getOrientation() { 
        return ORIENTATION_HORIZONTAL;
    }

    @Override
    public double getTextOffset() {
        return 0;
    }
}
