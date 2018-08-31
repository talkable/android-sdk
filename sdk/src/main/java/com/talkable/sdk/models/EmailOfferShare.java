package com.talkable.sdk.models;

public class EmailOfferShare extends OfferShare {
    String recipients;
    ShareEmail shareEmail;

    public EmailOfferShare(Offer offer, String recipients, ShareEmail shareEmail) {
        sharingChannel = SharingChannel.EMAIL;
        this.offer = offer;
        this.recipients = recipients;
        this.shareEmail = shareEmail;
    }

    public EmailOfferShare(Integer id, String email, Type type, String shortUrl) {
        super(id, email, type, shortUrl);
    }
}
