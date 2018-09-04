package com.scurab.android.anuitor.tools;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public class CollectionTools {

    public static <T> List<T> toList(SparseArray<T> sparseArray) {
        if (sparseArray == null) {
            return null;
        }
        ArrayList<T> result = new ArrayList<>();
        for (int i = 0, n = sparseArray.size(); i < n; i++) {
            result.add(sparseArray.get(sparseArray.keyAt(i)));
        }
        return result;
    }
}
