package com.scurab.gwt.anuitor.client.util;

import com.scurab.gwt.anuitor.client.model.ViewFields;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.objects.Mesh;

public final class ParallaxTools {

    private ParallaxTools(){}

    private static final int LAYER_DISTANCE = 100;
    
    public static CubeGeometry geometryFromView(ViewNodeJSO view){
        return new CubeGeometry(view.getDouble(ViewFields.WIDTH), view.getDouble(ViewFields.HEIGHT), 0.01);      
    }
    
    public static Mesh meshFromView(ViewNodeJSO view, Material material){
        Mesh mesh = new Mesh(geometryFromView(view), material);
        mesh.translateX(view.getDouble(ViewFields.LOCATION_SCREEN_X));
        mesh.translateY(view.getDouble(ViewFields.LOCATION_SCREEN_Y));
        mesh.translateZ(LAYER_DISTANCE*view.getLevel());
        return mesh;
    }
}
