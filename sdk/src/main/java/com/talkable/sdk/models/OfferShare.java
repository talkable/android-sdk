package com.talkable.sdk.models;

import com.google.gson.annotations.SerializedName;
import com.talkable.sdk.interfaces.ApiSendable;

public class OfferShare implements ApiSendable {
    public enum Type {
        EmailOfferShare,
        SocialOfferShare,
        OfferShare
    }

    Integer id;
    Offer offer;
    @SerializedName("channel") SharingChannel sharingChannel;
    String email;
    Type type;
    @SerializedName("short_url") String shortUrl;

    public OfferShare() {

    }

    public OfferShare(Offer offer, SharingChannel sharingChannel) {
        this.offer = offer;
        this.sharingChannel = sharingChannel;
    }

    public OfferShare(Integer id, String email, Type type, String shortUrl) {
        this.id = id;
        this.email = email;
        this.type = type;
        this.shortUrl = shortUrl;
    }

    public Integer getId() {
        return id;
    }

    public Offer getOffer() {
        return offer;
    }

    public SharingChannel getSharingChannel() {
        return sharingChannel;
    }

    public String getEmail() {
        return email;
    }

    public Type getType() {
        return type;
    }

    public String getShortUrl() {
        return shortUrl;
    }
}
