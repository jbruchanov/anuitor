package com.scurab.android.anuitor.sample;

import android.app.Application;
import android.content.Intent;
import com.scurab.android.anuitor.service.AnUitorService;

/**
 * Created by jbruchanov@gmail.com
 *
 * @since 2014-05-15.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Intent i = new Intent(this, AnUitorService.class);
        i.setAction(AnUitorService.START);
        startService(i);
    }
}
