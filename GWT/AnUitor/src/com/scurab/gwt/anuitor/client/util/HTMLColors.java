package com.scurab.gwt.anuitor.client.util;

import com.google.gwt.canvas.dom.client.ImageData;

public final class HTMLColors {

    public static final String TRANSPARENT = "rgba(0,0,0,0)";
    
    public static final String WHITE =      "#FFFFFF";
    public static final String BLACK =      "#000000";
    public static final String RED =        "#FF0000";
    public static final String GREEN =      "#00FF00";
    public static final String BLUE =       "#0000FF";       
    
    public static final String CYAN =       "#00FFFF";
    public static final String MAGENTA =    "#FF00FF";
    public static final String YELLOW =     "#FFFF00";


    /**
     * Format int value for hex color 
     * @param value
     * @return
     */
    public static String addZeroPrefixIfNecessary(int value) {        
        String v = Integer.toHexString(value).toUpperCase();
        return v.length() == 1 ? "0" + v : v;
    }

    /**
     * Return color of pixel at [0,0]
     * @param data
     * @return
     */
    public static String getColorFromImageData(ImageData data){
        return getColorFromImageData(data, 0, 0);
    }
    
    /**
     * Return color of pixel on specific position
     * @param data
     * @param x
     * @param y
     * @return
     */
    public static String getColorFromImageData(ImageData data, int x, int y){
        return "#" + addZeroPrefixIfNecessary(data.getAlphaAt(x, y)) 
                           + addZeroPrefixIfNecessary(data.getRedAt(x, y))
                           + addZeroPrefixIfNecessary(data.getGreenAt(x, y)) 
                           + addZeroPrefixIfNecessary(data.getBlueAt(x, y));
    }
}
