package com.scurab.gwt.anuitor.client.model;

public class Pair implements Comparable<Pair>{
    public final String key;
    public final Object value;
    private final String keyCompare;
    
    public Pair(String key, Object value) {
        this.key = key;
        this.value = value;
        keyCompare = key.toLowerCase();
    }

    @Override
    public int compareTo(Pair o) {            
        return keyCompare.compareTo(o.keyCompare);
    }
}