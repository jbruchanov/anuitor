package com.scurab.android.anuitorsample;

import android.app.Application;
import android.graphics.Rect;

import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.extract.RenderAreaWrapper;

/**
 * Created by jbruchanov@gmail.com
 *
 * @since 2014-05-15.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DetailExtractor.registerRenderArea(DrawOutsideBoundsFragment.HelpTextView.class,
                (view, outRect) -> view.getDrawingSize(outRect));
    }
}
