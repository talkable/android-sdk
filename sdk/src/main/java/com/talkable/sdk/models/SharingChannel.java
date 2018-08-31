package com.talkable.sdk.models;

import com.google.gson.annotations.SerializedName;

public enum SharingChannel {
    @SerializedName("facebook")         FACEBOOK("facebook"),
    @SerializedName("facebook_message") FACEBOOK_MESSAGE("facebook_message"),
    @SerializedName("twitter")          TWITTER("twitter"),
    @SerializedName("linkedin")         LINKEDIN("linkedin"),
    @SerializedName("email")            EMAIL("email"),
    @SerializedName("sms")              SMS("sms"),
    @SerializedName("other")            OTHER("other");

    private final String identifier;

    private SharingChannel(String s) {
        identifier = s;
    }

    public boolean equalsIdentifier(String otherIdentifier) {
        return identifier.equals(otherIdentifier);
    }

    public String toString() {
        return this.identifier;
    }
}
