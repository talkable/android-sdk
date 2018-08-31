package com.talkable.sdk.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class Purchase extends Event {
    public static final String DEFAULT_CAMPAIGN_TAG = "android-post-purchase";

    @SerializedName("order_number") String orderNumber;
    ArrayList<Item> items = new ArrayList<>();

    public Purchase() {
        this(null, null, (String[]) null);
    }

    public Purchase(Double subtotal, String orderNumber, String coupon) {
        this(subtotal, orderNumber, new String[]{coupon});
    }

    public Purchase(Double subtotal, String orderNumber, String[] coupons) {
        super();
        this.subtotal = subtotal;
        this.orderNumber = orderNumber;
        this.couponCodes = coupons;
        setCampaignTag(DEFAULT_CAMPAIGN_TAG);
    }

    public Purchase(Double subtotal, String orderNumber) {
        this(subtotal, orderNumber, (String[]) null);
    }

    @Deprecated
    public Purchase(Double subtotal, String orderNumber, Date orderDate, String coupon) {
        this(subtotal, orderNumber, new String[]{coupon});
    }

    @Deprecated
    public Purchase(Double subtotal, String orderNumber, Date orderDate, String[] coupons) {
        this(subtotal, orderNumber, coupons);
    }

    @Deprecated
    public Purchase(Double subtotal, String orderNumber, Date orderDate) {
        this(subtotal, orderNumber, (String[]) null);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    @Deprecated
    public Date getOrderDate() {
        return null;
    }
}
