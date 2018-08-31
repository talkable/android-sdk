package com.talkable.sdk.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.talkable.sdk.utils.DateUtils;
import com.talkable.sdk.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.Date;

public class PurchaseDeserializer implements JsonDeserializer<Purchase> {
    @Override
    public Purchase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject data = json.getAsJsonObject();
        if (data.has("data")) {
            data = data.getAsJsonObject("data");
        }

        Double subtotal = data.get("subtotal").getAsDouble();
        String orderNumber = data.get("order_number").getAsString();
        String[] coupons = JsonUtils.unsplitStringElement(data.get("coupon_code"));

        return new Purchase(subtotal, orderNumber, coupons);
    }
}
