package com.scurab.gwt.anuitor.client;

import thothbot.parallax.core.client.RenderingPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.scurab.gwt.anuitor.client.ui.TestPage;
import com.scurab.gwt.anuitor.client.ui.ThreeDScene;
import com.scurab.gwt.anuitor.client.ui.TreeView;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AnUitor implements EntryPoint {
    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (event.getValue().length() == 0) {
                    addSelectionPane();
                }
            }
        });
        addSelectionPane();
    }

    private void addSelectionPane() {
        HorizontalPanel hp = new HorizontalPanel();
        Button screen = new Button("ScreenPreview");
        screen.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openWidget(((Button) event.getSource()).getText(), new TestPage(), false);
            }
        });
        hp.add(screen);
        Button triD = new Button("3D");
        triD.setWidth("120px");
        triD.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                RenderingPanel renderingPanel = new RenderingPanel();
                renderingPanel.setBackground(0x111111);
                ThreeDScene scene = new ThreeDScene();
                renderingPanel.setAnimatedScene(scene);
                openWidget(((Button) event.getSource()).getText(), renderingPanel, true);
            }
        });
        hp.add(triD);
        Button vh = new Button("ViewHierarchy");
        vh.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openWidget(((Button) event.getSource()).getText(), new TreeView(), false);
            }
        });
        hp.add(vh);
        RootLayoutPanel.get().clear();
        RootLayoutPanel.get().add(hp);
    }

    private void openWidget(String v, IsWidget w, boolean rootLayoutPanel) {
        History.newItem(v);
        RootPanel.get().clear();
        RootLayoutPanel.get().clear();
        if (rootLayoutPanel) {
            RootLayoutPanel.get().add(w);
        } else {
            RootPanel.get().add(w);
        }
    }
}
