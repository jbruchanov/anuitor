package com.scurab.android.anuitorsample;

import android.widget.Button;

import androidx.multidex.MultiDexApplication;

import com.scurab.android.anuitor.extract2.DetailExtractor;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.service.AnUitorClientConfig;
import com.scurab.android.anuitor.service.AnUitorService;

/**
 * Created by jbruchanov@gmail.com
 *
 * @since 2014-05-15.
 */
public class SampleApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        AnUitorService.startService(this, 8081, 0, true, null);
        AnUitorClientConfig.addTypeHighlighting(Button.class, "rgba(255, 0, 255, 0.15)");
        IdsHelper.loadValues(R.class);

        DetailExtractor.registerRenderArea(DrawOutsideBoundsFragment.HelpTextView.class,
                (view, outRect) -> view.getDrawingSize(outRect));
    }
}
