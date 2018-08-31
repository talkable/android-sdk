package com.talkable.sdk.utils;

public class TalkableException extends RuntimeException {
    static final long serialVersionUID = 42L;

    public TalkableException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
