package com.scurab.android.anuitor.reflect;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

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

    public List<String> getSharedElementSourceNames() {
        return getFieldValue("mSharedElementSourceNames");
    }

    public List<String> getSharedElementTargetNames() {
        return getFieldValue("mSharedElementTargetNames");
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
