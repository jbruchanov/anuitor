package com.scurab.android.anuitor.json;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
}
