package com.scurab.android.anuitor.reflect;

import android.support.v4.app.Fragment;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class SupportFragmentReflector extends Reflector<Fragment> {

    public SupportFragmentReflector(Fragment real) {
        super(real);
    }

    public int getState() {
        return getFieldValue("mState");
    }

    public String getWho() {
        return getFieldValue("mWho");
    }

    public int getIndex() {
        return getFieldValue("mIndex");
    }
}
