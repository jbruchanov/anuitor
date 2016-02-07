package com.scurab.gwt.anuitor.client.util;

import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.model.FSItemJSO;
import com.scurab.gwt.anuitor.client.model.Pair;
import com.scurab.gwt.anuitor.client.model.ViewFields;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

public final class TableTools {

    /**
     * Create data model for tableview based on {@link ViewNodeJSO}
     * 
     * @param viewNode
     * @return
     */
    public static ListDataProvider<Pair> createDataProvider(ViewNodeJSO viewNode) {
        ListDataProvider<Pair> dataProvider = new ListDataProvider<Pair>();
        List<Pair> list = dataProvider.getList();
        Set<String> keys = viewNode.getDataKeys();
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {

            if (key.startsWith("_") || key.equals(ViewFields.TYPE) || key.equals(ViewFields.POSITION)) { // ignore  internal fields and type for now
                continue;
            }
            boolean clickable = key.indexOf(":") > 0;               
            try {
                String value = viewNode.getStringedValue(key);
                clickable &= !"null".equals(value);
                list.add(new Pair(key, value, clickable, viewNode.getPosition()));
            } catch (Exception e) {
                sb.append(key).append("\n" + e.getMessage());
            }
        }

        if (sb.length() > 0) {
            Window.alert(sb.toString());
        }
        java.util.Collections.sort(list);

        list.add(0, new Pair("Position", viewNode.getPosition()));
        list.add(0, new Pair("Level", viewNode.getLevel()));
        list.add(0, new Pair("IDName", viewNode.getIDName()));
        list.add(0, new Pair("ID", viewNode.getID()));
        list.add(0, new Pair(ViewFields.TYPE, viewNode.getStringedValue(ViewFields.TYPE)));

        return dataProvider;
    }
    
    /**
     * Init table by adding 2columns
     * @param cellTable
     */
    public static void initTableForPairs(CellTable<Pair> cellTable) {
        initTableForPairs(cellTable, false);
    }
    
    public static void initTableForPairs(CellTable<Pair> cellTable, boolean clickable) {
        initTableForPairs(cellTable, clickable, -1);
    }
    
    public static void initTableForPairs(CellTable<Pair> cellTable, boolean clickable, final int screenIndex) {
        cellTable.getLoadingIndicator().setVisible(false);
        Column<Pair, String> column = new Column<Pair, String>(new TextCell()) {
            @Override
            public String getValue(Pair p) {
                return p.keyReadable();
            }
            
            @Override
            public void render(Context context, Pair object, SafeHtmlBuilder sb) {
                if (object.clickable) {                           
                    String key = object.keyReadable();                    
                    sb.append(createLink(createUrl(object.position, key, screenIndex), key));
                } else if(ViewFields.POSITION.equals(object.key)) {
                    sb.append(createLink("/view.png" + DataProvider.SCREEN_INDEX_QRY + screenIndex + "&position=" + object.value, object.key));                                                           
                } else {
                    super.render(context, object, sb);                    
                }
            }
        };
        column.setCellStyleNames("tableLabel");
        cellTable.addColumn(column, "Property");
        cellTable.setColumnWidth(column, "200px");

        column = new Column<Pair, String>(new TextCell()) {
            @Override
            public String getValue(Pair object) {
                return String.valueOf(object.value);
            }
            
            @Override
            public void render(Context context, Pair object, SafeHtmlBuilder sb) {
                if (object.value instanceof String) {
                    String value = (String) object.value;                    
                    if (value.startsWith("com.android") || value.startsWith("android.")) {
                        sb.append(createLink(createGoogle(cleanInstanceHash(value)), value));
                        return;
                    } else if (value.startsWith("com.scurab") && !value.contains("anuitorsample")) {
                        sb.append(createLink(createGithub(cleanInstanceHash(value)), value));
                        return;
                    }
                }                
                if (object.value instanceof String) {
                    String value = (String)object.value;
                    if (value.startsWith("#")) {
                        try {                                                        
                            sb.append(createLink("http://hslpicker.com/" + HTMLColors.convertColorForHSLPicker(value), value));
                            String color = HTMLColors.convertColor(value);
                            sb.append(createColorBlock(color));
                            return;
                        } catch (Throwable t) {

                        }
                    }                 
                }
                super.render(context, object, sb);
            }
        };
        column.setCellStyleNames("tableValue");
        cellTable.addColumn(column, "Value");
        cellTable.setColumnWidth(column, "100%");
    }
    
    private static String createUrl(int position, String key, int screenIndex) {
        return new StringBuilder()
        .append("/viewproperty.json")
        .append(DataProvider.SCREEN_INDEX).append(screenIndex)
        .append("&position=").append(position)
        .append("&property=").append(key)
        .toString();
    }
    
    private static SafeHtml createColorBlock(String color) {
        return SafeHtmlUtils.fromTrustedString("<span style=\"height:10px;width:50px; margin:0 10px; display:inline-block; border: 1px solid black;\" class=\"transparent\"><span style=\"height:10px; display:block;background:" + color + "\">&nbsp;</span></span>");
    }      
    
    private static String createGithub(String value) {        
        return "https://github.com/jbruchanov/AnUitor/blob/develop/Android/service/src/main/java/" + value.replaceAll("\\.", "/") + ".java";
    }
    
    private static String createGoogle(String value) {
        return "https://developer.android.com/index.html#q=" + value;
    }
    
    private static String cleanInstanceHash(String value){
        int index = value.indexOf("@");
        if (index > 0) {
            value = value.substring(0, index);
        }
        return value;
    }
    
    private static SafeHtml createLink(String link, String value){
        return SafeHtmlUtils.fromTrustedString("<a href=\"" + link + "\" target=\"_blank\">" + value + "</a>");
    }
    
    /**
     * Init table by adding 2columns
     * @param cellTable
     */
    public static void initTableForFileStorage(CellTable<FSItemJSO> cellTable) {        
        Column<FSItemJSO, String> column = new Column<FSItemJSO, String>(new TextCell()) {
            @Override
            public String getValue(FSItemJSO p) {                
                return p.getName();
            }
        };
        column.setCellStyleNames("tableLabel");
        cellTable.addColumn(column, "Name");
        cellTable.setColumnWidth(column, "500px");

        column = new Column<FSItemJSO, String>(new TextCell()) {
            @Override
            public String getValue(FSItemJSO object) {
                return object.getType() == FSItemJSO.TYPE_FILE ? object.getSize() : "DIR";                
            }
        };
        column.setCellStyleNames("tableValue");
        column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        cellTable.addColumn(column, "Size");        
        cellTable.setColumnWidth(column, "100px");
    }

    /**
     * 
     * @param items
     * @param parentFolder
     * @return
     */
    public static ListDataProvider<FSItemJSO> createDataProvider(JsArray<FSItemJSO> items, boolean addParentFolder) {
        ListDataProvider<FSItemJSO> dataProvider = new ListDataProvider<FSItemJSO>();
        List<FSItemJSO> list = dataProvider.getList();   
        
        for (int i = 0, n = items.length(); i < n; i++) {
            list.add(items.get(i));
        }

        if(addParentFolder){
            FSItemJSO fsi = FSItemJSO.createObject().cast();
            fsi.setName("..");
            fsi.setType(FSItemJSO.TYPE_PARENT_FOLDER);
            list.add(0, fsi);
        }                
        return dataProvider;
    }
}
