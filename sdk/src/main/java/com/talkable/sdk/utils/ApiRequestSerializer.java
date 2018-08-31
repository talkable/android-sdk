package com.talkable.sdk.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ApiRequestSerializer implements JsonSerializer<ApiRequest> {
    @Override
    public JsonElement serialize(ApiRequest src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();

        json.addProperty("method", src.getMethod());
        json.addProperty("url", src.getUrl());
        json.add("body", src.getBody());

        return json;
    }
}
