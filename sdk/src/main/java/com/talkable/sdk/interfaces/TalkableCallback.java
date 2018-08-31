package com.talkable.sdk.interfaces;

import com.talkable.sdk.utils.TalkableException;

public interface TalkableCallback<RESULT, E extends TalkableException> extends TalkableErrorCallback<E> {
    void onSuccess(RESULT result);
}
