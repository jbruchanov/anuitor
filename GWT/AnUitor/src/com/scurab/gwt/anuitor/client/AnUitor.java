package com.scurab.gwt.anuitor.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.scurab.gwt.anuitor.client.ui.ResourcesPage;
import com.scurab.gwt.anuitor.client.ui.FileStoragePage;
import com.scurab.gwt.anuitor.client.ui.ScreenPreviewPage;
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
            toOpen = new ScreenPreviewPage();
        } else if ("3D".equals(screen)) {
            toOpen = new ThreeDPage();
        } else if ("ViewHierarchy".equals(screen)) {
            toOpen = new TreeViewPage();
        } else if ("Resources".equals(screen)) {
            toOpen = new ResourcesPage();
        } else if ("FileStorage".equals(screen)) {
            toOpen = new FileStoragePage();
        } else {
            screen = "";
            toOpen = createSelectionPane();
        }

        openWidget(screen, toOpen);
    }

    private CellPanel createSelectionPane() {
        VerticalPanel hp = new VerticalPanel();
        hp.setWidth("100%");
        hp.setStyleName("mainScreenContent", true);        
        hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        hp.add(createButton("ScreenPreview"));
        hp.add(createButton("3D"));
        hp.add(createButton("ViewHierarchy"));
        hp.add(createButton("Resources"));
        hp.add(createButton("FileStorage"));       

        return hp;
    }
    
    private Button createButton(String name){
        Button btn = new Button(name);
        btn.setStyleName("mainScreenButton", true);
        btn.addClickHandler(mClickHandler);
        return btn;
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
