package com.scurab.android.uitor.reflect;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class FragmentReflector extends Reflector<Object> {

    public FragmentReflector(Object real) {
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

    public boolean hasOptionsMenu() {
        return getFieldValue("mHasMenu");
    }

    public boolean isMenuVisible() {
        return getFieldValue("mMenuVisible");
    }
}
