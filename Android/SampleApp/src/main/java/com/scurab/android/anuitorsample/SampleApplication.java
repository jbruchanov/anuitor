package com.scurab.android.anuitorsample;

import androidx.multidex.MultiDexApplication;

import com.scurab.android.anuitor.extract2.DetailExtractor;

/**
 * Created by jbruchanov@gmail.com
 *
 * @since 2014-05-15.
 */
public class SampleApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        DetailExtractor.registerRenderArea(DrawOutsideBoundsFragment.HelpTextView.class,
                (view, outRect) -> view.getDrawingSize(outRect));
    }
}
