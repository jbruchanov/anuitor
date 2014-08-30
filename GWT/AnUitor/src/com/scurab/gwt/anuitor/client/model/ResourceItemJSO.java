package com.scurab.gwt.anuitor.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class ResourceItemJSO extends JavaScriptObject {

    protected ResourceItemJSO() {

    }

    public final native int getKey()
    /*-{
		return this.Key;
    }-*/;

    public final native String getValue()
    /*-{
		return this.Value;
    }-*/;

    public final native String getValue1()
    /*-{
		return this.Value1 != undefined ? this.Value1 : null;
    }-*/;    
}
