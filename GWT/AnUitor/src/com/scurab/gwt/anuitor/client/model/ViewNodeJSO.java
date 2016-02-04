package com.scurab.gwt.anuitor.client.model;

import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import com.scurab.gwt.anuitor.client.model.ViewNodeHelper.HasNodes;

/**
 * Base JSObject for intercommunication with device service
 * 
 * @author jbruchanov
 * 
 */
public class ViewNodeJSO extends JavaScriptObject implements HasNodes<ViewNodeJSO> {

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
            return getDouble(key);
        } else if ("String".equals(type)) {
            return getString(key);
        } else if ("Boolean".equals(type)) {
            // TODO: check why do i have to convert to string and then parse it,
            // json looks fine
            return getBoolean(key);
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

    public final native double getDouble(String key)
    /*-{
		return this.Data[key];
    }-*/;

    public final double getWidth() {
        return getWidth(true);
    }
    
    public final double getHeight() {
        return getHeight(true);
    }
    
    public final double getLeft() {
        return getLeft(true);
    }
    
    public final double getTop() {
        return getTop(true);
    }
    
    public final double getWidth(boolean render) {
        return hasCustomRenderSize() && render ? getRenderWidth() : getDouble(ViewFields.WIDTH);
    }
    
    public final double getHeight(boolean render) {
        return hasCustomRenderSize() && render ? getRenderWidth() : getDouble(ViewFields.HEIGHT);
    }
    
    public final double getLeft(boolean render) {
        return getDouble(ViewFields.LOCATION_SCREEN_X) + (render ? getRenderAreaPosition(0) : 0);
    }
    
    public final double getTop(boolean render) {
        return getDouble(ViewFields.LOCATION_SCREEN_Y) + (render ? getRenderAreaPosition(1) : 0);
    }
    
    public final boolean getBoolean(String key) {
        // not sure why native doesnt work :(
        String v = getStringedValue(key);
        return Boolean.parseBoolean(v);
    }

    // doesnt work
    private final native Boolean _getBoolean(String key)
    /*-{
		return (this.Data[key] === true);
    }-*/;

    public final native String getStringedValue(String key)
    /*-{
		return String(this.Data[key]);
    }-*/;

    public final String getType() {
        return getString("Type");
    }

    public final String getSimpleType() {
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

    public final native int getPosition()
    /*-{
		return this.Position;
    }-*/;

    public final native boolean isLeaf() /*-{
		return this.Nodes == null || this.Nodes.length == 0;
    }-*/;

    public final boolean shouldRender() {
        return getBoolean("_RenderViewContent");
    }
    
    public final boolean hasCustomRenderSize(){
        return hasKey(ViewFields.Internal.RENDER_AREA_RELATIVE);
    }
    
    private final int getRenderAreaPosition(int index) {
        if(hasCustomRenderSize()){
            return Integer.parseInt(getString(ViewFields.Internal.RENDER_AREA_RELATIVE).split(",")[index]);
        }
        return 0;
    }
    
    private final int getRenderWidth() {
        return getRenderWidthHeight(0, 2);
    }
    
    private final int getRenderHeight() {
        return getRenderWidthHeight(1, 3);
    }
    
    private final int getRenderWidthHeight(int i1, int i2) {
        if(hasCustomRenderSize()){
            String[] vals = getString(ViewFields.Internal.RENDER_AREA_RELATIVE).split(",");
            return Integer.parseInt(vals[i2]) - Integer.parseInt(vals[i1]);
        }
        return 0;
    }
}