package com.talkable.sdk.api;

import com.google.gson.JsonObject;

public class ApiError {
    String message;
    JsonObject response;

    public ApiError(String _message) {
        this(_message, null);
    }

    public ApiError(String _message, JsonObject _response) {
        message = _message;
        response = _response;
    }

    public String getMessage() {
        return message;
    }

    public JsonObject getResponse() {
        return response;
    }
}
