package com.talkable.sdk.utils;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.talkable.sdk.models.AffiliateMember;
import com.talkable.sdk.models.AffiliateMemberSerializer;
import com.talkable.sdk.models.EmailOfferShare;
import com.talkable.sdk.models.EmailOfferShareSerializer;
import com.talkable.sdk.models.Event;
import com.talkable.sdk.models.EventDeserializer;
import com.talkable.sdk.models.EventSerializer;
import com.talkable.sdk.models.Origin;
import com.talkable.sdk.models.OriginSerializer;
import com.talkable.sdk.models.Purchase;
import com.talkable.sdk.models.PurchaseDeserializer;
import com.talkable.sdk.models.PurchaseSerializer;
import com.talkable.sdk.models.Visitor;
import com.talkable.sdk.models.VisitorSerializer;

import java.lang.reflect.Type;

// TODO: build a bullet prof json processing [PR-9236]
public class JsonUtils {
    private static Gson gson;

    private static void initialize() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Origin.class, new OriginSerializer());
        gsonBuilder.registerTypeAdapter(Event.class, new EventSerializer());
        gsonBuilder.registerTypeAdapter(Event.class, new EventDeserializer());
        gsonBuilder.registerTypeAdapter(AffiliateMember.class, new AffiliateMemberSerializer());
        gsonBuilder.registerTypeAdapter(Purchase.class, new PurchaseSerializer());
        gsonBuilder.registerTypeAdapter(Purchase.class, new PurchaseDeserializer());
        gsonBuilder.registerTypeAdapter(Visitor.class, new VisitorSerializer());
        gsonBuilder.registerTypeAdapter(EmailOfferShare.class, new EmailOfferShareSerializer());
        gsonBuilder.registerTypeAdapter(ApiRequest.class, new ApiRequestSerializer());

        gsonBuilder.setDateFormat(DateUtils.getFormatString());

        gson = gsonBuilder.create();
    }

    private static boolean isInitialized() {
        return gson != null;
    }

    public static Gson getGson() {
        if (!isInitialized()) {
            initialize();
        }
        return gson;
    }

    public static String toJson(Object src) {
        return getGson().toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return getGson().fromJson(json, classOfT);
    }

    public static <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
        return getGson().fromJson(json, classOfT);
    }

    public static JsonElement toJsonTree(Object src) {
        return getGson().toJsonTree(src);
    }

    public static JsonElement toJsonTree(Object src, Type typeOfSrc) {
        return getGson().toJsonTree(src, typeOfSrc);
    }

    public static Boolean getJsonBoolean(JsonObject json, String key) {
        JsonElement field = json.get(key);
        return !isNull(field) && field.getAsBoolean();
    }

    public static Integer getJsonInt(JsonObject json, String key) {
        JsonElement field = json.get(key);
        if (isNull(field)) {
            return null;
        }
        return field.getAsInt();
    }

    public static String getJsonString(JsonObject json, String key) {
        JsonElement field = json.get(key);
        if (isNull(field)) {
            return null;
        }
        return field.getAsString();
    }

    public static boolean isNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }

    @Nullable
    public static String[] unsplitStringElement(JsonElement json) {
        if (isNull(json) || json.getAsString().isEmpty()) {
            return null;
        }
        return json.getAsString().trim().split(",");
    }
}
