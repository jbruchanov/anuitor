package com.scurab.gwt.anuitor.client.model;

public class Pair implements Comparable<Pair>{
    public final String key;
    public final Object value;
    private final String keyCompare;
    public final boolean clickable;
    public Object context;
    public final int position;
    
    public Pair(String key, Object value) {
        this(key, value, false, -1);
    }
    
    public Pair(String key, Object value, boolean clickable, int position) {
        this.key = key;
        this.value = value;
        this.clickable = clickable;
        this.position = position;
        keyCompare = key.toLowerCase();
    }

    @Override
    public int compareTo(Pair o) {            
        return keyCompare.compareTo(o.keyCompare);
    }
    
    public String keyReadable(){
        int i = key.indexOf(":");
        if(i > 0){
            return key.substring(0,  i);
        }
        return key;
    }
}