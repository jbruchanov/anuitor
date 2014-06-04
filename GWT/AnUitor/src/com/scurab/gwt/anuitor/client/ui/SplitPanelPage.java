package com.scurab.gwt.anuitor.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.scurab.gwt.anuitor.client.model.Pair;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;
import com.scurab.gwt.anuitor.client.util.TableTools;

public abstract class SplitPanelPage extends Composite {

    private static SplitPanelPageUiBinder uiBinder = GWT.create(SplitPanelPageUiBinder.class);
    
    public static final double CONTENT_PERCENT = 0.75;

    interface SplitPanelPageUiBinder extends UiBinder<Widget, SplitPanelPage> {
    }

    @UiField SplitLayoutPanel splitLayoutPanel;
    @UiField ScrollPanel contentPanel;
    @UiField ScrollPanel cellTablePanel;
    @UiField(provided = true) CellTable<Pair> cellTable = new CellTable<Pair>();
    
    boolean mFirstClick = true;

    public SplitPanelPage() {
        initWidget(uiBinder.createAndBindUi(this));
        TableTools.initTableForPairs(cellTable);
        splitLayoutPanel.setWidgetSize(contentPanel, Window.getClientWidth() * (shouldHideTableOnStart() ? 0.99 : CONTENT_PERCENT));       
        contentPanel.add(getContentPanelWidget());
    }        
    
    protected boolean shouldHideTableOnStart() {
        return true;
    }
    
    public abstract IsWidget getContentPanelWidget();    

    protected void dispatchViewNodeClick(ViewNodeJSO view) {
        if(mFirstClick){
            splitLayoutPanel.setWidgetSize(contentPanel, Window.getClientWidth() * CONTENT_PERCENT);
            mFirstClick = false;
        }
        TableTools.createDataProvider(view).addDataDisplay(cellTable);
    }    
}
