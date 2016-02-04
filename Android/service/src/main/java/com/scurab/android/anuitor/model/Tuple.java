package com.scurab.android.anuitor.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jbruchanov on 22/05/2014.
 */
public class Tuple<K, V, A> extends Pair<K, V> {

    @SerializedName("Value1")
    public final A value1;

    public Tuple(K key, V value, A value1) {
        super(key, value);
        this.value1 = value1;
    }
}
