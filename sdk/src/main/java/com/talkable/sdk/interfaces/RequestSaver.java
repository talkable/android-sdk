package com.talkable.sdk.interfaces;

import com.google.gson.JsonElement;
import com.talkable.sdk.utils.ApiRequest;

import java.util.ArrayList;

public abstract class RequestSaver {
    public void save(String method, String url, JsonElement body) {
        save(new ApiRequest(method, url, body));
    }

    public abstract void save(ApiRequest apiRequest);

    public abstract ArrayList<ApiRequest> takeEntries();
}
