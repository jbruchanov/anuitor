package com.scurab.gwt.anuitor.client.ui;

import thothbot.parallax.core.client.RenderingPanel;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEvent;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEventHandler;

public class ThreeDPage extends SplitPanelPage {

    private RenderingPanel mPanel;
    private ThreeDScene mScene;
    private static final int MARGIN = 20;

    @Override
    public IsWidget getContentPanelWidget() {
        if (mPanel == null) {
            mPanel = initScene();
        }
        return mPanel;
    }

    private RenderingPanel initScene() {
        Window.addResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                updateRenderSize(mPanel);
            }
        });
        RenderingPanel renderingPanel = new RenderingPanel();
        updateRenderSize(renderingPanel);        
        renderingPanel.setBackground(0x111111);
        mScene = new ThreeDScene();
        mScene.addMeshClickHandler(new ViewNodeClickEventHandler() {            
            @Override
            public void onViewNodeClick(ViewNodeClickEvent event) {           
                dispatchViewNodeClick(event.getView());
            }
        });
        renderingPanel.setAnimatedScene(mScene);        
        return renderingPanel;
    }
    
    @Override
    protected boolean shouldHideTableOnStart() {     
        return false;
    }

    private void updateRenderSize(RenderingPanel panel) {
        if (panel != null) {            
            panel.setSize(Window.getClientWidth() * CONTENT_PERCENT + "px", Window.getClientHeight() - MARGIN + "px");
        }
    }
}