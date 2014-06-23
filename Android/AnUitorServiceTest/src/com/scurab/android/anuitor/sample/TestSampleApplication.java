package com.scurab.android.anuitor.sample;

import com.scurab.android.anuitor.TestHelper;

/**
 * Created by jbruchanov on 23/06/2014.
 */
public class TestSampleApplication extends SampleApplication {

    @Override
    public void onCreate() {
        TestHelper.initMockito(this);
        super.onCreate();
    }
}
