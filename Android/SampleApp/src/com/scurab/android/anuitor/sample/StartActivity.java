package com.scurab.android.anuitor.sample;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.AssetManagerHelper;
import android.content.res.ResourcesHelper;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.TypedValue;

/**
 * Created by jbruchanov on 15.5.14.
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
