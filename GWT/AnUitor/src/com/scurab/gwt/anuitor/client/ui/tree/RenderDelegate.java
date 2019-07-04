package com.scurab.gwt.anuitor.client.ui.tree;

import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

public interface RenderDelegate extends Rendering {
    public static final int ORIENTATION_VERTICAL = 1;
    public static final int ORIENTATION_HORIZONTAL = 2;
    String getType(ViewNodeJSO value);
    String getId(ViewNodeJSO value);
    int getCircleRadius();
    int getOrientation();
    double getTextOffset();
}
