package com.scurab.gwt.anuitor.client.util;

import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.Camera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Projector;
import thothbot.parallax.core.shared.core.Ray;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.scurab.gwt.anuitor.client.model.ViewFields;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

/**
 * Help methods for working in 3D mode
 * 
 * @author jbruchanov
 * 
 */
public final class ParallaxTools {

    private ParallaxTools() {
    }

    public static double FRAME_OPACITY = 0.05;
    
    private static final int LAYER_DISTANCE = 100;
    private static final double HALF = 0.5;
    private static final double DEPTH = 0.01;

    public static CubeGeometry geometryFromView(ViewNodeJSO view) {
        return new CubeGeometry(view.getDouble(ViewFields.WIDTH), -view.getDouble(ViewFields.HEIGHT), DEPTH);
    }

    public static ViewMesh meshFromView(ViewNodeJSO view, Material material) {
        return meshFromView(view, material, true);
    }

    public static ViewMesh meshFromView(ViewNodeJSO view, Material material, boolean translate) {
        double x = view.getDouble(ViewFields.LOCATION_SCREEN_X);
        double y = view.getDouble(ViewFields.LOCATION_SCREEN_Y);
        double z = LAYER_DISTANCE * view.getLevel() + (int) (view.getPosition() / 5.0);

        double w = view.getDouble(ViewFields.WIDTH);
        double h = view.getDouble(ViewFields.HEIGHT);
        double d = DEPTH;

        // moving coordinates because [0,0,0] is center of gravity for cube
        CubeGeometry cg = new CubeGeometry(w, h, d);
        ViewMesh mesh = new ViewMesh(cg, material, view);
        if (translate) {
            mesh.translateX((+w * HALF) + x);
            mesh.translateY((-h * HALF) - y);
            mesh.translateZ((-d * HALF) + z);
        }
        return mesh;
    }
    
    /**
     * Create simple material with {@link #FRAME_OPACITY} as opacity 
     * @param color
     * @return
     */
    public static MeshBasicMaterial createMaterial(int color) {
        return createMaterial(color, FRAME_OPACITY);
    }
    
    /**
     * Create simple material wireframe material
     * @param color
     * @return
     */
    public static MeshBasicMaterial createMaterial(int color, double opacity) {
        MeshBasicMaterial mat = new MeshBasicMaterial();
        mat.setColor(new Color(color));
        mat.setOpacity(opacity);
        mat.setTransparent(true);
        mat.setWireframe(true);
        return mat;
    }
    
    /**
     * Generate material with text
     * @param row1
     * @param row2 optional, can be null
     * @return
     */
    public static MeshBasicMaterial createTextMaterial(String row1, String row2) {
        Texture t = new Texture(generateTexture(row1, row2));
        t.setFlipY(false);
        t.setNeedsUpdate(true);

        MeshBasicMaterial textMaterial = new MeshBasicMaterial();
        textMaterial.setColor(new Color(0xFFFFFFFF));
        textMaterial.setTransparent(true);
        textMaterial.setMap(t);
        return textMaterial;
    }

    /**
     * Generate 2 row text texture 
     * @param row
     * @param row2 optional, can be null
     * @return
     */
    public static CanvasElement generateTexture(String row, String row2) {
        CanvasElement canvas = Document.get().createElement("canvas").cast();
        canvas.setWidth(512);//must be power2 value, otherwise it will be black
        canvas.setHeight(512);

        Context2d context = canvas.getContext2d();
        context.setFont("Bold 10px Arial");
        context.setFillStyle("rgba(255,255,255,10)");
        context.fillText(row, 20, 20);
        
        if (!(row2 == null)) {
            context.fillText(row2, 20, 30);
        }

        return canvas;
    }
    
    private static final Vector3 MOUSE_VECTOR = new Vector3(0, 0, 1);
    private static final Projector PROJECTOR = new Projector();
    
    /**
     * Return picking ray for particula mouse cursor position
     * @param camera
     * @param canvasWidth
     * @param canvasHeight
     * @param clientX
     * @param clientY
     * @return
     */
    public static Ray getPickingRay(Camera camera, double canvasWidth, double canvasHeight, double clientX, double clientY) {
        // convert coordinates to [-1,1] relative window coordinates
        double x = (clientX / canvasWidth) * 2 - 1;
        double y = 1 - 2 * (clientY / canvasHeight);
        MOUSE_VECTOR.set(x, y);

        Ray pickingRay = PROJECTOR.pickingRay(MOUSE_VECTOR.clone(), camera);
        return pickingRay;
    }
}
