package com.scurab.gwt.anuitor.client.util;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.scurab.gwt.anuitor.client.DataProvider;

public class GenericTools {

    public static String createPropertyHistoryToken(int position, String key, int screenIndex) {
        return new StringBuilder()
        .append("#ViewProperty")
        .append("?").append(DataProvider.QRY_PARAM_SCREEN_INDEX).append("=").append(screenIndex)
        .append("&").append(DataProvider.QRY_PARAM_POSITION).append("=").append(position)
        .append("&").append(DataProvider.QRY_PARAM_PROPERTY).append("=").append(key)       
        .toString();
    }
    
    public static String createPropertyUrl(int position, String key, int screenIndex) {
        return new StringBuilder()
        .append("/viewproperty.json")
        .append("?").append(DataProvider.QRY_PARAM_SCREEN_INDEX).append("=").append(screenIndex)
        .append("&").append(DataProvider.QRY_PARAM_POSITION).append("=").append(position)
        .append("&").append(DataProvider.QRY_PARAM_PROPERTY).append("=").append(key)       
        .toString();
    }
    
    public static String createGroovyHistoryToken(int position, int screenIndex) {
        return new StringBuilder()
        .append("#Groovy")
        .append("?").append(DataProvider.QRY_PARAM_SCREEN_INDEX).append("=").append(screenIndex)
        .append("&").append(DataProvider.QRY_PARAM_POSITION).append("=").append(position)               
        .toString();
    }
    
    public static SafeHtml createColorBlock(String color) {
        return SafeHtmlUtils.fromTrustedString("<span style=\"height:10px;width:50px; margin:0 10px; display:inline-block; border: 1px solid black;\" class=\"transparent\"><span style=\"height:10px; display:block;background:" + color + "\">&nbsp;</span></span>");
    }      
    
    public static SafeHtml createColorCellHighlight(String color) {
        return SafeHtmlUtils.fromTrustedString("<span style=\"position:absolute; left:5px; height:10px;width:10px;background:" + color + "\"></span>&nbsp;");
    }
    
    public static String createGithub(String value) {        
        return "https://github.com/jbruchanov/AnUitor/blob/develop/Android/service/src/main/java/" + value.replaceAll("\\.", "/") + ".java";
    }
    
    public static String createGoogle(String value) {
        return "https://developer.android.com/index.html?q=" + value;
    }
    
    public static String cleanInstanceHash(String value){
        int index = value.indexOf("@");
        if (index > 0) {
            value = value.substring(0, index);
        }
        index = value.indexOf("{");
        if (index > 0) {
            value = value.substring(0, index);
        }
        return value;
    }
    
    public static SafeHtml createLink(String link, String value){
        return SafeHtmlUtils.fromTrustedString("<a href=\"" + link + "\" target=\"_blank\">" + value + "</a>");
    }
}
