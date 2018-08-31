package com.talkable.sdk.models;

public class ShareEmail {
    String subject, body;
    Boolean reminder;

    public ShareEmail(String subject, String body, Boolean reminder) {
        this.subject = subject;
        this.body = body;
        this.reminder = reminder;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getReminder() {
        return reminder;
    }

    public void setReminder(Boolean reminder) {
        this.reminder = reminder;
    }
}
