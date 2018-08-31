package com.talkable.sdk.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class EmailOfferShareSerializer implements JsonSerializer<EmailOfferShare> {
    public JsonElement serialize(EmailOfferShare src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = context.serialize(src, OfferShare.class).getAsJsonObject();

        json.addProperty("recipients", src.recipients);

        if (src.shareEmail != null) {
            json.add("email", context.serialize(src.shareEmail));
        }

        return json;
    }
}
