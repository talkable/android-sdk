package com.talkable.sdk.models;

public class AffiliateMember extends Origin {
    public static final String DEFAULT_CAMPAIGN_TAG = "android-invite";

    public AffiliateMember() {
        this(null);
    }

    public AffiliateMember(Customer customer) {
        super();
        this.customer = customer;
        setCampaignTag(DEFAULT_CAMPAIGN_TAG);
    }
}
