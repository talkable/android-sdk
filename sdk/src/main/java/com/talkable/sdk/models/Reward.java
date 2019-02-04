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

    public String getReason() {
        return reason;
    }

    public String getIncentiveType() {
        return incentiveType;
    }

    public String getIncentiveDescription() {
        return incentiveDescription;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public Status getStatus() {
        return status;
    }
}
