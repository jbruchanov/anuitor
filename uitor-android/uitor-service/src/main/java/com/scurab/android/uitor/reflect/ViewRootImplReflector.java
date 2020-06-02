package com.scurab.android.uitor.reflect;

import android.view.View;

public class ViewRootImplReflector extends Reflector<Object> {

    public ViewRootImplReflector(Object real) {
        super(real);
    }

    public View getView() {
        return callByReflection();
    }
}
