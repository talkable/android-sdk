package com.talkable.sdk.models;

public class SocialOfferShare extends OfferShare {
    public SocialOfferShare(Integer id, String email, Type type, String shortUrl) {
        super(id, email, type, shortUrl);
    }

    public SocialOfferShare(Offer offer, SharingChannel channel) {
        super(offer, channel);
    }
}
