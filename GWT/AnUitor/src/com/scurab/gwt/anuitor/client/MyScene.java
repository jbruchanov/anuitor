package com.scurab.gwt.anuitor.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.http.client.Request;
import com.scurab.gwt.anuitor.client.DataProvider.AsyncCallback;
import com.scurab.gwt.anuitor.client.model.ViewNodeHelper;
import com.scurab.gwt.anuitor.client.model.ViewNodeHelper.Func;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;
import com.scurab.gwt.anuitor.client.util.ParallaxTools;

import thothbot.parallax.core.client.AnimatedScene;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshNormalMaterial;
//import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;

public class MyScene extends AnimatedScene {

    private static final String texture = "./screen.png";
    PerspectiveCamera camera;

    private CustomTrackballControls controls;
    private MeshBasicMaterial redMaterial;
    private MeshBasicMaterial greenMaterial;

    @Override
    protected void onStart() {
        camera = new PerspectiveCamera(45, // fov
                getRenderer().getAbsoluteAspectRation(), // aspect
                1, // near
                100000 // far
        );
        camera.getPosition().setZ(9000);

        controls = new CustomTrackballControls(camera, getCanvas());
        
//        material = new MeshBasicMaterial();
//        Texture txt = new Texture(texture);
//        txt.setFlipY(false);
//        material.setMap(txt);

         redMaterial = new MeshBasicMaterial();
         redMaterial.setColor(new Color(0xFF0000));
         redMaterial.setWireframe(true);
         
         greenMaterial = new MeshBasicMaterial();
         greenMaterial.setColor(new Color(0xFF00FF00));
         greenMaterial.setWireframe(true);

        DataProvider.getTreeHierarchy(new AsyncCallback<ViewNodeJSO>() {

            @Override
            public void onError(Request r, Throwable t) {

            }

            @Override
            public void onDownloaded(ViewNodeJSO result) {
                int q = 1;
                q++;
                mToRender.clear();
                ViewNodeHelper.forEachNodePreOrder(result, new Func<ViewNodeJSO>() {

                    @Override
                    public void doFunc(ViewNodeJSO value) {
                        int level = value.getLevel();
                        try {
                            addMesh(value);
                        } catch (Exception e) {

                        }
                    }
                });
                continueLoading();
                continueLoading();
            }
        });
    }
    
    private void continueLoading(){
        if(mToRender.size() > 0){
            ViewNodeJSO view = mToRender.remove(0);
            drawViewNodeTexture(view);
        }
    }
    
    private void drawViewNodeTexture(ViewNodeJSO view){
        MeshBasicMaterial material = new MeshBasicMaterial();
        material.setTransparent(true);            
      
        String link = "./view.png?position=" + view.getPosition(); 
        Texture txt = new Texture(link, new Texture.ImageLoadHandler() {            
            @Override
            public void onImageLoad(Texture texture) {
                continueLoading();
            }
        });
        
        txt.setFlipY(false);
        material.setMap(txt);            
        getScene().add(ParallaxTools.meshFromView(view, material)); 
    }
    
    private List<ViewNodeJSO> mToRender = new ArrayList<ViewNodeJSO>(); 

    int ctr = 0;
    private void addMesh(ViewNodeJSO view) {        
        if(view.shouldRender()){         
            mToRender.add(view);            
        }              
        getScene().add(ParallaxTools.meshFromView(view, view.isLeaf() ? redMaterial : greenMaterial));        
    }

    private double mLastDuration = 0;

    @Override
    protected void onUpdate(double duration) {
        double delta = (duration - mLastDuration) / 10;
        mLastDuration = duration;

        getRenderer().render(getScene(), camera);
        this.controls.update();
    }
}
