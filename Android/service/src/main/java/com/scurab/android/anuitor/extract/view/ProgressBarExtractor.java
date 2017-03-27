package com.scurab.android.anuitor.extract.view;

import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.widget.ProgressBar;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ProgressBarExtractor extends ViewExtractor {

    public ProgressBarExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        ProgressBar pb = (ProgressBar) v;

        data.put("Max", pb.getMax());
        data.put("Progress", pb.getProgress());
        data.put("ProgressDrawable:", pb.getProgressDrawable());
        data.put("ProgressSecondary", pb.getSecondaryProgress());
        data.put("IsIndeterminate", pb.isIndeterminate());

        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            data.put("IndeterminateTintMode", String.valueOf(pb.getIndeterminateTintMode()));
            data.put("ProgressTintMode", String.valueOf(pb.getProgressTintMode()));
            data.put("ProgressBackgroundTintMode", String.valueOf(pb.getProgressBackgroundTintMode()));
            data.put("SecondaryProgressTintMode", String.valueOf(pb.getSecondaryProgressTintMode()));
        }
        return data;
    }
}
