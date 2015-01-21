package com.scurab.gwt.anuitor.client.ui;

import com.google.gwt.user.client.ui.IsWidget;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEvent;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEventHandler;

public class TreeViewPage extends SplitPanelPage {

    private TreeView mTreeView;    
    
    public TreeViewPage(int screenIndex) {
        super(screenIndex);        
    }
    
    private TreeView initTreeView(int screenIndex) {
        TreeView tv = new TreeView(screenIndex);
        tv.addClickHandler(new ViewNodeClickEventHandler() {
            @Override
            public void onViewNodeClick(ViewNodeClickEvent event) {
                dispatchViewNodeClick(event.getView());
            }
        });
        return tv;
    }

    @Override
    public IsWidget getContentPanelWidget(int screenIndex) {
        if(mTreeView == null){
            mTreeView = initTreeView(screenIndex);
        }
        return mTreeView;
    }      
}
