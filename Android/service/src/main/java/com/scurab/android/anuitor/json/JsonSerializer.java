package com.scurab.android.anuitor.json;

public interface JsonSerializer {
    String toJson(Object o);

    <T> T fromJson(String json, Class<T> clazz);
}
