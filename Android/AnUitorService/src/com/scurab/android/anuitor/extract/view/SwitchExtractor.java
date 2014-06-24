package com.scurab.android.anuitor.extract.view;


import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.Switch;

import java.util.HashMap;

/**
 * Created by jbruchanov on 06/06/2014.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SwitchExtractor extends CompoundButtonExtractor {

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data,
                                              HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        Switch s = (Switch) v;

        data.put("SwitchMinWidth", s.getSwitchMinWidth());
        data.put("SwitchPadding", s.getSwitchPadding());
        data.put("TextOn", s.getTextOn());
        data.put("TextOff", s.getTextOff());
        data.put("ThumbTextPadding", s.getThumbTextPadding());

        return data;
    }
}
