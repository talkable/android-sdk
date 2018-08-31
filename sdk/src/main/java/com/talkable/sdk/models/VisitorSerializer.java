package com.talkable.sdk.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class VisitorSerializer implements JsonSerializer<Visitor> {
    public JsonElement serialize(Visitor src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        JsonObject data = new JsonObject();
        data.addProperty("uuid", src.uuid);
        json.add("data", data);
        return json;
    }
}
