package com.scurab.android.anuitor.reflect;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class SupportBackStackEntryReflector extends Reflector<FragmentManager.BackStackEntry> {

    public SupportBackStackEntryReflector(FragmentManager.BackStackEntry real) {
        super(real);
    }

    public int getEnterAnim() {
        return getFieldValue("mEnterAnim");
    }

    public int getExitAnim() {
        return getFieldValue("mExitAnim");
    }

    public int getPopEnterAnim() {
        return getFieldValue("mPopEnterAnim");
    }

    public int getPopExitAnim() {
        return getFieldValue("mPopExitAnim");
    }

    public boolean addToBackStack() {
        return getFieldValue("mAddToBackStack");
    }

    public Object getHead() {
        return getFieldValue("mHead");
    }

    public Object getTail() {
        return getFieldValue("mTail");
    }

    public Fragment getHeadFragment() {
        return new OpReflector(getHead()).getFragment();
    }

    public Fragment getTailFragment() {
        return new OpReflector(getTail()).getFragment();
    }

    private static class OpReflector extends Reflector<Object> {

        protected OpReflector(Object real) {
            super(real);
        }

        Fragment getFragment() {
            return getFieldValue("fragment");
        }
    }
}
