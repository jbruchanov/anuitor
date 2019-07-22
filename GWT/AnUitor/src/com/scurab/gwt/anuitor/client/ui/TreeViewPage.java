package com.scurab.gwt.anuitor.client.ui;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.DataProvider.AsyncCallback;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEvent;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEventHandler;
import com.scurab.gwt.anuitor.client.ui.tree.DefaultRenderer;
import com.scurab.gwt.anuitor.client.ui.tree.NoTextRenderer;
import com.scurab.gwt.anuitor.client.ui.tree.RenderDelegate;
import com.scurab.gwt.anuitor.client.ui.tree.ShortNameRenderer;

public class TreeViewPage extends SplitPanelPage {

    private IsWidget mRoot;
    private TreeView mTreeView;

    private static final RenderDelegate[] RENDERERS = new RenderDelegate[] { new DefaultRenderer(), new ShortNameRenderer(),
            new NoTextRenderer() };

    public TreeViewPage(int screenIndex) {
        super(screenIndex);
    }

    private IsWidget initPage(int screenIndex) {
        VerticalPanel hp = new VerticalPanel();
        hp.setWidth("100%");
        mTreeView = new TreeView(screenIndex);
        mTreeView.addClickHandler(new ViewNodeClickEventHandler() {
            @Override
            public void onViewNodeClick(ViewNodeClickEvent event) {
                dispatchViewNodeClick(event.getView());
            }
        });
        hp.add(createListBox());
        hp.add(mTreeView);
        return hp;
    }

    @Override
    public IsWidget getContentPanelWidget(int screenIndex) {
        if (mRoot == null) {
            mRoot = initPage(screenIndex);
        }
        return mRoot;
    }

    private ListBox createListBox() {
        final ListBox lb = new ListBox(false);
        for (int i = 0, n = RENDERERS.length; i < n; i++) {
            String name = RENDERERS[i].getClass().getSimpleName()
                    //add space
                    .replace("Renderer", " Renderer");
            lb.addItem(name, name);
        }
        lb.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                onRendererChange(RENDERERS[lb.getSelectedIndex()]);
            }
        });
        return lb;
    }

    private void onRendererChange(RenderDelegate renderer) {        
        mTreeView.setRenderer(renderer);
    }
}
