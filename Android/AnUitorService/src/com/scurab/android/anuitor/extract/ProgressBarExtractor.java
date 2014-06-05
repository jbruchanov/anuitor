package com.scurab.android.anuitor.extract;

import android.view.View;
import android.widget.ProgressBar;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ProgressBarExtractor extends ViewExtractor {

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        ProgressBar pb = (ProgressBar) v;

        data.put("Max", pb.getMax());
        data.put("Progress", pb.getProgress());
        data.put("ProgressSecondary", pb.getSecondaryProgress());
        data.put("IsIndeterminate", pb.isIndeterminate());

        return data;
    }
}
