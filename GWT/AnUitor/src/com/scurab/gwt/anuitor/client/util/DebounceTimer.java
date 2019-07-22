package com.scurab.gwt.anuitor.client.util;

import com.google.gwt.user.client.Timer;

public class DebounceTimer<T> extends Timer {
    
    public interface Callback<T> {
        void onAction(T action);
    }
    
    private final int mDelay;//ms
    private final Callback<T> mActionListener;
    private T mItem;
    
    public DebounceTimer(Callback<T> callback) {
        this(100, callback);
    }
    
    public DebounceTimer(int delay, Callback<T> callback) {
        mDelay = delay;
        mActionListener = callback;
    }

    @Override
    public void run() {
        mActionListener.onAction(mItem);
    }
    
    public void postAction(T item) {
        cancel();
        mItem = item;
        schedule(mDelay);        
    }
}
