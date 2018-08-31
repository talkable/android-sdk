package com.talkable.sdk.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class PurchaseSerializer implements JsonSerializer<Purchase> {
    public JsonElement serialize(Purchase src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = context.serialize(src, Origin.class).getAsJsonObject();

        JsonObject data = json.get("data").getAsJsonObject();

        data.addProperty("subtotal", src.subtotal);
        data.addProperty("order_number", src.orderNumber);

        JsonArray coupons;
        if (src.getCouponCodes() != null) {
            coupons = context.serialize(src.getCouponCodes()).getAsJsonArray();
        } else {
            coupons = context.serialize(new String[0]).getAsJsonArray();
        }
        data.add("coupon_code", coupons);

        JsonArray items = new JsonArray();
        data.add("items", items);
        for (Item item : src.items) {
            items.add(context.serialize(item));
        }

        return json;
    }
}
