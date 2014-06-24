package com.scurab.android.anuitor.extract.view;


import android.view.View;
import android.widget.SeekBar;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class AbsSeekBarExtractor extends ProgressBarExtractor {

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        SeekBar sb = (SeekBar) v;
        data.put("ThumbOffset", sb.getThumbOffset());

        return data;
    }
}
