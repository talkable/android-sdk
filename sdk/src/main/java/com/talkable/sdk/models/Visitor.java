package com.talkable.sdk.models;

import com.talkable.sdk.interfaces.ApiSendable;

public class Visitor implements ApiSendable {
    String uuid;

    public Visitor() {
    }

    public Visitor(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
