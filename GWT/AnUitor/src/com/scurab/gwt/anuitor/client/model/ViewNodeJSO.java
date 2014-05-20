package com.scurab.gwt.anuitor.client.model;

import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;

/**
 * Base JSObject for intercommunication with device service
 * @author jbruchanov
 *
 */
public class ViewNodeJSO extends JavaScriptObject {

    protected ViewNodeJSO() {

    }

    public final native int getID()
    /*-{
        return this.IDi;
    }-*/;

    public final native int getLevel()
    /*-{
        return this.Level;
    }-*/;

    public final native String getIDName()
    /*-{
        return this.IDs;
    }-*/;

    public final native JsArray<ViewNodeJSO> getNodes()
    /*-{
        return this.Nodes;
    }-*/;

    public final native JavaScriptObject getData()
    /*-{
        return this.Data;
    }-*/;

    public final Set<String> getDataKeys() {
        return new JSONObject(getData()).keySet();
    }
    
    public final native String getDataValueType(String key)
    /*-{
		var obj = this.Data[key];
		if (typeof obj === "undefined")
			return "undefined";
		if (obj === null)
			return "null";
		return Object.prototype.toString.call(obj).match(/^\[object\s(.*)\]$/)[1];
    }-*/;
    
    public final Object getDataValue(String key) {
        String type = getDataValueType(key);
        if ("Number".equals(type)) {
            return getInt(key);
        }else if ("String".equals(type)) {
            return getString(key);
        } else if ("Boolean".equals(type)) {
            return getBoolean(type);
        } else if ("Array".equals(type)) {
            throw new IllegalStateException("Not implemented for Array type");
        } else if ("Object".equals(type)) {
            return getJSDataValue(key);
        } else if ("null".equals(type) || "undefined".equals(type)) {
            return null;
        } else {
            throw new IllegalStateException("Not implemented for type:" + type);
        }
    }

    public final native JavaScriptObject getJSDataValue(String key)
    /*-{
        return this.Data[key];
    }-*/;

    public final native String getString(String key)
    /*-{
        return this.Data[key];
    }-*/;

    public final native int getInt(String key)
    /*-{
        return this.Data[key];
    }-*/;

    public final native float getFloat(String key)
    /*-{
        return this.Data[key];
    }-*/;

    public final native boolean getBoolean(String key)
    /*-{
        return this.Data[key];
    }-*/;
        
    public final String getType(){
        return getString("Type");
    }
    
    public final String getSimpleType(){
        String[] nameFull = getType().split("\\.");
        return nameFull[nameFull.length - 1];
    }

    public final native boolean hasKey(String key)
    /*-{
        return (key in this.Data);
    }-*/;

    /**
     * Just for nice readable representation, because in debug time this doesn't
     * work as usual java object in variables.
     * 
     * @return
     */
    public final String toJsonString() {
        return new JSONObject(this).toString();
    }
}