package com.talkable.sdk.interfaces;

import com.talkable.sdk.api.ApiError;

public interface Callback0 {
    void onSuccess();
    void onError(ApiError e);
}