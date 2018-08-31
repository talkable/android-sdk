package com.talkable.sdk.models;

import com.google.gson.annotations.SerializedName;

public class Offer {
    String email;
    @SerializedName("short_url_code") String code;
    @SerializedName("show_url") String showUrl;
    @SerializedName("claim_url") String claimUrl;

    public String getCode() {
        return code;
    }

    @Deprecated
    public String getShortUrlCode() {
        return code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShowUrl() {
        return showUrl;
    }

    public String getClaimUrl() {
        return claimUrl;
    }
}
