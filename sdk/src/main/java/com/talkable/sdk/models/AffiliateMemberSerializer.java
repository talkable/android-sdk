package com.talkable.sdk.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AffiliateMemberSerializer implements JsonSerializer<AffiliateMember> {
    public JsonElement serialize(AffiliateMember src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src, Origin.class).getAsJsonObject();
    }
}
