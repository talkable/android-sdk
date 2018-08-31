package com.talkable.sdk.models;

public class OfferWebData {
    String offerCode;
    String originUrl;
    String html;

    public OfferWebData(String offerCode) {
        this.offerCode = offerCode;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getOfferCode() {
        return offerCode;
    }
}
