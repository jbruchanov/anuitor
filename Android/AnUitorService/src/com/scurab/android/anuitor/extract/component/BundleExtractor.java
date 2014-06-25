package com.scurab.android.anuitor.extract.component;

import android.os.Bundle;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class BundleExtractor extends BaseExtractor<Bundle> {

    public BundleExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(Bundle bundle, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                Object value = bundle.get(key);
                data.put(key, value);
            }
        }
        return data;
    }
}
