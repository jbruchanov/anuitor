package com.scurab.android.uitor.reflect;

/**
 * Created by JBruchanov on 06/02/2016.
 */
public class ObjectReflector extends Reflector<Object> {
    private static final Object[] EMPTY = new Object[0];

    public ObjectReflector(Object real) {
        super(real);
    }

    public <T> T callMethod(String methodName) {
        return callMethodByReflection(methodName, EMPTY);
    }
}
