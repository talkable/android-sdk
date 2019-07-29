package com.talkable.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.facebook.FacebookSdk;
import com.google.gson.JsonObject;
import com.talkable.sdk.models.Contact;
import com.talkable.sdk.models.OfferWebData;
import com.talkable.sdk.models.SharingChannel;
import com.talkable.sdk.utils.ContactsImporter;
import com.talkable.sdk.utils.FacebookUtils;
import com.talkable.sdk.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

public class TalkableOfferFragment extends Fragment {
    public static final String OFFER_CODE_PARAM = "offer_code_param";
    public static final int REQUEST_CODE_SEND_SMS = 1;
    public static final int REQUEST_CODE_READ_CONTACTS = 2;
    public static final int REQUEST_CODE_SEND_NATIVE_MAIL = 3;

    private WebView mWebView;
    private WebAppInterface mWebAppInterface;
    private String mOfferCode;
    private boolean mHasOpenedPopups = false;

    private TalkableOfferFragmentListener mListener;

    public TalkableOfferFragment() {}

    public static TalkableOfferFragment newInstance(String offerCode) {
        TalkableOfferFragment offerFragment = new TalkableOfferFragment();

        Bundle args = new Bundle();
        args.putString(OFFER_CODE_PARAM, offerCode);
        offerFragment.setArguments(args);

        return offerFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TalkableOfferFragmentListener) {
            mListener = (TalkableOfferFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Talkable.isInitialized()) {
            throw new IllegalStateException("Talkable SDK is not initialized. " +
                    "Make sure you call Talkable.initialize() from Application class and " +
                    "define Application class name in the manifest");
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            mOfferCode = arguments.getString(OFFER_CODE_PARAM);
        }

        if (mOfferCode == null) {
            throw new IllegalStateException("Offer code should be provided. Make sure " +
                    "you pass an offer code to the fragment as TalkableOfferFragment.OFFER_CODE_PARAM argument");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talkable, container, false);
        mWebView = view.findViewById(R.id.webView);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeWebView();

        OfferWebData offerWebData = TalkablePreferencesStore.getOfferWebData(mOfferCode);
        mWebView.loadDataWithBaseURL(offerWebData.getOriginUrl(), offerWebData.getHtml(), "text/html", "utf-8", null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (FacebookSdk.isInitialized()) {
            FacebookUtils.onActivityResult(requestCode, resultCode, data);
        }

        if (mWebAppInterface == null) return;

        if (requestCode == REQUEST_CODE_SEND_SMS) { // resultCode always RESULT_CANCELED for SMS sharing
            shareSucceeded(SharingChannel.SMS.toString());
        }

        if (requestCode == REQUEST_CODE_SEND_NATIVE_MAIL && resultCode == Activity.RESULT_OK) {
            shareSucceeded(SharingChannel.NATIVE_MAIL.toString());
        }
    }

    public boolean onBackPressed() {
        if (!mHasOpenedPopups) {
            return false;
        } else {
            publishToWebView("close_popups", null);
            mHasOpenedPopups = false;
            return true;
        }
    }

    //----------+
    // Web view |
    //----------+

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initializeWebView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url, getTalkableHeaders());
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString(), getTalkableHeaders());
                return true;
            }
        });

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUserAgentString(Talkable.getUserAgent());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (getActivity() != null &&
                    (getActivity().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        mWebAppInterface = new WebAppInterface(this);
        mWebView.addJavascriptInterface(mWebAppInterface, "TalkableAndroidSDK");
    }

    //---------------+
    // Web interface |
    //---------------+

    private void publishToWebView(String name, Object jsonData) {
        callWebViewMethod("publish", name, jsonData);
    }

    private void callWebViewMethod(String method, Object... params) {
        if (mWebView == null) return;

        String[] serializedParams = new String[params.length];
        for (int i = 0, l = params.length; i < l; i++) {
            serializedParams[i] = JsonUtils.toJson(params[i]);
        }

        final String js = "Talkable." + method + "(" + TextUtils.join(", ", serializedParams) + ")";
        Log.d(Talkable.TAG, "Javascript: " + js);

        mWebView.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(js, null);
                } else {
                    mWebView.loadUrl("javascript:" + js);
                }
            }
        });
    }

    public void shareSucceeded(String channel) {
        callWebViewMethod("shareSucceeded", channel);
    }

    //-----------+
    // Callbacks |
    //-----------+

    public void shareOfferViaNativeMail(String subject, String message, String claimUrl) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        if (subject != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (message != null) {
            intent.putExtra(Intent.EXTRA_TEXT, message);
        }
        startActivityForResult(intent, REQUEST_CODE_SEND_NATIVE_MAIL);
    }

    public void shareOfferViaSms(String recipients, String message, String claimUrl) {
        String uriString = "sms:";
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uriString));
        if (message != null && claimUrl != null && !message.contains(claimUrl)) {
            message += " " + claimUrl;
        }
        intent.putExtra("sms_body", message);
        startActivityForResult(intent, REQUEST_CODE_SEND_SMS);
    }

    public void importContacts() {
        if (getActivity() == null) return;

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            publishImportedContacts();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    publishImportedContacts();
                } else {
                    Log.d(Talkable.TAG, "Permissions to READ_CONTACTS were not granted");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void setLocation(String href) {

    }

    public void offerTriggered(String offerSharePath) {

    }

    public void couponIssued(String channel, String couponCode) {

    }

    public void offerClose() {
        if (mListener != null) {
            mListener.onOfferClosed();
        }
    }

    public void changeOfferState(String offerStateAttribute, String offerState, String action) {

    }

    public void offerLoaded(Boolean gleamReward, Boolean performSnapshot) {

    }

    public void responsiveIframeHeight(Integer height) {

    }

    public void shareOfferViaTwitter(String message) {

    }

    public void shareOfferViaFacebook(String url, String message) {
        FacebookUtils.share(this, url);
    }

    public void shareOfferViaFacebookMessage(String claimUrl) {
        FacebookUtils.shareViaMessenger(this, claimUrl);
    }

    public void copyToClipboard(String string) {
        if (getActivity() == null) return;

        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(string, string);
        clipboard.setPrimaryClip(clip);
    }

    public void popupOpened() {
        mHasOpenedPopups = true;
    }

    public void customWebEvent(String eventName, JsonObject data) {
    }

    private Map<String, String> getTalkableHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(Talkable.TALKABLE_FEATURES_HEADER, Talkable.getNativeFeatures());
        return headers;
    }

    private void publishImportedContacts() {
        ArrayList<Contact> contacts = ContactsImporter.getAllContacts(getActivity());
        JsonObject json = new JsonObject();
        json.add("recipients", JsonUtils.toJsonTree(contacts, ArrayList.class));
        publishToWebView("contacts_imported", json);
    }

    public interface TalkableOfferFragmentListener {
        void onOfferClosed();
    }
}
