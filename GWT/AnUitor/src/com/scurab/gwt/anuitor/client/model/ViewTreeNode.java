package com.scurab.gwt.anuitor.client.model;

import com.github.gwtd3.api.layout.Node;
import com.google.gwt.core.client.JavaScriptObject;

public class ViewTreeNode extends Node {

    protected ViewTreeNode() {
        super();
    }
    
    public final native void addChildren(ViewTreeNode node)/*-{
        if(!this.children){
            this.children = [];
        }
        this.children.push(node);
    }-*/;

    public final native int id() /*-{
		return this.id || -1;
    }-*/;

    public final native int id(int id) /*-{
		return this.id = id;
    }-*/;

    public final native void setAttr(String name, JavaScriptObject value) /*-{
		this[name] = value;
    }-*/;

    public final native double setAttr(String name, double value) /*-{
		return this[name] = value;
    }-*/;

    public final native JavaScriptObject getObjAttr(String name) /*-{
		return this[name];
    }-*/;

    public final native double getNumAttr(String name) /*-{
		return this[name];
    }-*/;

    public final native void setView(ViewNodeJSO view) /*-{
		this.view = view;
    }-*/;    
    
    public final native ViewNodeJSO getView() /*-{
		return this.view;
    }-*/;
    
    public final native void setParent(Node parent) /*-{
        this.parent = parent;
    }-*/;
    
    public final native int getLevelPosition() /*-{
        if(this.levelPosition){
            return this.levelPosition;
        }
        return 0;
    }-*/;

    public final native void setLevelPosition(int position) /*-{
        this.levelPosition = position;
    }-*/;
    
    public static final ViewTreeNode createObject() {
        return JavaScriptObject.createObject().cast();
    }    
}