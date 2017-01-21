package com.scurab.android.anuitor.extract.view;

import android.support.v7.widget.GridLayout;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by JBruchanov on 04/02/2016.
 */
public class GridLayoutExtractor extends ViewGroupExtractor {

    public GridLayoutExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        GridLayout gl = (GridLayout) v;

        data.put("AlignmentMode", gl.getAlignmentMode());
        data.put("ColumnCount", gl.getColumnCount());
        data.put("Orientation", getTranslator().gridLayoutOrientation(gl.getOrientation()));
        data.put("Printer", String.valueOf(gl.getPrinter()));
        data.put("RowCount", gl.getRowCount());
        data.put("UseDefaultMargins", gl.getUseDefaultMargins());
        data.put("IsColumnOrderPreserved", gl.isColumnOrderPreserved());
        data.put("IsRowOrderPreserved", gl.isRowOrderPreserved());

        return data;
    }
}
