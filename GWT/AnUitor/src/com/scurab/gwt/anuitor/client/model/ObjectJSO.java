package com.scurab.gwt.anuitor.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class ObjectJSO extends JavaScriptObject {

    protected ObjectJSO() {

    }

    public final native JsArrayString getFields()/*-{
		var keys = [];
		for ( var key in this) {
			keys.push(key);
		}
		return keys;
    }-*/;
    
    public final String[] getFieldsAsStrings(){
        JsArrayString fields = getFields();
        int len = fields.length();
        String[] data = new String[len];
        for (int i = 0; i < fields.length(); i++) {
            String key = fields.get(i);
            data[i] = key;
        }
        return data;
    }

    public final native JavaScriptObject getField(String key)/*-{
		return this[key];
    }-*/;
}