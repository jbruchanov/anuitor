package com.scurab.android.anuitor.reflect;

import android.app.Fragment;
import android.app.FragmentManager;
import android.util.SparseArray;

import com.scurab.android.anuitor.tools.CollectionTools;

import java.util.List;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public class FragmentManagerReflector extends Reflector<FragmentManager> {
    public FragmentManagerReflector(FragmentManager real) {
        super(real);
    }

    @SuppressWarnings("unchecked")
    public List<Fragment> getFragments() {
        Object o = getFieldValue("mActive");
        if (o == null) {
            return null;
        } else if (o instanceof List) {
            return (List<Fragment>) o;
        } else if (o instanceof SparseArray) {
            SparseArray<Fragment> sparseArray = (SparseArray<Fragment>) o;
            return CollectionTools.toList(sparseArray);
        } else {
            throw new IllegalStateException(String.format("Invalid object type returned, it's '%s'", o.getClass().getName()));
        }
    }
}
