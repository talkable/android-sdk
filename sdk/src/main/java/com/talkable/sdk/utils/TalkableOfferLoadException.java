package com.talkable.sdk.utils;

public class TalkableOfferLoadException extends TalkableException {
    /** Network error */
    public static final int NETWORK_ERROR = 1000;
    /** Talkable API unavailable */
    public static final int API_ERROR = 1001;
    /** Bad request  */
    public static final int REQUEST_ERROR = 1002;
    /** Campaign not found */
    public static final int CAMPAIGN_ERROR = 1003;

    private int errorCode;

    public TalkableOfferLoadException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
