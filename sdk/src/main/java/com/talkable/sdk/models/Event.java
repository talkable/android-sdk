package com.talkable.sdk.models;

import com.google.gson.annotations.SerializedName;

public class Event extends Origin {
    @SerializedName("event_number") String eventNumber;
    @SerializedName("event_category") String eventCategory;
    @SerializedName("coupon_code") String[] couponCodes;
    @SerializedName("subtotal") Double subtotal;

    public Event(String eventNumber, String eventCategory, Double subtotal, String couponCode) {
        this(eventNumber, eventCategory, subtotal, new String[]{couponCode});
    }

    public Event(String eventNumber, String eventCategory, Double subtotal, String[] couponCodes) {
        super();
        this.eventNumber = eventNumber;
        this.eventCategory = eventCategory;
        this.subtotal = subtotal;
        this.couponCodes = couponCodes;
    }

    public Event(String eventNumber, String eventCategory) {
        this(eventNumber, eventCategory, null, (String[]) null);
    }

    public Event() {
        this(null, null, null, (String[]) null);
    }

    public Double getSubtotal() {
        return subtotal;
    }

    @Deprecated
    public String getCouponCode() {
        if (couponCodes != null && couponCodes.length > 0) {
            return couponCodes[0];
        }
        return null;
    }

    public String[] getCouponCodes() {
        return couponCodes;
    }

    public String getEventNumber() {
        return eventNumber;
    }

    public String getEventCategory() {
        return eventCategory;
    }
}
