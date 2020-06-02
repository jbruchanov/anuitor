package com.scurab.android.uitor.reflect;

public class ObjectReflector extends Reflector<Object> {
    private static final Object[] EMPTY = new Object[0];

    public ObjectReflector(Object real) {
        super(real);
    }

    public <T> T callMethod(String methodName) {
        return callMethodByReflection(methodName, EMPTY);
    }
}
