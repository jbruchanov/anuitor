package com.scurab.android.uitor.reflect;

import android.content.res.ColorStateList;

/**
 * Created by jbruchanov on 06/06/2014.
 */
public class ColorStateListReflector extends Reflector<ColorStateList> {

    public ColorStateListReflector(ColorStateList real) {
        super(real);
    }

    public int getStateCount() {
        return getStateSpecs().length;
    }

    public int[] getColorState(int index) {
        return getStateSpecs()[index];
    }

    private int[][] getStateSpecs() {
        return getFieldValue("mStateSpecs");
    }
}
