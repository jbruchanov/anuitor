package com.scurab.android.anuitor.extract;

import android.content.Intent;

import java.util.HashMap;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class IntentExtractor extends BaseExtractor<Intent> {

    private BundleExtractor mBundleExtractor = new BundleExtractor();

    @Override
    public HashMap<String, Object> fillValues(Intent intent, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        if (intent != null) {
            data.put("Action", intent.getAction());
            data.put("Categories", intent.getCategories());//TODO as csv ?
            data.put("Data", intent.getData());
            data.put("Flags", intent.getFlags());
            data.put("Scheme", intent.getScheme());
            data.put("Package", intent.getPackage());
            data.put("Type", intent.getType());
            data.put("Component", intent.getComponent().flattenToString());
            data.put("Extras", mBundleExtractor.fillValues(intent.getExtras(), new HashMap<String, Object>(), data));
        }
        return data;
    }
}
