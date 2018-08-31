package com.talkable.sdk.interfaces;

import com.talkable.sdk.api.ApiError;

public interface Callback1<T1> {
    void onSuccess(T1 arg1);
    void onError(ApiError e);
}