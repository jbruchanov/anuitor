package com.scurab.android.anuitor.reflect;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by JBruchanov on 23/11/2015.
 */
public class ViewReflector extends ObjectReflector {
    public ViewReflector(View real) {
        super(real);
    }

    public SparseArray<Object> getKeyedTags() {
        return getFieldValue("mKeyedTags");
    }

    @Override
    public <T> T callMethod(String methodName) {
        if ("getKeyedTags".equals(methodName)) {
            return (T) getKeyedTags();
        }
        return super.callMethod(methodName);
    }
}
