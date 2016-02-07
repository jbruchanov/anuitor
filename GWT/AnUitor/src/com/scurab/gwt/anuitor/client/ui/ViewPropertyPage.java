package com.scurab.gwt.anuitor.client.ui;

import java.util.HashMap;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.DataProvider.AsyncCallback;
import com.scurab.gwt.anuitor.client.model.ObjectJSO;
import com.scurab.gwt.anuitor.client.model.ResourceItemJSO;

public class ViewPropertyPage extends Composite {

    private int mScreenIndex;
    
    public ViewPropertyPage(int screenIndex) {
        mScreenIndex = screenIndex;
        loadData();
    }
    
    /**
     * Load list from server
     */
    protected void loadData() {
        DataProvider.getViewProperty(new AsyncCallback<ObjectJSO>() {
            @Override
            public void onError(Request r, Throwable t) {
                Window.alert(t.getMessage());
            }

            @Override
            public void onDownloaded(ObjectJSO result) {
                //HashMap<String, HashMap<String, ResourceItemJSO>> transformed = convertData(result);
                onDataLoaded(null);
            }
        });
    }
    
    protected void onDataLoaded(HashMap<String, HashMap<String, ResourceItemJSO>> transformed) {
        // TODO Auto-generated method stub
        
    }    
}
