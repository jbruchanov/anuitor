package com.scurab.android.anuitor.json;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

@SuppressWarnings("unused")
public class JacksonSerializer implements JsonSerializer {

    private final ObjectMapper mObjectMapper;

    public JacksonSerializer() {
        this(new ObjectMapper()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS));
    }

    public JacksonSerializer(@NonNull ObjectMapper objectMapper) {
        mObjectMapper = objectMapper;
    }

    @Override
    public String toJson(Object o) {
        try {
            return mObjectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mObjectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
