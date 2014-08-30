package com.scurab.gwt.anuitor.client.util;

import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.objects.Mesh;

import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

public class ViewMesh extends Mesh{
    
    private ViewNodeJSO mView;
    
    public ViewMesh(Geometry geometry, Material material) {
        super(geometry, material);     
    }
    
    public ViewMesh(Geometry geometry, Material material, ViewNodeJSO view) {
        super(geometry, material);
        mView = view;
    }
    
    public ViewNodeJSO getView() {
        return mView;
    }
}
