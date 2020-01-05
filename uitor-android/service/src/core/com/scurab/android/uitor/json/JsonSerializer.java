package com.scurab.android.uitor.json;

/**
 * Simple interface to hide the JSON library behind
 */
public interface JsonSerializer {
    /**
     * Serialize object into json
     * @param o
     * @return
     */
    String toJson(Object o);

    /**
     * Deserialize object from json
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T fromJson(String json, Class<T> clazz);
}
