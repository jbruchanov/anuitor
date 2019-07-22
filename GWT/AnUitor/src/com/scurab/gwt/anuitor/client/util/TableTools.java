package com.scurab.gwt.anuitor.client.util;

import static com.scurab.gwt.anuitor.client.util.GenericTools.cleanInstanceHash;
import static com.scurab.gwt.anuitor.client.util.GenericTools.createColorBlock;
import static com.scurab.gwt.anuitor.client.util.GenericTools.createGithub;
import static com.scurab.gwt.anuitor.client.util.GenericTools.createGoogle;
import static com.scurab.gwt.anuitor.client.util.GenericTools.createGroovyHistoryToken;
import static com.scurab.gwt.anuitor.client.util.GenericTools.createLink;
import static com.scurab.gwt.anuitor.client.util.GenericTools.createPropertyHistoryToken;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;
import com.scurab.gwt.anuitor.client.AnUitor;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.model.FSItemJSO;
import com.scurab.gwt.anuitor.client.model.Pair;
import com.scurab.gwt.anuitor.client.model.ViewFields;
import com.scurab.gwt.anuitor.client.model.ViewNodeJSO;

public final class TableTools {

    public interface Filter<T> {
        boolean accept(T item);
    }
    
    /**
     * Create data model for tableview based on {@link ViewNodeJSO}
     * 
     * @param viewNode
     * @return
     */
    public static ListDataProvider<Pair> createDataProvider(ViewNodeJSO viewNode) {
        return createDataProvider(viewNode, null);
    }
        
    /**
     * Create data model for tableview based on {@link ViewNodeJSO}
     * 
     * @param viewNode
     * @param filter optional filter to filter out some items
     * @return
     */
    public static ListDataProvider<Pair> createDataProvider(ViewNodeJSO viewNode, Filter<Pair> filter) {
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
                final Pair p = new Pair(key, value, clickable, viewNode.getPosition());
                boolean accept = filter == null || filter.accept(p);
                if(accept) { 
                    list.add(p);
                }
            } catch (Exception e) {
                sb.append(key).append("\n" + e.getMessage());
            }
        }

        if (sb.length() > 0) {
            Window.alert(sb.toString());
        }
        java.util.Collections.sort(list);

        moveToFirst(list, "StringValue");
        moveToFirst(list, "Context:");
        moveToFirst(list, "Inheritance");
        list.add(0, new Pair(ViewFields.OWNER, viewNode.getOwner(), true, viewNode.getPosition()));
        if (ConfigHelper.isGroovyEnabled()) {
            list.add(0, new Pair("Groovy Console", viewNode.getPosition()));
        }
        list.add(0, new Pair(ViewFields.POSITION, viewNode.getPosition()));
        list.add(0, new Pair("Level", viewNode.getLevel()));
        list.add(0, new Pair("IDName", viewNode.getIDName()));
        list.add(0, new Pair("ID", viewNode.getID()));
        list.add(0, new Pair(ViewFields.TYPE, viewNode.getStringedValue(ViewFields.TYPE)));

        return dataProvider;
    }   
    
    private static void moveToFirst(List<Pair> list, String keyName) {
        Pair pair = null;
        Iterator<Pair> iterator = list.iterator();
        while (iterator.hasNext()) {
            Pair item = iterator.next();
            if (keyName.equals(item.key)) {
                pair = item;
                iterator.remove();
                break;
            }
        }

        if (pair != null) {
            list.add(0, pair);
        }
        
    }
    
    /**
     * Create data model for tableview based on data
     * 
     * @param data
     * @return
     */
    public static ListDataProvider<Pair> createDataProvider(List<Pair> data) {
        ListDataProvider<Pair> dataProvider = new ListDataProvider<Pair>();
        dataProvider.getList().addAll(data);                       
        return dataProvider;
    }
    
    /**
     * Init table by adding 2columns
     * @param cellTable
     */     
    public static void initTableForPairs(CellTable<Pair> cellTable) {
        initTableForPairs(cellTable, -1);
    }
    
    public static void initTableForPairs(CellTable<Pair> cellTable, final int screenIndex) {
        cellTable.getLoadingIndicator().setVisible(false);
        Column<Pair, String> column = new Column<Pair, String>(new TextCell()) {
            @Override
            public String getValue(Pair p) {
                return p.keyReadable();
            }
            
            @Override
            public void render(Context context, Pair object, SafeHtmlBuilder sb) {
                HTMLColors.appendColorHighglightForCell(AnUitor.getConfig(), object.key, sb);                
                if (object.clickable) {                           
                    String key = object.keyReadable();                    
                    sb.append(createLink(createPropertyHistoryToken(object.position, key, screenIndex), key));
                } else if(ViewFields.POSITION.equals(object.key)) {
                    sb.append(createLink("/view.png" + DataProvider.SCREEN_INDEX_QRY + screenIndex + "&" + DataProvider.QRY_PARAM_POSITION + "=" + object.value, object.key));                                                           
                } else if(ViewFields.GROOVY_CONSOLE.equals(object.key)) {
                    sb.append(createLink(createGroovyHistoryToken((Integer)object.value, screenIndex), object.key));                                                                              
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
                    boolean isInheritance = object.key != null && object.key.contains("Inheritance");
                    String value = (String) object.value;
                    if (isInheritance) {
                        sb.append(SafeHtmlUtils.fromTrustedString(value.replaceAll(">", "&gt;<br/>")));
                        return;
                    } else if (value.startsWith("com.android") || value.startsWith("android.") || value.startsWith("androidx.")) {
                        sb.append(createLink(createGoogle(cleanInstanceHash(value)), value));
                        return;
                    } else if (value.startsWith("com.scurab")
                            && !(value.contains("anuitorsample") || value.endsWith("Extractor"))) {
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
                    } else if(object.clickable && value.length() > 100){
                        sb.append(SafeHtmlUtils.fromString(value.substring(0, 100) + "..."));
                        return;
                    }
                }
                super.render(context, object, sb);
            }
        };
        column.setCellStyleNames("tableValue");
        cellTable.addColumn(column, "Value");
        cellTable.setColumnWidth(column, "100%");
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
