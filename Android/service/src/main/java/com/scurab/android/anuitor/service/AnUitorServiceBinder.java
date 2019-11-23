package com.scurab.android.anuitor.service;

import android.os.Binder;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 10:27
 */
public class AnUitorServiceBinder extends Binder {

    private final AnUitorServiceX mService;

    public AnUitorServiceBinder(AnUitorServiceX service) {
        mService = service;
    }

    public AnUitorServiceX getService() {
        return mService;
    }
}
