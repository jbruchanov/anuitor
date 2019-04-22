package com.scurab.gwt.anuitor.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.DataProvider.AsyncCallback;
import com.scurab.gwt.anuitor.client.model.DataResponseJSO;
import com.scurab.gwt.anuitor.client.model.ObjectJSO;
import com.scurab.gwt.anuitor.client.model.Pair;
import com.scurab.gwt.anuitor.client.util.GenericTools;
import com.scurab.gwt.anuitor.client.util.TableTools;

public class ViewPropertyPage extends Composite {

    private static ViewPropertyPageUiBinder uiBinder = GWT.create(ViewPropertyPageUiBinder.class);

    interface ViewPropertyPageUiBinder extends UiBinder<Widget, ViewPropertyPage> {
    }

    @UiField
    ScrollPanel dataScrollPanel;
    @UiField
    ScrollPanel detailScrollPanel;
    @UiField
    VerticalPanel detailVerticalPanel;
    @UiField
    Button rawJson;

    @UiField(provided = true)
    CellTable<Pair> cellTable = new CellTable<Pair>();

    private int mScreenIndex;
    private int mPosition;
    private String mProperty;

    public ViewPropertyPage(int screenIndex, int position, String property) {
        initWidget(uiBinder.createAndBindUi(this));
        mScreenIndex = screenIndex;
        mPosition = position;
        mProperty = property;
        initDataTable();
        loadData();
        detailScrollPanel.setHeight((Window.getClientHeight()) + "px");
        detailScrollPanel.setWidth((Window.getClientWidth() / 3) + "px");
        rawJson.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(GenericTools.createPropertyUrl(mPosition, mProperty, mScreenIndex), "_blank","");
            }
        });
    }

    private void loadData() {
        DataProvider.getViewProperty(mScreenIndex, mPosition, mProperty, new AsyncCallback<DataResponseJSO>() {

            @Override
            public void onError(Request r, Throwable t) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDownloaded(DataResponseJSO result) {                
                ObjectJSO context = result.getContext().<ObjectJSO> cast();                                
                onDataDownloaded(result, convertContext(context));
            }
        });
    }
    
    /**
     * Update scrollpanel height based on current window size
     */
    private void updateScrollContentHeight(){
        String h = Window.getClientHeight() + "px";        
        dataScrollPanel.setHeight(h);        
    }
    
    protected void onDataDownloaded(DataResponseJSO result, List<Pair> dataitems) {        
        TableTools.createDataProvider(dataitems).addDataDisplay(cellTable);
        onShowData(result.getDataType(), result.getData());
        updateScrollContentHeight();
    }
    
    protected void onShowData(String type, String data) {
        if (type != null && data != null) {
            if("base64_png".equals(type)) {
                Image im = new Image("data:image/png;base64," + data);
                im.setStyleName("transparent propertyPreview");                        
                detailVerticalPanel.add(im);
            }
        }
    }

    private void initDataTable() {        
        TableTools.initTableForPairs(cellTable, mScreenIndex);
        cellTable.setPageSize(1000);
    }

    private List<Pair> convertContext(ObjectJSO jso) {
        List<Pair> list = new ArrayList<Pair>();
        String[] keys = jso.getFieldsAsStrings();
        Arrays.sort(keys);

        for (String key : keys) {
            try {
            String value = jso.getFieldValue(key);
                if ("Type".equals(key)) {
                    list.add(0, new Pair(key, value));
                } else {
                    list.add(new Pair(key, value));
                }
            } catch (Throwable t) {
                list.add(new Pair(key, t.getMessage()));
            }
        }
        return list;
    }
}
