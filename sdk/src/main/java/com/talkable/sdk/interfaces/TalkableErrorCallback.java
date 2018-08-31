package com.talkable.sdk.interfaces;

import com.talkable.sdk.utils.TalkableException;

public interface TalkableErrorCallback<E extends TalkableException> {
    void onError(E error);
}
