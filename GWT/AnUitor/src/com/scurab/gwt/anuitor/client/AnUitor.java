package com.scurab.gwt.anuitor.client;

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
import com.scurab.gwt.anuitor.client.ui.ResourcesPage;
import com.scurab.gwt.anuitor.client.ui.TestPage;
import com.scurab.gwt.anuitor.client.ui.ThreeDPage;
import com.scurab.gwt.anuitor.client.ui.TreeViewPage;

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
                    openScreen("");
                }
            }
        });

        openScreen(History.getToken());        
    }

    private void openScreen(String screen) {
        IsWidget toOpen = null;
        if ("ScreenPreview".equals(screen)) {
            toOpen = new TestPage();
        } else if ("3D".equals(screen)) {
            toOpen = new ThreeDPage();
        } else if ("ViewHierarchy".equals(screen)) {
            toOpen = new TreeViewPage();
        } else if ("Resources".equals(screen)) {
            toOpen = new ResourcesPage();
        }else {
            screen = "";
            toOpen = createSelectionPane();
        }

        openWidget(screen, toOpen);
    }

    private HorizontalPanel createSelectionPane() {
        HorizontalPanel hp = new HorizontalPanel();

        Button screen = new Button("ScreenPreview");
        screen.addClickHandler(mClickHandler);
        hp.add(screen);

        Button triD = new Button("3D");
        triD.setWidth("120px");
        triD.addClickHandler(mClickHandler);
        hp.add(triD);

        Button vh = new Button("ViewHierarchy");
        vh.addClickHandler(mClickHandler);
        hp.add(vh);
        
        Button res = new Button("Resources");
        res.addClickHandler(mClickHandler);
        hp.add(res);

        return hp;
    }

    private IsWidget mLastScreen;

    private void openWidget(String v, IsWidget w) {
        History.newItem(v);
        if (mLastScreen != null) {
            RootLayoutPanel.get().remove(mLastScreen);
        }
        mLastScreen = w;
        RootLayoutPanel.get().add(mLastScreen);
    }

    private ClickHandler mClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            openScreen(((Button) event.getSource()).getText());
        }
    };
}
