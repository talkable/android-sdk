package com.talkable.sdk.api;

import com.google.gson.JsonObject;

public interface ApiCallback {
    void onSuccess(JsonObject json);

    void onFailure(ApiError e);
}
