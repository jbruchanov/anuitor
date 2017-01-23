package com.scurab.android.anuitor.reflect;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by JBruchanov on 23/11/2015.
 */
public class ViewReflector extends Reflector<View> {
    public ViewReflector(View real) {
        super(real);
    }

    public SparseArray<Object> getKeyedTags() {
        return getFieldValue("mKeyedTags");
    }

    public Object callMethod(String methodName){
        return callMethodByReflection(methodName);
    }

    @Override
    protected <T> T callMethodByReflection(@Nullable String methodName, @NonNull Object... params) {
        if ("getKeyedTags".equals(methodName)) {
            return (T) getKeyedTags();
        }
        return super.callMethodByReflection(methodName, params);
    }

    public int getBackgroundResourceId() {
        return getFieldValue(mReal, "mBackgroundResource");
    }
}
