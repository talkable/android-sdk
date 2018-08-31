package com.talkable.sdk.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.talkable.sdk.utils.JsonUtils;

import java.lang.reflect.Type;

public class EventDeserializer implements JsonDeserializer<Event> {
    @Override
    public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject data = json.getAsJsonObject();
        if (data.has("data")) {
            data = data.getAsJsonObject("data");
        }

        String eventNumber = data.get("subtotal").getAsString();
        String eventCategory = data.get("event_category").getAsString();
        Double subtotal = data.get("subtotal").getAsDouble();
        String[] couponCodes = JsonUtils.unsplitStringElement(data.get("coupon_code"));

        return new Event(eventNumber, eventCategory, subtotal, couponCodes);
    }
}
