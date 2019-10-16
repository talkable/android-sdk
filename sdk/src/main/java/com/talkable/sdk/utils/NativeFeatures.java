package com.talkable.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.facebook.FacebookSdk;
import com.facebook.messenger.MessengerUtils;
import com.google.gson.JsonObject;
import com.talkable.sdk.BuildConfig;

import java.util.ArrayList;
import java.util.List;

public class NativeFeatures {
    private static JsonObject features;

    public enum Feature {
        SEND_SMS("send_sms"),
        COPY_TO_CLIPBOARD("copy_to_clipboard"),
        SHARE_VIA_NATIVE_EMAIL("share_via_native_mail"),
        SHARE_VIA_TWITTER("share_via_twitter"),
        SHARE_VIA_FACEBOOK("share_via_facebook"),
        SHARE_VIA_FACEBOOK_MESSENGER("share_via_facebook_messenger");

        private final String identifier;

        Feature(String s) {
            identifier = s;
        }

        public boolean equalsIdentifier(String otherIdentifier) {
            return identifier.equals(otherIdentifier);
        }

        public String toString() {
            return this.identifier;
        }
    }

    public static void initialize(Context context) {
        Boolean isSmsAvailable = false;
        Boolean isMessengerInstalled = false;
        Boolean isMailAvailable = false;

        if (context != null) {
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                    ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimState() == TelephonyManager.SIM_STATE_READY) {
                isSmsAvailable = true;
            }

            isMessengerInstalled = MessengerUtils.hasMessengerInstalled(context);

            Intent sendNativeMailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(sendNativeMailIntent, 0);// .isEmpty();
            if (!resolveInfos.isEmpty()) {
                // for some android emulators there is always "com.android.fallback/.Fallback" intent
                // https://stackoverflow.com/a/31052350
                List<ResolveInfo> filtered = new ArrayList<ResolveInfo>();
                for (ResolveInfo info : resolveInfos) {
                    String packageName = info.activityInfo.packageName;
                    if (!packageName.toLowerCase().contains("fallback")) {
                        filtered.add(info);
                    }
                }
                isMailAvailable = !filtered.isEmpty();
            }
        }

        JsonObject json = new JsonObject();
        json.addProperty(String.valueOf(Feature.SEND_SMS), isSmsAvailable);
        json.addProperty(String.valueOf(Feature.COPY_TO_CLIPBOARD), true);
        json.addProperty(String.valueOf(Feature.SHARE_VIA_FACEBOOK), FacebookSdk.isInitialized());
        json.addProperty(String.valueOf(Feature.SHARE_VIA_FACEBOOK_MESSENGER), FacebookSdk.isInitialized() && isMessengerInstalled);
        json.addProperty(String.valueOf(Feature.SHARE_VIA_TWITTER), false);
        json.addProperty(String.valueOf(Feature.SHARE_VIA_NATIVE_EMAIL), isMailAvailable);
        json.addProperty("sdk_version", BuildConfig.VERSION_NAME);
        json.addProperty("sdk_build", BuildConfig.VERSION_CODE);

        features = json;
    }

    public static String getFeatures() {
        return features.toString();
    }

    public static boolean isAvailable(Feature feature) {
        return JsonUtils.getJsonBoolean(features, feature.toString());
    }
}
