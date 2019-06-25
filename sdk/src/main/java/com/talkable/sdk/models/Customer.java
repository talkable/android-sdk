package com.talkable.sdk.models;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;
import com.talkable.sdk.interfaces.ApiSendable;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Customer implements ApiSendable {
    @SerializedName("customer_id") String id;
    @SerializedName("first_name") String firstName;
    @SerializedName("last_name") String lastName;
    String email, encodedEmail;
    @SerializedName("custom_properties") HashMap<String, String> customProperties;

    public Customer() {

    }

    public Customer(String email) throws UnsupportedEncodingException {
        this(null, null, null, email);
    }

    public Customer(String id, String firstName, String lastName, String email) throws UnsupportedEncodingException {
        this(null, null, null, email, null);
    }

    public Customer(String id, String firstName, String lastName, String email, HashMap<String, String> customProperties) throws UnsupportedEncodingException {
        this.firstName = firstName;

        this.lastName = lastName;
        this.id = id;
        setEmail(email);
        setCustomProperties(customProperties);
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) throws UnsupportedEncodingException {
        this.email = email;
        if (email != null) {
            this.encodedEmail = Base64.encodeToString(email.getBytes("UTF-8"), Base64.DEFAULT);
        }
    }

    public String getEncodedEmail() {
        return encodedEmail;
    }

    public void setEncodedEmail(String encodedEmail) {
        this.encodedEmail = encodedEmail;
    }

    public HashMap<String, String> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(HashMap<String, String> customProperties) {
        this.customProperties = customProperties;
    }
}
