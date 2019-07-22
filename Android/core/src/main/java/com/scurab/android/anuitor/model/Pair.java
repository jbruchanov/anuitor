package com.scurab.android.anuitor.model;

/**
 * Created by jbruchanov on 22/05/2014.
 */
public class Pair<K, V> {

    public final K Key;
    public final V Value;

    public Pair(K key, V value) {
        this.Key = key;
        this.Value = value;
    }
}
