package com.talkable.sdk;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.talkable.sdk.api.ApiError;
import com.talkable.sdk.interfaces.Callback2;
import com.talkable.sdk.models.Event;
import com.talkable.sdk.models.Offer;
import com.talkable.sdk.models.Origin;
import com.talkable.sdk.models.VisitorOffer;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.talkable.sdk.Talkable.TAG;
import static com.talkable.sdk.Talkable.UUID_KEY;
import static com.talkable.sdk.Talkable.VISITOR_OFFER_KEY;
import static com.talkable.sdk.utils.ManifestInfo.SCHEME_PREFIX;

public class TalkableDeepLinking {
    public static void track(Map<String, String> params) {
        if(!Talkable.isInitialized()) {
            String errorMessage = "Talkable SDK is not initialized. " +
                    "Make sure you call Talkable.initialize() from Application class and " +
                    "define an application class name in the manifest";
            Log.d(TAG, errorMessage);
            return;
        }
        Log.d(TAG, "Deep linking params: " + params);
        if (params == null) {
            return;
        }
        trackVisit(params.get(VISITOR_OFFER_KEY));
        if (!TalkablePreferencesStore.isAppInstallTracked()) {
            trackAppInstall(params.get(UUID_KEY));
        }
    }

    public static void track(JSONObject params) {
        if (params == null) {
            return;
        }
        Map<String, String> paramsMap = new HashMap<>();
        try {
            if (params.has(VISITOR_OFFER_KEY) && !params.isNull(VISITOR_OFFER_KEY)) {
                paramsMap.put(VISITOR_OFFER_KEY, params.getString(VISITOR_OFFER_KEY));
            }
            if (params.has(UUID_KEY) && !params.isNull(UUID_KEY)) {
                paramsMap.put(UUID_KEY, params.getString(UUID_KEY));
            }
        } catch(JSONException ignored) {}
        track(paramsMap);
    }

    public static void track(Activity activity) {
        if (activity == null) {
            return;
        }
        String scheme = activity.getIntent().getScheme();
        Uri data = activity.getIntent().getData();
        if (data != null && scheme != null && scheme.startsWith(SCHEME_PREFIX)) {
            track(data);
        }
    }

    public static void track(Uri uri) {
        if (uri == null) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        try {
            for (String param : uri.getQueryParameterNames()) {
                params.put(param, uri.getQueryParameter(param));
            }
            track(params);
        } catch (UnsupportedOperationException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private static void trackVisit(String visitorOfferId) {
        try {
            Integer id = Integer.valueOf(visitorOfferId);
            TalkableApi.trackVisit(new VisitorOffer(id));
        } catch (NumberFormatException ignored) {
        }
    }

    private static void trackAppInstall(String uuid) {
        if (uuid == null) {
            return;
        }
        TalkablePreferencesStore.setAlternateUUID(uuid);

        String eventId = TalkablePreferencesStore.getAndroidId();
        Event event = new Event(eventId, Origin.APP_INSTALL_EVENT_CATEGORY);
        TalkableApi.createOrigin(event, new Callback2<Origin, Offer>() {
            @Override
            public void onSuccess(Origin arg1, Offer arg2) {
                TalkablePreferencesStore.trackAppInstalled();
            }

            @Override
            public void onError(ApiError e) {
            }
        });
    }
}
