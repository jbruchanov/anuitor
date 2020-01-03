package com.scurab.android.uitor.reflect;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by jbruchanov on 06/06/2014.
 */
public class StateListDrawableReflector extends Reflector<StateListDrawable> {

    public StateListDrawableReflector(StateListDrawable real) {
        super(real);
    }

    public int getStateCount() {
        return (Integer)callByReflection();
    }

    public int[] getStateSet(int index) {
        return callByReflection(index);
    }

    public Drawable getStateDrawable(int index) {
        return callByReflection(index);
    }
}
