package com.scurab.android.anuitor.extract;

import java.util.HashMap;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public abstract class BaseExtractor<T> {

    public abstract HashMap<String, Object> fillValues(T t, HashMap<String, Object> data, HashMap<String, Object> contextData);
}
