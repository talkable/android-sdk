package com.talkable.sdk.utils;

import com.google.gson.JsonElement;

public class ApiRequest {
    private String method;
    private String url;
    private JsonElement body;

    public ApiRequest(String method, String url, JsonElement body) {
        this.method = method;
        this.url = url;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JsonElement getBody() {
        return body;
    }

    public void setBody(JsonElement body) {
        this.body = body;
    }
}
