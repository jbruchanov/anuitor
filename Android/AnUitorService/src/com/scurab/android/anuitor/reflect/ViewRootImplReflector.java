package com.scurab.android.anuitor.reflect;

import android.view.View;

/**
 * Created by jbruchanov on 04/07/2014.
 */
public class ViewRootImplReflector extends Reflector<Object> {

    public ViewRootImplReflector(Object real) {
        super(real);
    }

    public View getView() {
        return callByReflection();
    }
}
