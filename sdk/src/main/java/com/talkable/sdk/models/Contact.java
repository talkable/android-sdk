package com.talkable.sdk.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Contact {
    @SerializedName("first_name") String name;
    @SerializedName("phone_number") ArrayList<String> phoneNumbers = new ArrayList<>();
    @SerializedName("email") ArrayList<String> emails = new ArrayList<>();

    public Contact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }
}
