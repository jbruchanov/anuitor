package com.scurab.gwt.anuitor.client.model;

import java.util.HashMap;

/**
 * This is just help object to see data directly in debug mode
 * @author jbruchanov
 *
 */
public class ViewNodeJO {

    public int idi;
    public String ids;
    public String type;
    public int level;
    public int position;
    public boolean isLeaf;
    
    public int x;
    public int y;
    public int width;
    public int height;
    
    public String json;

    public HashMap<String, String> data = new HashMap<String, String>();

    public static ViewNodeJO from(ViewNodeJSO obj) {
        ViewNodeJO v = new ViewNodeJO();
        v.idi = obj.getID();
        v.ids = obj.getIDName();
        v.type = obj.getType();
        v.level = obj.getLevel();
        v.position = obj.getPosition();
        v.isLeaf = obj.isLeaf();
        
        v.x = (int)obj.getDouble(ViewFields.LOCATION_SCREEN_X);
        v.y = (int)obj.getDouble(ViewFields.LOCATION_SCREEN_Y);
        v.width = (int)obj.getDouble(ViewFields.WIDTH);
        v.height = (int)obj.getDouble(ViewFields.HEIGHT);
        
        v.json = obj.toJsonString();

        for (String key : obj.getDataKeys()) {
            v.data.put(key, obj.getStringedValue(key));
        }
        return v;
    }
}
