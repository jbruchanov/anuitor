package com.scurab.gwt.anuitor.client.model;

import com.github.gwtd3.api.layout.Node;
import com.google.gwt.core.client.JavaScriptObject;
import com.scurab.gwt.anuitor.client.ui.TreeView;

/**
 * Data model class used for {@link TreeView}
 * @author jbruchanov
 *
 */
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
    
    public static final ViewTreeNode createObject() {
        return JavaScriptObject.createObject().cast();
    } 
    
    public final native void setSelected(boolean value) /*-{
        this.selected = value;
    }-*/;

    public final native boolean isSelected() /*-{
        return this.selected === true;
     }-*/;
}