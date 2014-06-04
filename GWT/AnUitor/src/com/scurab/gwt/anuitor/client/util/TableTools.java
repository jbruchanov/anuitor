package com.scurab.gwt.anuitor.client.util;

import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.ListDataProvider;
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

            if (key.startsWith("_") || key.equals(ViewFields.TYPE)) { // ignore  internal fields and type for now
                continue;
            }
            try {
                String value = viewNode.getStringedValue(key);
                list.add(new Pair(key, value));
            } catch (Exception e) {
                sb.append(key).append("\n" + e.getMessage());
            }
        }

        if (sb.length() > 0) {
            Window.alert(sb.toString());
        }
        java.util.Collections.sort(list);

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
        cellTable.getLoadingIndicator().setVisible(false);
        Column<Pair, String> column = new Column<Pair, String>(new TextCell()) {
            @Override
            public String getValue(Pair p) {
                return p.key;
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
        };
        column.setCellStyleNames("tableValue");
        cellTable.addColumn(column, "Value");
        cellTable.setColumnWidth(column, "100%");
    }
}
