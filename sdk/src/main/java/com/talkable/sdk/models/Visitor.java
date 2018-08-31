package com.talkable.sdk.models;

import com.talkable.sdk.interfaces.ApiSendable;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

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

    public void setUuidFromAndroidId(String androidId) {
        try {
            uuid = UUID.nameUUIDFromBytes(androidId.getBytes("UTF-8")).toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
