package com.scurab.gwt.anuitor.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class ResourceDetailJSO extends JavaScriptObject {
    
    protected ResourceDetailJSO() {

    }

    public final native int getID()
    /*-{
        return this.Id;
    }-*/;

    public final native String getName()
    /*-{
        return this.Name;
    }-*/;
    
    public final native String getType()
    /*-{
        return this.Type;
    }-*/;
    
    public final native String getDataType()
    /*-{
        return this.DataType;
    }-*/;
    
    public final native JavaScriptObject getData()
    /*-{
        return this.Data;
    }-*/;
    
    public final native String getStringedData()
    /*-{
        return String(this.Data);
    }-*/;
    
    public final native JavaScriptObject getContext()
    /*-{
        return this.Context;
    }-*/;
    
    public final native String getStringedContext()
    /*-{
        return String(this.Context);
    }-*/;
    
    public final boolean hasDataArray(){
        return "array".equals(getType());
    }
}
