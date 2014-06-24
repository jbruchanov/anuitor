package com.scurab.android.anuitor.reflect;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.List;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class FragmentManagerReflector extends Reflector<FragmentManager> {
    public FragmentManagerReflector(FragmentManager real) {
        super(real);
    }

    public List<Fragment> getFragments() {
        return getFieldValue("mActive");
    }
}
