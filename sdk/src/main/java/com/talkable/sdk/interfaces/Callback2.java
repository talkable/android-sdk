package com.talkable.sdk.interfaces;

import com.talkable.sdk.api.ApiError;

public interface Callback2<T1, T2> {
    void onSuccess(T1 arg1, T2 arg2);
    void onError(ApiError e);
}