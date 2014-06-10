package com.scurab.gwt.anuitor.client.ui;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.DataProvider.AsyncCallback;
import com.scurab.gwt.anuitor.client.model.ResourceDetailJSO;
import com.scurab.gwt.anuitor.client.model.ResourceItemJSO;
import com.scurab.gwt.anuitor.client.model.ResourcesJSO;

/**
 * Resources page
 * @author jbruchanov
 *
 */
public class ResourcesPage extends Composite {

    private static ResourcesPageUiBinder uiBinder = GWT.create(ResourcesPageUiBinder.class);
    
    interface ResourcesPageUiBinder extends UiBinder<Widget, ResourcesPage> {
    }
    
    @UiField ScrollPanel groupsScrollPanel;
    @UiField ScrollPanel itemsScrollPanel;
    @UiField ScrollPanel detailScrollPanel;
    
    @UiField(provided = true)
    CellTable<String> groups = new CellTable<String>();
    @UiField(provided = true)
    CellTable<ResourceItemJSO> items = new CellTable<ResourceItemJSO>();

    /* Detail view generates content for particular resource details */
    private ResourceDetailView mDetailView = new ResourceDetailView();
    /* Comparator for alphabetical sort */
    private ResItemComparator mComparator = new ResItemComparator();
    /* Loaded data */
    private HashMap<String, HashMap<String, ResourceItemJSO>> mData;   
   
    public ResourcesPage() {
        initWidget(uiBinder.createAndBindUi(this));
        initGroupsTable();
        initItemsTable();   
        
        mDetailView.setStyleName("detailView");
        detailScrollPanel.add(mDetailView);        
        updateScrollContentHeight();
        Window.addResizeHandler(new ResizeHandler() {
            
            @Override
            public void onResize(ResizeEvent event) {              
                updateScrollContentHeight();
            }
        });
        
        loadData();       
    }
    
    /**
     * Load list from server
     */
    protected void loadData() {
        DataProvider.getResources(new AsyncCallback<ResourcesJSO>() {
            @Override
            public void onError(Request r, Throwable t) {
                Window.alert(t.getMessage());
            }

            @Override
            public void onDownloaded(ResourcesJSO result) {
                HashMap<String, HashMap<String, ResourceItemJSO>> transformed = convertData(result);
                onDataLoaded(transformed);
            }
        });
    }
    
    /** 
     * Called when data have been loaded
     * @param values
     */
    public void onDataLoaded(HashMap<String, HashMap<String, ResourceItemJSO>> values) {
        mData = values;
        ListDataProvider<String> dataProvider = new ListDataProvider<String>();
        List<String> list = dataProvider.getList();
        list.addAll(values.keySet());
        java.util.Collections.sort(list);
        dataProvider.addDataDisplay(groups);
    }
    
    /**
     * Convert JSON data to better collection for easier work 
     * @param result
     * @return
     */
    private static HashMap<String, HashMap<String, ResourceItemJSO>> convertData(ResourcesJSO result) {
        HashMap<String, HashMap<String, ResourceItemJSO>> transformed = new HashMap<String, HashMap<String, ResourceItemJSO>>();
        String[] keys = result.getFieldsAsStrings();

        for (String key : keys) {
            HashMap<String, ResourceItemJSO> list = new HashMap<String, ResourceItemJSO>();
            transformed.put(key, list);

            JavaScriptObject field = result.getField(key);
            JsArray<ResourceItemJSO> values = field.<JsArray<ResourceItemJSO>> cast();

            for (int i = 0, n = values.length(); i < n; i++) {
                ResourceItemJSO value = values.get(i);
                list.put(value.getValue(), value);
            }
        }
        return transformed;
    }
    
    /**
     * Update scrollpanel height based on current window size
     */
    private void updateScrollContentHeight(){
        String h = Window.getClientHeight() + "px";
        groupsScrollPanel.setHeight(h);
        itemsScrollPanel.setHeight(h);
        detailScrollPanel.setHeight(h);        
    }

    /** 
     * Initialize group table
     */
    private void initGroupsTable() {
        groups.setPageSize(100);
        Column<String, String> column = new Column<String, String>(new TextCell()) {
            @Override
            public String getValue(String p) {
                return p;
            }
        };
        column.setCellStyleNames("tableLabel");
        groups.addColumn(column, "Group");
        groups.setColumnWidth(column, "200px");
        
        final SingleSelectionModel<String> ssm = new SingleSelectionModel<String>();
        groups.setSelectionModel(ssm);
        ssm.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                String group = ssm.getSelectedObject();
                onSelectionGroupChange(group);
            }
        });
    }
    
    /**
     * Initialize items table
     */
    private void initItemsTable() {
        items.getLoadingIndicator().setVisible(false);
        items.setPageSize(1000);
        Column<ResourceItemJSO, String> column = new Column<ResourceItemJSO, String>(new TextCell()) {
            @Override
            public String getValue(ResourceItemJSO p) {
                return p.getValue();
            }
        };
        column.setCellStyleNames("tableLabel");
        items.addColumn(column, "Item");
        items.setColumnWidth(column, "400px");
        
        final SingleSelectionModel<ResourceItemJSO> ssm = new SingleSelectionModel<ResourceItemJSO>();
        items.setSelectionModel(ssm);
        ssm.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ResourceItemJSO item = ssm.getSelectedObject();
                onSelectionItemChanged(item.getValue(), item);
            }
        });
    }

    /**
     * Called when group has been selected
     * @param group
     */
    public void onSelectionGroupChange(String group) {
        mDetailView.setItem(null, null);//reset current value
        
        Collection<ResourceItemJSO> list = mData.get(group).values();
        ListDataProvider<ResourceItemJSO> dataProvider = new ListDataProvider<ResourceItemJSO>();
        List<ResourceItemJSO> dpList = dataProvider.getList();
        for (ResourceItemJSO value : list) {
            dpList.add(value);
        }        
        java.util.Collections.sort(dpList, mComparator);
        dataProvider.addDataDisplay(items);       
    }    

    /**
     * Called when item selection has been changed and starts loading new details
     * @param group
     * @param item
     */
    protected void onSelectionItemChanged(String group, final ResourceItemJSO item) {
        DataProvider.getResource(item.getKey(), new AsyncCallback<ResourceDetailJSO>() {
            @Override
            public void onError(Request r, Throwable t) {
                Window.alert(t.getMessage());
            }
            
            @Override
            public void onDownloaded(ResourceDetailJSO result) {                
                mDetailView.setItem(item, result);
            }
        });
    }       
    
    /**
     * Alphabetical comparator handled nulls
     * @author jbruchanov
     *
     */
    private static class ResItemComparator implements Comparator<ResourceItemJSO>{
        @Override
        public int compare(ResourceItemJSO o1, ResourceItemJSO o2) {
            String v1 = o1.getValue();
            String v2 = o2.getValue();
            if (v1 != null && v2 != null) {
                return v1.compareTo(v2);
            } else if (v1 == v2) {
                return 0;
            } else if (v1 != null) {
                return -1;
            } else { // (v2 != null)
                return 1;
            }
        }        
    }
}
