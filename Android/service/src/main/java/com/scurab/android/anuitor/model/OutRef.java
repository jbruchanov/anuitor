package com.scurab.android.anuitor.model;

/**
 * Created by JBruchanov on 22/02/2016.
 */
public class OutRef<T> {

    private T mValue;

    public OutRef() {
    }

    public OutRef(T value) {
        this.mValue = value;
    }

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
    }
}
