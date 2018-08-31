package com.talkable.sdk.models;

import com.talkable.sdk.interfaces.ApiSendable;

public class VisitorOffer implements ApiSendable {
    Integer id;

    public VisitorOffer(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
