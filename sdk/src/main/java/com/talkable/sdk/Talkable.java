package com.talkable.sdk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.facebook.FacebookSdk;
import com.facebook.messenger.MessengerUtils;
import com.google.gson.JsonObject;
import com.talkable.sdk.api.ApiError;
import com.talkable.sdk.interfaces.Callback2;
import com.talkable.sdk.interfaces.TalkableCallback;
import com.talkable.sdk.interfaces.TalkableErrorCallback;
import com.talkable.sdk.models.Event;
import com.talkable.sdk.models.Item;
import com.talkable.sdk.models.Offer;
import com.talkable.sdk.models.OfferWebData;
import com.talkable.sdk.models.Origin;
import com.talkable.sdk.models.Purchase;
import com.talkable.sdk.models.VisitorOffer;
import com.talkable.sdk.utils.FacebookUtils;
import com.talkable.sdk.utils.IncorrectInstallationException;
import com.talkable.sdk.utils.ManifestInfo;
import com.talkable.sdk.utils.TalkableOfferLoadException;
import com.talkable.sdk.utils.UriUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

@SuppressWarnings("WeakerAccess")
public class Talkable {
    public static final String USER_AGENT_SUFFIX = "Talkable Android SDK v" + BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE + ".java";
    public static final String TAG = "TKBL";
    public static final String DEFAULT_SERVER = "https://www.talkable.com";

    public static final String UUID_KEY = "talkable_visitor_uuid";
    public static final String VISITOR_OFFER_KEY = "talkable_visitor_offer_id";

    public static final String ARG_OFFER_CODE = "offer_code";
    public static final String ARG_OFFER_FRAGMENT_CLASS = "offer_fragment_class";

    public static final String TALKABLE_FEATURES_HEADER = "X-Talkable-Native-Features";
    public static final String TALKABLE_ERROR_CODE_HEADER = "X-Talkable-Error-Code";
    public static final String TALKABLE_ERROR_MESSAGE_HEADER = "X-Talkable-Error-Message";
    public static final String TALKABLE_OFFER_CODE_HEADER = "X-Talkable-Offer-Code";

    public static final String ERROR_REASON_SITE_NOT_FOUND = "SITE_NOT_FOUND";

    private static OkHttpClient httpClient;
    private static String server, siteSlug, nativeFeatures, defaultUserAgent, debugDeviceId;
    private static Map<String, String> credentialsMap;
    private static Boolean initialized = false, debug = false;

    //----------------+
    // Initialization |
    //----------------+

    public static void initialize(Context context) throws IncorrectInstallationException {
        initialize(context, null);
    }

    public static void initialize(Context context, String initialSiteSlug, boolean debug, String debugDeviceId) throws IncorrectInstallationException {
        Talkable.debug = debug;
        if (debug) {
            Talkable.debugDeviceId = debugDeviceId == null ? UUID.randomUUID().toString() : debugDeviceId;
            Log.d(TAG, "Debug mode engaged. Device ID: " + debugDeviceId);
        }
        initialize(context, initialSiteSlug);
    }

    public static void initialize(Context context, String initialSiteSlug) throws IncorrectInstallationException {
        if (isInitialized()) {
            Log.d(TAG, "Talkable SDK is already initialized");
            return;
        }

        Context applicationContext = context.getApplicationContext();

        if (initialSiteSlug == null) {
            credentialsMap = ManifestInfo.getCredentialsConfiguration(context);
            if (credentialsMap.keySet().size() == 1) {
                initialSiteSlug = credentialsMap.keySet().iterator().next();
            } else {
                initialSiteSlug = ManifestInfo.getDefaultSiteSlug(applicationContext);
            }
            if (initialSiteSlug == null || initialSiteSlug.isEmpty()) {
                throw new IncorrectInstallationException("Default site slug is not specified. " +
                        "Set default site slug inside an element with `" + ManifestInfo.DEFAULT_SITE_SLUG_KEY+
                        "` name");
            }
        }

        Log.d(TAG, "Initializing Talkable SDK with `" + initialSiteSlug + "` site slug");

        loadConfig(applicationContext);
        setSiteSlug(initialSiteSlug);
        setHttpClient(applicationContext);
        initialized = true;

        TalkableApi.initialize(applicationContext);
        TalkablePreferencesStore.initialize(applicationContext);
        if (FacebookSdk.isInitialized()) {
            FacebookUtils.initialize();
        }
        setNativeFeatures(context);
    }

    private static void loadConfig(Context context) {
        server = ManifestInfo.getServer(context);
        if (credentialsMap == null) {
            credentialsMap = ManifestInfo.getCredentialsConfiguration(context);
        }
        ManifestInfo.checkDeepLinkingScheme(context, credentialsMap.keySet());
    }

    private static void setHttpClient(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .build();
        defaultUserAgent = getDefaultUserAgentString(context);
    }

    // https://stackoverflow.com/a/5261472
    private static String getDefaultUserAgentString(Context context) {
        if (Build.VERSION.SDK_INT >= 17) {
            return NewApiWrapper.getDefaultUserAgent(context);
        }

        try {
            Constructor<WebSettings> constructor = WebSettings.class.getDeclaredConstructor(Context.class, WebView.class);
            constructor.setAccessible(true);
            try {
                WebSettings settings = constructor.newInstance(context, null);
                return settings.getUserAgentString();
            } finally {
                constructor.setAccessible(false);
            }
        } catch (Exception e) {
            return new WebView(context).getSettings().getUserAgentString();
        }
    }

    @TargetApi(17)
    private static class NewApiWrapper {
        static String getDefaultUserAgent(Context context) {
            return WebSettings.getDefaultUserAgent(context);
        }
    }

    private static void setNativeFeatures(Context context) {
        Boolean isSmsAvailable = false;
        Boolean isMessengerInstalled = false;

        if (context != null) {
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                    ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimState() == TelephonyManager.SIM_STATE_READY) {
                isSmsAvailable = true;
            }
            isMessengerInstalled = MessengerUtils.hasMessengerInstalled(context);
        }

        Intent sendNativeMailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        Boolean canSendNativeMail = !context.getPackageManager().queryIntentActivities(sendNativeMailIntent, 0).isEmpty();

        JsonObject json = new JsonObject();
        json.addProperty("send_sms", isSmsAvailable);
        json.addProperty("copy_to_clipboard", true);
        json.addProperty("share_via_facebook", FacebookSdk.isInitialized());
        json.addProperty("share_via_facebook_messenger", FacebookSdk.isInitialized() && isMessengerInstalled);
        json.addProperty("share_via_twitter", false);
        json.addProperty("share_via_native_mail", canSendNativeMail);
        json.addProperty("sdk_version", BuildConfig.VERSION_NAME);
        json.addProperty("sdk_build", BuildConfig.VERSION_CODE);

        nativeFeatures = json.toString();
    }

    public static String getNativeFeatures() {
        return nativeFeatures;
    }

    public static OkHttpClient getHttpClient() {
        return httpClient;
    }

    public static String getUserAgent() {
        return defaultUserAgent + ";" + USER_AGENT_SUFFIX;
    }

    //------------+
    // Show Offer |
    //------------+

    @Deprecated
    public static void showOffer(Activity activity, Origin origin) {
        showOffer(activity, origin, TalkableActivity.class, TalkableOfferFragment.class);
    }

    @Deprecated
    public static void showOffer(Activity activity, Origin origin,
                                 Class<? extends FragmentActivity> talkableActivityClass,
                                 Class<? extends TalkableOfferFragment> talkableOfferFragmentClass) {
        showOffer(activity, origin, talkableOfferFragmentClass, null);
    }

    public static void showOffer(final Activity activity, Origin origin, TalkableErrorCallback<TalkableOfferLoadException> callback) {
        showOffer(activity, origin, TalkableOfferFragment.class, callback);
    }

    public static void showOffer(final Activity activity, Origin origin,
                                 final Class<? extends TalkableOfferFragment> talkableOfferFragmentClass,
                                 final TalkableErrorCallback<TalkableOfferLoadException> callback) {
        loadOffer(origin, new TalkableCallback<String, TalkableOfferLoadException>() {
            @Override
            public void onSuccess(String offerCode) {
                Intent intent = new Intent(activity, TalkableActivity.class);
                intent.putExtra(ARG_OFFER_FRAGMENT_CLASS, talkableOfferFragmentClass.getName());
                intent.putExtra(ARG_OFFER_CODE, offerCode);
                activity.startActivity(intent);
            }

            @Override
            public void onError(TalkableOfferLoadException error) {
                if (callback != null) {
                    callback.onError(error);
                }
            }
        });
    }

    public static void loadOffer(final Origin origin, final TalkableCallback<String, TalkableOfferLoadException> callback) {
        if(!Talkable.isInitialized()) {
            throw new IllegalStateException("Talkable SDK is not initialized. " +
                    "Make sure you call Talkable.initialize() from Application class and " +
                    "define Application class name in the manifest");
        }

        if (origin instanceof Purchase) {
            Purchase purchase = (Purchase) origin;
            asyncCreateProductsFromItems(purchase.getItems());
        }

        final String originUrl = UriUtils.offerUri(origin).toString();
        final Request request = new Request.Builder()
                .url(originUrl)
                .header("User-Agent", getUserAgent())
                .header(TALKABLE_FEATURES_HEADER, getNativeFeatures())
                .get()
                .build();

        getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "Offer Loading Error: " + e.getMessage());
                callback.onError(new TalkableOfferLoadException(TalkableOfferLoadException.NETWORK_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() >= 500) {
                    callback.onError(new TalkableOfferLoadException(TalkableOfferLoadException.API_ERROR, "Trouble reaching Talkable servers, please try again later"));
                } else if (response.code() >= 400) {
                    callback.onError(new TalkableOfferLoadException(TalkableOfferLoadException.REQUEST_ERROR, "Request can't be processed"));
                } else {
                    String errorReason = response.header(TALKABLE_ERROR_CODE_HEADER);
                    String offerCode = response.header(TALKABLE_OFFER_CODE_HEADER);

                    if (errorReason != null) {
                        String errorMessage = "";
                        try {
                            errorMessage = new String(Base64.decode(response.header(TALKABLE_ERROR_MESSAGE_HEADER, ""), Base64.DEFAULT));
                        } catch (IllegalArgumentException e) {
                            Log.d(TAG, e.getMessage());
                        }
                        int errorCode = TalkableOfferLoadException.CAMPAIGN_ERROR;
                        if (errorReason.equals(ERROR_REASON_SITE_NOT_FOUND)) {
                            errorCode = TalkableOfferLoadException.REQUEST_ERROR;
                        }
                        callback.onError(new TalkableOfferLoadException(errorCode, errorMessage));
                        return;
                    } else if (offerCode == null) {
                        callback.onError(new TalkableOfferLoadException(TalkableOfferLoadException.NETWORK_ERROR, "Invalid Response"));
                        return;
                    }

                    OfferWebData offerWebData = new OfferWebData(offerCode);
                    offerWebData.setHtml(getOfferHtml(response));
                    offerWebData.setOriginUrl(originUrl);
                    TalkablePreferencesStore.saveOfferWebData(offerWebData);

                    callback.onSuccess(offerCode); // runs on non UI thread
                }
            }

            private String getOfferHtml(Response response) {
                try {
                    return response.body().string(); // flushes after string() called
                } catch (IOException e) {
                    return null;
                }
            }
        });
    }

    private static void asyncCreateProductsFromItems(List<Item> items) {
        if (items == null) return;

        for (Item item : items) {
            if (item.getProductId() == null ||
                    (item.getUrl() == null && item.getImageUrl() == null && item.getTitle() == null)) {
                continue;
            }

            final String productUrl = UriUtils.productUri(item).toString();
            final Request request = new Request.Builder()
                    .url(productUrl)
                    .get()
                    .build();

            getHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "Product creation error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    Log.d(TAG, "Product creation response: " + response.toString());
                }
            });
        }
    }

    //-------------+
    // Credentials |
    //-------------+

    public static String getServer() {
        return server != null ? server : DEFAULT_SERVER;
    }

    public static void setServer(String newServer) {
        // TODO: regenerate new UUID when server changes (after moving from android ID)
        server = newServer;
    }

    public static String getApiKey() {
        return credentialsMap.get(siteSlug);
    }

    public static String getSiteSlug() {
        return siteSlug;
    }

    @Deprecated
    public static void setSiteSlug(Context context, String newSiteSlug) {
        setSiteSlug(newSiteSlug);
    }

    public static void setSiteSlug(String newSiteSlug) {
        if (newSiteSlug == null) {
            throw new IllegalArgumentException("Site slug can not be null");
        }
        if (!credentialsMap.containsKey(newSiteSlug)) {
            throw new IllegalArgumentException("Data for `" + newSiteSlug + "` site slug is not specified " +
                    "inside AndroidManifest file");
        }
        siteSlug = newSiteSlug;
    }

    public static boolean getDebug() {
        return debug;
    }

    public static String getDebugDeviceId() {
        return debugDeviceId;
    }

    public static Boolean isInitialized() {
        return initialized;
    }

    //--------------+
    // App Tracking |
    //--------------+

    public static void trackAppOpen(Activity activity) {
        if(!Talkable.isInitialized()) {
            throw new IllegalStateException("Talkable SDK is not initialized. " +
                    "Make sure you call Talkable.initialize() from Application class and " +
                    "define an application class name in the manifest");
        }

        Uri intentData = activity.getIntent().getData();
        if (intentData == null) {
            return;
        }

        String visitorOfferId = intentData.getQueryParameter("visitor_offer_id");
        if (visitorOfferId == null) {
            return;
        }

        VisitorOffer visitorOffer = new VisitorOffer(Integer.valueOf(visitorOfferId));
        TalkableApi.trackVisit(visitorOffer);
    }

    public static void trackAppInstall() {
        if (!TalkablePreferencesStore.isAppInstallTracked()) {
            return;
        }

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
