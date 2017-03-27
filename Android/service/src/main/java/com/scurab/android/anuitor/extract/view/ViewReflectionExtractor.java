package com.scurab.android.anuitor.extract.view;

import android.view.View;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.hierarchy.ExportView;

import java.util.HashMap;

/**
 * Created by JBruchanov on 06/02/2016.
 */
public class ViewReflectionExtractor extends ReflectionExtractor {

    public ViewReflectionExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(Object o, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        data = super.fillValues(o, data, contextData);

        View v = (View) o;
        ViewExtractor.fillScale(v, data, contextData);
        ViewExtractor.fillLocationValues(v, data, contextData);

        if (isExportView(v)) {
            ViewExtractor.fillAnnotatedValues(v, data);
        }

        return data;
    }

    private boolean isExportView(View v) {
        return v.getClass().getAnnotation(ExportView.class) != null;
    }
}
