package com.talkable.sdk;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.talkable.sdk.api.ApiError;
import com.talkable.sdk.interfaces.Callback1;
import com.talkable.sdk.models.OfferWebData;
import com.talkable.sdk.models.Visitor;
import com.talkable.sdk.utils.PreferencesStore;

import java.util.Set;
import java.util.UUID;

public class TalkablePreferencesStore {
    private static final String SHARED_PREFERENCES_NAME = "TKBL_PREFERENCES";
    private static final String MAIN_UUID_KEY = "TKBL_MAIN_UUID";
    private static final String ALTERNATE_UUID_KEY = "TKBL_ALTERNATE_UUID";
    private static final String APP_INSTALLED_KEY = "TKBL_APP_INSTALLED";
    private static final String OFFER_HTML_KEY = "TKBL_OFFER_HTML";
    private static final String OFFER_URL_KEY = "TKBL_OFFER_URL";
    private static final String SAVED_OFFERS_KEY = "CACHED_OFFERS";

    private static PreferencesStore preferencesStore;
    private static String mainUUID, alternateUUID;
    private static String androidId;

    public static void initialize(Context context) {
        if (isInitialized()) {
            return;
        }
        setPreferencesStore(context);
        if (Talkable.getDebug()) {
            preferencesStore.putBoolean(APP_INSTALLED_KEY, false);
            setAndroidId(Talkable.getDebugDeviceId());
        } else {
            setAndroidId(context);
        }
        cleanupOfferWebData();
        if (Talkable.getDebug() || getMainUUID() == null) {
            generateMainUuid();
        }
    }

    private static void cleanupOfferWebData() {
        Set<String> savedOfferIds = preferencesStore.getStringSet(SAVED_OFFERS_KEY);
        for (String offerId: savedOfferIds) {
            preferencesStore.remove(offerId);
        }
        preferencesStore.remove(SAVED_OFFERS_KEY);
    }

    public static Boolean isInitialized() {
        return preferencesStore != null;
    }

    private static void setPreferencesStore(Context context) {
        preferencesStore = new PreferencesStore(context, SHARED_PREFERENCES_NAME);
    }

    // TODO: remove android ID usage and use an analog of IOS KeyChain
    private static void setAndroidId(Context context) {
        androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static void setAndroidId(String customAndroidId) {
        androidId = customAndroidId;
    }

    public static String getAndroidId() {
        return androidId;
    }

    private static void generateMainUuid() {
        Visitor visitor = new Visitor();
        visitor.setUuidFromAndroidId(getAndroidId());

        TalkableApi.createVisitor(visitor, new Callback1<Visitor>() {
            @Override
            public void onSuccess(Visitor arg1) {

            }

            @Override
            public void onError(ApiError error) {
                String message = error.getMessage();
                if (message != null) {
                    Log.d(Talkable.TAG, message);
                }
            }
        });

        mainUUID = visitor.getUuid();
        preferencesStore.putString(MAIN_UUID_KEY, visitor.getUuid());
    }

    public static String getMainUUID() {
        if (mainUUID != null) {
            return mainUUID;
        }
        return preferencesStore.getString(MAIN_UUID_KEY);
    }

    public static void setAlternateUUID(String alternateUUID) {
        TalkablePreferencesStore.alternateUUID = alternateUUID;
        preferencesStore.putString(ALTERNATE_UUID_KEY, alternateUUID);
    }

    public static String getAlternateUUID() {
        if (alternateUUID != null) {
            return alternateUUID;
        }
        return preferencesStore.getString(ALTERNATE_UUID_KEY);
    }

    public static Boolean isAppInstallTracked() {
        return preferencesStore.getBoolean(APP_INSTALLED_KEY);
    }

    public static void trackAppInstalled() {
        preferencesStore.putBoolean(APP_INSTALLED_KEY, true);
    }

    public static void saveOfferWebData(OfferWebData offerWebData) {
        String offerCode = offerWebData.getOfferCode();
        preferencesStore.appendStringSet(SAVED_OFFERS_KEY, offerCode);
        preferencesStore.putString(keyOfferWebData(OFFER_HTML_KEY, offerCode), offerWebData.getHtml());
        preferencesStore.putString(keyOfferWebData(OFFER_URL_KEY, offerCode), offerWebData.getOriginUrl());
    }

    public static OfferWebData getOfferWebData(String offerCode) {
        String html = preferencesStore.getString(keyOfferWebData(OFFER_HTML_KEY, offerCode));
        String originUrl = preferencesStore.getString(keyOfferWebData(OFFER_URL_KEY, offerCode));

        OfferWebData offerWebData = new OfferWebData(offerCode);
        offerWebData.setHtml(html);
        offerWebData.setOriginUrl(originUrl);

        return offerWebData;
    }

    private static String keyOfferWebData(String dataKey, String offerCode) {
        return dataKey + offerCode;
    }
}
