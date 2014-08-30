package com.scurab.android.anuitor.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jbruchanov on 22/05/2014.
 */
public class Pair<K, V> {

    @SerializedName("Key")
    final K key;

    @SerializedName("Value")
    final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
