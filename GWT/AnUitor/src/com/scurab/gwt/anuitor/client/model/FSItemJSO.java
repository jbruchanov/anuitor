package com.scurab.gwt.anuitor.client.model;

import com.google.gwt.core.client.JavaScriptObject;

public class FSItemJSO extends JavaScriptObject {
    public static final int TYPE_PARENT_FOLDER = -1;
    public static final int TYPE_FILE = 1;
    public static final int TYPE_FOLDER = 2;

    protected FSItemJSO() {

    }

    public final native String getName()/*-{
		return this.Name;
    }-*/;

    public final native String getSize()/*-{
		return this.Size == undefined ? 0 : this.Size;
    }-*/;

    public final native int getType()/*-{
		return this.Type;
    }-*/;

    public final native void setName(String name)/*-{
		this.Name = name;
    }-*/;

    public final native void setType(int type)/*-{
		return this.Type = type;
    }-*/;
    
    public final native void setRootFolder()/*-{
        this.IsRootFolder = isRootFolder;
    }-*/;
    
    public final native boolean isRootFolder()/*-{
        return this.IsRootFolder != undefined && this.IsRootFolder == true;
    }-*/;
}
