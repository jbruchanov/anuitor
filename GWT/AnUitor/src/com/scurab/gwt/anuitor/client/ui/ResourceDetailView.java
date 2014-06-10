package com.scurab.gwt.anuitor.client.ui;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.scurab.gwt.anuitor.client.gwtbootstrap.client.ui.resources.prettify.PrettifyHelper;
import com.scurab.gwt.anuitor.client.model.ResourceDetailJSO;
import com.scurab.gwt.anuitor.client.model.ResourceItemJSO;
import com.scurab.gwt.anuitor.client.util.HTMLColors;

/**
 * Detail view.
 * Generates content based on resources details
 * @author jbruchanov
 *
 */
public class ResourceDetailView extends VerticalPanel {

    private ResourceDetailJSO mItem;

    public void setItem(ResourceItemJSO group, ResourceDetailJSO item) {
        mItem = item;
        clear();
        initWidgets(group);
        initWidgets(item);
    }

    public ResourceDetailJSO getItem() {
        return mItem;
    }

    private void initWidgets(ResourceItemJSO item) {
        if(item == null){
            return;
        }
        addLine("ID:" + item.getKey());
        addLine("Name:" + item.getValue());
        addLine("Source:" + item.getValue1());
        addLine("_______________________________________________________");
        addLine("Data:");
    }
    
    private void initWidgets(ResourceDetailJSO item) {
        if (item == null) {
            addLine("No item!");// TODO: remove it later?
        } else {
            addContext(item);
            String type = item.getDataType();
            
            if ("xml".equals(type)) {
                final PreElement pre = Document.get().createPreElement();
                pre.setInnerSafeHtml(SafeHtmlUtils.fromString(item.getStringedData()));
                //add like this is necessary for proper coloring, otherwise it's b/w
                getElement().appendChild(pre);
                final Widget w = new Widget() {{ setElement(pre); }};
                PrettifyHelper prettifyHelper = new PrettifyHelper(w);
                prettifyHelper.configure();                   
                getElement().removeChild(pre);//remove it now 
                add(w);//and add it as proper widget
            } else if ("string".equals(type) || "boolean".equals(type) || "number".equals(type)) {
                addLine(item.getStringedData());
            } else if ("string[]".equals(type)) {                    
                JsArrayString array = item.getData().cast();                    
                for (int i = 0, n = array.length(); i < n; i++) {
                    addLine(array.get(i));
                }
            } else if("base64_png".equals(type)) {
                Image im = new Image("data:image/png;base64," + item.getStringedData());
                im.setStyleName("transparent");                        
                add(im);
            } else if ("array".equals(type)) {
                JsArray<ResourceDetailJSO> childs = item.getData().cast();
                for (int i = 0, n = childs.length(); i < n; i++) {
                    initWidgets(childs.get(i));
                }                
            } else if("color".equals(type)){
                addLine(item.getStringedData());   
                addColorBox(item.getStringedData());
            } else{
                addLine(item.getStringedData());
            }
        }
    }
    
    /**
     * Add context if it's defined
     * @param item
     */
    private void addContext(ResourceDetailJSO item){
        addLine(item.getStringedContext());        
    }
    
    /**
     * Add line into container with styling
     * @param line
     */
    private void addLine(String line){
        if(line != null){
            Label label = new Label(line);
            label.setStyleName("contextDetailLabel");
            add(label);
        }
    }
    
    /**
     * Add color box (50x200) based on color
     * @param color
     */
    private void addColorBox(String color){        
        final PreElement pre = Document.get().createPreElement();
        pre.setAttribute("style", "height:50px; width:200px; background:url(transparent.png);");
               
        PreElement inner = Document.get().createPreElement();        
        String rgba =  HTMLColors.convertColor(color);
        inner.setAttribute("style", "height:50px; width:200px; background-color: " + rgba);        
        pre.appendChild(inner);
        
        final Widget w = new Widget() {{ setElement(pre); }};        
        add(w);
    }
    
    
}
