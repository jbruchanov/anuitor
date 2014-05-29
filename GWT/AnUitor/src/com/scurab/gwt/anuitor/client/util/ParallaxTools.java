package com.scurab.gwt.anuitor.client.util;

import com.scurab.gwt.anuitor.client.ViewMesh;
import com.scurab.gwt.anuitor.client.model.ViewFields;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.objects.Mesh;

/**
 * Help methods for working in 3D mode
 * @author jbruchanov
 *
 */
public final class ParallaxTools {

    private ParallaxTools() {
    }

    private static final int LAYER_DISTANCE = 100;
    private static final double HALF = 0.5;
    private static final double DEPTH = 0.01;

    public static CubeGeometry geometryFromView(ViewNodeJSO view) {
        return new CubeGeometry(view.getDouble(ViewFields.WIDTH), -view.getDouble(ViewFields.HEIGHT), DEPTH);
    }

    public static Mesh meshFromView(ViewNodeJSO view, Material material) {        
        double x = view.getDouble(ViewFields.LOCATION_SCREEN_X);
        double y = view.getDouble(ViewFields.LOCATION_SCREEN_Y);
        double z = LAYER_DISTANCE * view.getLevel() + (int) (view.getPosition() / 5.0);

        double w = view.getDouble(ViewFields.WIDTH);
        double h = view.getDouble(ViewFields.HEIGHT);
        double d = DEPTH;

        // moving coordinates because [0,0,0] is center of gravity for cube
        CubeGeometry cg = new CubeGeometry(w, h, d);
        Mesh mesh = new ViewMesh(cg, material, view);        
        mesh.translateX((+w * HALF) + x);
        mesh.translateY((-h * HALF) - y);
        mesh.translateZ((-d * HALF) + z);
        return mesh;
    }       
}
