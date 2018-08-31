package com.talkable.sdk.models;

import com.google.gson.annotations.SerializedName;

public class Reward {
    public enum Status {
        Paid,
        Unpaid,
        Voided
    }

    Integer id;
    String reason;
    @SerializedName("incentive_type") String incentiveType;
    @SerializedName("incentive_description") String incentiveDescription;
    Double amount;
    @SerializedName("coupon_code") String couponCode;
    Status status;
}
