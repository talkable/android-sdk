package com.talkable.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

public class InstallReferrerReceiver extends BroadcastReceiver {
    private static final String TALKABLE_KEY = "tkbl";
    private static final String UUID_KEY = "uuid";
    private static final String SITE_SLUG_KEY = "site";

    // Testing via terminal:
    // run ./adb shell
    // and put this to it:
    // am broadcast -a com.android.vending.INSTALL_REFERRER -n com.talkable.demo/com.talkable.sdk.InstallReferrerReceiver --es "referrer" "some_string"

    @Override
    public void onReceive(Context context, Intent intent) {
        String referrerData = intent.getStringExtra("referrer");
        if (referrerData != null) {
            Log.i(Talkable.TAG, "Install extra data: " + referrerData);

            UrlQuerySanitizer sanitizer = new UrlQuerySanitizer("http://localhost/?" + referrerData);
            String talkableData = sanitizer.getValue(TALKABLE_KEY);

            if (talkableData != null) {
                try {
                    byte[] data = Base64.decode(talkableData, Base64.DEFAULT);
                    talkableData = new String(data, "UTF-8");
                    Log.i(Talkable.TAG, "Decoded Talkable data: `" + talkableData + "`.");

                    try {
                        JsonObject talkableJson = new JsonParser().parse(talkableData).getAsJsonObject();
                        JsonElement uuidField = talkableJson.get(UUID_KEY);
                        JsonElement siteSlugField = talkableJson.get(SITE_SLUG_KEY);

                        if (siteSlugField != null && !siteSlugField.isJsonNull()) {
                            Talkable.initialize(context, siteSlugField.getAsString());

                            if (uuidField != null && !uuidField.isJsonNull()) {
                                TalkablePreferencesStore.setAlternateUUID(uuidField.getAsString());
                            }

                            Talkable.trackAppInstall();
                        } else {
                            Log.d(Talkable.TAG, "Site slug is not provided");
                        }
                    } catch (JsonSyntaxException e) {
                        Log.d(Talkable.TAG, "incorrect json syntax: `" + talkableData + "`.", e);
                    }
                } catch (UnsupportedEncodingException e) {
                    Log.d(Talkable.TAG, "incorrect json syntax", e);
                }
            } else {
                Log.d(Talkable.TAG, "No Talkable data");
            }
        }
    }
}
