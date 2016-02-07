package com.scurab.gwt.anuitor.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class DataResponseJSO extends ObjectJSO {
    
    protected DataResponseJSO() {

    }
    
    public final native String getData()
    /*-{
        return this.Data;
    }-*/;
    
    public final native String getDataType()
    /*-{
        return this.DataType;
    }-*/;
    
    public final native JavaScriptObject getContext()
    /*-{
        return this.Context;
    }-*/;
    
    public final native String getStringedContext()
    /*-{
        return this.Context == undefined ? null : String(this.Context);
    }-*/;
}
