package com.scurab.android.uitor.reflect;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Arrays;
import java.util.List;

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

    public boolean isAddToBackStack() {
        return getFieldValue("mAddToBackStack");
    }

    public List<String> getSharedElementSourceNames() {
        return getFieldValue("mSharedElementSourceNames");
    }

    public String getSharedElementSourceNamesValue() {
        final List<String> elems = getSharedElementSourceNames();
        if (elems != null) {
            return Arrays.toString(elems.toArray());
        }
        return null;
    }

    public List<String> getSharedElementTargetNames() {
        return getFieldValue("mSharedElementTargetNames");
    }

    public String getSharedElementTargetNamesValue() {
        final List<String> elems = getSharedElementTargetNames();
        if (elems != null) {
            return Arrays.toString(elems.toArray());
        }
        return null;
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
