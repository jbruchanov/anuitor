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
                openWidget(((Button) event.getSource()).getText(), new TestPage());
            }
        });
        hp.add(screen);
        Button triD = new Button("3D");
        triD.setWidth("120px");
        triD.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openWidget(((Button) event.getSource()).getText(), new ThreeDPage());
            }
        });
        hp.add(triD);
        Button vh = new Button("ViewHierarchy");
        vh.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openWidget(((Button) event.getSource()).getText(), new TreeViewPage());
            }
        });
        hp.add(vh);        
        openWidget("", hp);
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
}
