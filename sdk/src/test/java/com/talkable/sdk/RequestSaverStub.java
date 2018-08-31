package com.talkable.sdk;

import com.talkable.sdk.interfaces.RequestSaver;
import com.talkable.sdk.utils.ApiRequest;

import java.util.ArrayList;

public class RequestSaverStub extends RequestSaver {
    protected ArrayList<ApiRequest> entries = new ArrayList<>();

    @Override
    public void save(ApiRequest apiRequest) {
        entries.add(apiRequest);
    }

    @Override
    public ArrayList<ApiRequest> takeEntries() {
        ArrayList<ApiRequest> entries = this.entries;
        this.entries = new ArrayList<>();
        return entries;
    }
}
