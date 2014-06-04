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
        updateRenderSize(renderingPanel, 0.999);
        renderingPanel.setBackground(0x111111);
        mScene = new ThreeDScene();
        mScene.addMeshClickHandler(new ViewNodeClickEventHandler() {
            @Override
            public void onViewNodeClick(ViewNodeClickEvent event) {
                if (mFirstClick) {
                    updateRenderSize(mPanel);
                }
                dispatchViewNodeClick(event.getView());
            }
        });
        renderingPanel.setAnimatedScene(mScene);
        return renderingPanel;
    }

    private void updateRenderSize(RenderingPanel panel) {
        updateRenderSize(panel, CONTENT_PERCENT);
    }

    private void updateRenderSize(RenderingPanel panel, double coef) {
        if (panel != null) {
            panel.setSize(Window.getClientWidth() * coef + "px", Window.getClientHeight() + "px");
        }
    }
}