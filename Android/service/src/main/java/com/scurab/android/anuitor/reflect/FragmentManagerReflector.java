package com.scurab.android.anuitor.reflect;

import android.app.Fragment;
import android.app.FragmentManager;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
        final Object active = getFieldValue("mActive");
        if (active instanceof List) {
            return (List<Fragment>) active;
        } else if (active instanceof SparseArray) {
            return convert(((SparseArray) active));
        }
        throw new IllegalStateException(String.format("Unsupported type %s of 'mActive' field", active.getClass().getName()));
    }

    private <T> List<T> convert(SparseArray<T> src) {
        if (src == null) {
            return null;
        }
        List<T> result = new ArrayList<>();
        for (int i = 0, n = src.size(); i < n; i++) {
            result.add(src.get(src.keyAt(i)));
        }
        return result;
    }
}
