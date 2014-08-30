package com.scurab.android.anuitor.service;

import android.os.Binder;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 10:27
 */
public class AnUitorServiceBinder extends Binder {

    private final AnUitorService mService;

    public AnUitorServiceBinder(AnUitorService service) {
        mService = service;
    }

    public AnUitorService getService() {
        return mService;
    }
}
