package com.scurab.android.anuitor.extract.view;

import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by jbruchanov on 06/06/2014.
 */
public class DrawerLayoutExtractor extends ViewGroupExtractor {

    public DrawerLayoutExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data,
                                              HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        DrawerLayout dl = (DrawerLayout) v;

        Translator translator = getTranslator();
        data.put("DrawerLockModeLeft", translator.drawerLockMode(dl.getDrawerLockMode(Gravity.LEFT)));
        data.put("DrawerLockModeRight", translator.drawerLockMode(dl.getDrawerLockMode(Gravity.RIGHT)));
        data.put("IsDrawerLeftOpen", dl.isDrawerOpen(Gravity.LEFT));
        data.put("IsDrawerRightOpen", dl.isDrawerOpen(Gravity.RIGHT));
        data.put("IsDrawerLeftVisible", dl.isDrawerVisible(Gravity.LEFT));
        data.put("IsDrawerRightVisible", dl.isDrawerVisible(Gravity.RIGHT));

        return data;
    }
}
