package com.scurab.gwt.anuitor.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import thothbot.parallax.core.client.AnimatedScene;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.client.controls.TrackballControls;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.Log;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color;
import thothbot.parallax.core.shared.core.Ray;
import thothbot.parallax.core.shared.core.Ray.Intersect;
import thothbot.parallax.core.shared.core.Vector3;
import thothbot.parallax.core.shared.geometries.CubeGeometry;
import thothbot.parallax.core.shared.geometries.PlaneGeometry;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.objects.Mesh;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.DataProvider.AsyncCallback;
import com.scurab.gwt.anuitor.client.event.ViewHoverChangedEvent;
import com.scurab.gwt.anuitor.client.event.ViewHoverChangedEventHandler;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEvent;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEventHandler;
import com.scurab.gwt.anuitor.client.model.ViewFields;
import com.scurab.gwt.anuitor.client.model.ViewNodeHelper;
import com.scurab.gwt.anuitor.client.model.ViewNodeHelper.Action;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;
import com.scurab.gwt.anuitor.client.util.PBarHelper;
import com.scurab.gwt.anuitor.client.util.ParallaxTools;
import com.scurab.gwt.anuitor.client.util.ViewMesh;

public class ThreeDScene extends AnimatedScene {

    private PerspectiveCamera mCamera;
    private TrackballControls mControls;    
    
    /* Texture color for hovered state, "darker" visibility */
    private Color mHoveredColor = new Color(0x666666);
    /* Default texture color (full visibility) */
    private Color mDefaultColor = new Color(0xFFFFFF);

    /* Collection of views to render with texture */
    private List<ViewNodeJSO> mToRender = new ArrayList<ViewNodeJSO>();

    /* Collection of meshes findable by view */
    private HashMap<ViewNodeJSO, ViewMesh> mViewFrames = new HashMap<ViewNodeJSO, ViewMesh>();
    
    /* Last mesh targetted by mouse */
    private ViewMesh mLastMesh;
    
    private HandlerManager mEventBus = new HandlerManager(this);

    @Override
    protected void onStart() {
        initScene();
        bind();
        loadData();
    }

    /**
     * Init Scene Create camera, projector and show grid
     */
    private void initScene() {       
        mCamera = new PerspectiveCamera(45, // fov
                getRenderer().getAbsoluteAspectRation(), // aspect
                1, // near
                100000 // far
        );
        mCamera.getPosition().setZ(6000);
        mCamera.getPosition().setX(2000);
        mCamera.getPosition().setY(500);

        mControls = new TrackballControls(mCamera, getCanvas());        
             
        drawGrid();
    }

    private void bind() {
        getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                Canvas3d canvas = getCanvas();
                
                Ray pickingRay = ParallaxTools.getPickingRay(mCamera, canvas.getWidth(), canvas.getHeight(), event.getClientX(), event.getClientY());

                List<Ray.Intersect> intersects = pickingRay.intersectObjects(getScene().getChildren());

                if (!intersects.isEmpty()) {//we hit something                    
                    Intersect intersect = intersects.get(intersects.size() - 1);//get the closest to camera
                    
                    if (!(intersect.object instanceof ViewMesh)) {//for floor
                        setMaterial(mLastMesh, false);//just unselect the old one
                        notifyMeshHoverChanged(mLastMesh, false);
                        mLastMesh = null;                            
                    } else {
                        ViewMesh m = (ViewMesh) intersect.object;
                        if (m != mLastMesh) {//we hit different one                            
                            setMaterial(mLastMesh, false);
                            notifyMeshHoverChanged(mLastMesh, false);
                            setMaterial(m, true);
                            notifyMeshHoverChanged(m, true);
                            mLastMesh = m;
                        }
                    }
                } else {                    
                    setMaterial(mLastMesh, false);
                    notifyMeshHoverChanged(mLastMesh, false);
                    mLastMesh = null;                    
                }
            }
        });

        getCanvas().addClickHandler(new ClickHandler() {            
            @Override
            public void onClick(ClickEvent event) {               
                if (mLastMesh != null) {
                    mEventBus.fireEvent(new ViewNodeClickEvent(mLastMesh.getView()));
                }
            }
        });
    }

    /**
     * Load data from server
     */
    private void loadData() {
        PBarHelper.show();
        DataProvider.getTreeHierarchy(new AsyncCallback<ViewNodeJSO>() {
            @Override
            public void onError(Request r, Throwable t) {
                PBarHelper.hide();
                Window.alert(t.getMessage());
            }

            @Override
            public void onDownloaded(ViewNodeJSO result) {
                onDataLoaded(result);                
            }
        });
    }

    /**
     * Called when the data are sucessfuly loaded
     * 
     * @param root
     */
    protected void onDataLoaded(ViewNodeJSO root) {
        double centerX = root.getDouble(ViewFields.WIDTH) / 2;
        double centerY = root.getDouble(ViewFields.HEIGHT) / 2;

        mCamera.lookAt(new Vector3(centerX, centerY, 0));
        mToRender.clear();

        // traverse whole tree and add meshes
        ViewNodeHelper.forEachNodePreOrder(root, new Action<ViewNodeJSO>() {
            @Override
            public boolean doAction(ViewNodeJSO value, ViewNodeJSO parent) {
                try {
                    ViewMesh vm = addMesh(value);
                    mViewFrames.put(value, vm);                       
                } catch (Exception e) {
                    // swallow some internal exception
                    Log.error(e);
                }
                return true;
            }
        });

        // continue loading with textures
        continueLoading();
    }

    /**
     * Continue loading with views
     */
    protected void continueLoading() {
        if (mToRender.size() > 0) {
            try {
                ViewNodeJSO view = mToRender.remove(0);
                addViewNodeTexture(view);
            } catch (Exception e) {
                Log.error(e.getMessage());
                e.printStackTrace();
                continueLoading();
            }
        } else {
            PBarHelper.hide();
        }
    }

    /**
     * Change material state based on selection
     * 
     * @param mesh
     *            frame mesh with optionsl texture mesg like a first child
     * @param selected
     */
    private void setMaterial(ViewMesh mesh, boolean selected) {
        if (mesh != null) {
            MeshBasicMaterial material = (MeshBasicMaterial) mesh.getMaterial();
            if (material.isWireframe()) {
                material.setOpacity(selected ? 1 : ParallaxTools.FRAME_OPACITY);
            }

            if (!mesh.getDescendants().isEmpty()) {
                mesh = (ViewMesh) mesh.getDescendants().get(0);
                material = (MeshBasicMaterial) mesh.getMaterial();
                material.setColor(selected ? mHoveredColor : mDefaultColor);
            }
        }
    }

    /**
     * Add simple [1,1,1] wireframe cube to [0,0,0]
     */
    @SuppressWarnings("unused")
    private void addZeroCube() {
        getScene().add(new Mesh(new CubeGeometry(1, 1, 1), ParallaxTools.createMaterial(0xFFFF00FF, 1)));
    }

    /**
     * Draw world grid for better orientation
     */
    private void drawGrid() {
        MeshBasicMaterial planeMaterial = new MeshBasicMaterial();
        planeMaterial.setColor(new Color(0x333333));
        planeMaterial.setWireframe(true);
        Mesh plane = new Mesh(new PlaneGeometry(100000, 100000, 50, 50), planeMaterial);
        plane.getRotation().setX(-Math.PI / 2);
        plane.translateY(-10000);
        plane.translateZ(-5000);
        getScene().add(plane);
    }

    /**
     * Add new mesh with texture into view frame
     * 
     * @param view
     */
    private void addViewNodeTexture(final ViewNodeJSO view) {
        MeshBasicMaterial material = new MeshBasicMaterial();
        material.setTransparent(true);
        material.setColor(mDefaultColor);

        final ViewMesh m = ParallaxTools.meshFromView(view, material, false);
        final String link = DataProvider.getViewImageLink(view.getPosition());
        Image i = new Image(link);

        i.addErrorHandler(new ErrorHandler() {// only way to get error
            @Override
            public void onError(ErrorEvent event) {
                mToRender.add(view); // add the view again into toRender
                                     // collection and try it again
                getScene().remove(m);// remove texture mesh from scene
                continueLoading();// continue
            }
        });

        Texture txt = new Texture(i, new Texture.ImageLoadHandler() {
            @Override
            public void onImageLoad(Texture texture) {
                // continue loading, we have load it 1 by 1 otherwise android
                // won't handle many req thread creations
                continueLoading();
            }
        });
        txt.setFlipY(false);

        material.setMap(txt);
        ViewMesh frame = mViewFrames.get(view);// add our texture into frame
                                               // mesh like a child
        frame.add(m);
    }

    /**
     * Add particular mesh for view
     * 
     * @param view
     * @return Mesh added into scene
     */
    private ViewMesh addMesh(ViewNodeJSO view) {
        if (view.shouldRender()) {
            mToRender.add(view);
        }

        ViewMesh meshFromView = ParallaxTools.meshFromView(view,
                view.isLeaf() 
                    ? ParallaxTools.createMaterial(0xFF00FF00) 
                    : ParallaxTools.createMaterial(0xFFFF0000));

        getScene().add(meshFromView);
        return meshFromView;
    }

    @Override
    protected void onUpdate(double duration) {
        getRenderer().render(getScene(), mCamera);
        mControls.update();
    }
    
    /**
     * Notify any registered handlers that the hover has been changed
     * @param mesh
     * @param hover
     */
    protected void notifyMeshHoverChanged(ViewMesh mesh, boolean hover){
        if(mesh == null){
            return;
        }
        ViewNodeJSO view = mesh.getView();
        ViewHoverChangedEvent event = new ViewHoverChangedEvent(view, hover);
        mEventBus.fireEvent(event);
    }
    
    public void addMeshHoverChangedHandler(ViewHoverChangedEventHandler handler) {
        mEventBus.addHandler(ViewHoverChangedEvent.TYPE, handler);        
    }

    public void removeMeshHoverChangedHandler(ViewHoverChangedEventHandler handler) {
        mEventBus.removeHandler(ViewHoverChangedEvent.TYPE, handler);
    }
    
    public void addMeshClickHandler(ViewNodeClickEventHandler handler) {
        mEventBus.addHandler(ViewNodeClickEvent.TYPE, handler);        
    }

    public void removeMeshClickEventHandler(ViewNodeClickEventHandler handler) {
        mEventBus.removeHandler(ViewNodeClickEvent.TYPE, handler);
    }
}
