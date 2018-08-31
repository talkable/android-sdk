package com.talkable.sdk.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.talkable.sdk.Talkable;
import com.talkable.sdk.TalkablePreferencesStore;

import java.lang.reflect.Type;

public class OriginSerializer implements JsonSerializer<Origin> {

    @Override
    public JsonElement serialize(Origin src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        JsonObject data;
        JsonArray campaignTags;

        json.addProperty("type", src.getClass().getSimpleName());

        if (src.getCustomer() != null) {
            data = context.serialize(src.getCustomer()).getAsJsonObject();
        } else {
            data = new JsonObject();
        }

        data.addProperty("traffic_source", src.getTrafficSource());
        data.addProperty("uuid", TalkablePreferencesStore.getMainUUID());
        data.addProperty("alternative_visitor_uuid", TalkablePreferencesStore.getAlternateUUID());
        data.addProperty("ip_address", src.getIpAddress());

        data.remove("encodedEmail");

        if (src.getCampaignTags() != null) {
            campaignTags = context.serialize(src.getCampaignTags()).getAsJsonArray();
        } else {
            campaignTags = context.serialize(new String[0]).getAsJsonArray();
        }
        data.add("campaign_tags", campaignTags);


        json.add("data", data);

        return json;
    }
}
