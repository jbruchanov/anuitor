package com.scurab.gwt.anuitor.client.ui;

import com.google.gwt.user.client.ui.IsWidget;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEvent;
import com.scurab.gwt.anuitor.client.event.ViewNodeClickEventHandler;
import com.scurab.gwt.anuitor.client.util.TableTools;

public class TreeViewPage extends SplitPanelPage {

    private TreeView mTreeView;      
    
    private TreeView initTreeView() {
        TreeView tv = new TreeView();
        TableTools.initTableForPairs(cellTable);
        tv.addClickHandler(new ViewNodeClickEventHandler() {
            @Override
            public void onViewNodeClick(ViewNodeClickEvent event) {
                dispatchViewNodeClick(event.getView());
            }
        });
        return tv;
    }

    @Override
    public IsWidget getContentPanelWidget() {
        if(mTreeView == null){
            mTreeView = initTreeView();
        }
        return mTreeView;
    }      
}
