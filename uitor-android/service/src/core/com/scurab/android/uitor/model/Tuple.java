package com.scurab.android.uitor.model;

public class Tuple<K, V, A> extends Pair<K, V> {

    public final A Value1;

    public Tuple(K key, V value, A value1) {
        super(key, value);
        this.Value1 = value1;
    }
}
