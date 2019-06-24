package com.talkable.sdk.models;

import com.google.gson.annotations.SerializedName;
import com.talkable.sdk.interfaces.ApiSendable;

import java.util.HashMap;

public class Origin implements ApiSendable {
    public static final String APP_INSTALL_EVENT_CATEGORY = "android_app_installed";
    public static final String DEFAULT_TRAFFIC_SOURCE = "android";

    public enum Type {
        Event,
        Purchase,
        AffiliateMember
    }

    Integer id;
    Customer customer;
    Type type;
    String[] campaignTags = {};
    String trafficSource;
    @SerializedName("ip_address") String ipAddress = "current";

    protected Origin() {
        trafficSource = DEFAULT_TRAFFIC_SOURCE;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String[] getCampaignTags() {
        return campaignTags;
    }

    public void setCampaignTags(String[] campaignTags) {
        this.campaignTags = campaignTags;
    }

    public String getTrafficSource() {
        return trafficSource;
    }

    public void setTrafficSource(String trafficSource) {
        this.trafficSource = trafficSource;
    }

    public void setCampaignTag(String campaignTag) {
        this.campaignTags = new String[]{campaignTag};
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
