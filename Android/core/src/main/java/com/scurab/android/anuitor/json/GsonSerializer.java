package com.scurab.android.anuitor.json;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("unused")
public class GsonSerializer implements JsonSerializer {

    private final Gson mGson;

    public GsonSerializer() {
        this(new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .create());
    }

    public GsonSerializer(@NonNull Gson gson) {
        mGson = gson;
    }

    @Override
    public String toJson(Object o) {
        return mGson.toJson(o);
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        return mGson.fromJson(json, clazz);
    }
}
