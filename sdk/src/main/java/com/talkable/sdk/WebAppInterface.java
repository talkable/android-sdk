package com.talkable.sdk;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.talkable.sdk.utils.JsonUtils;

class WebAppInterface {
    private TalkableOfferFragment talkableOfferFragment;

    WebAppInterface(TalkableOfferFragment fragment) {
        this.talkableOfferFragment = fragment;
    }

    @JavascriptInterface
    public void publish(String eventName, String data) {
        Log.d(Talkable.TAG, "Event:" + eventName + "; Data:" + data);

        try {
            JsonObject json = new JsonObject();
            if (data.length() > 0) {
                json = JsonParser.parseString(data).getAsJsonObject();
            }
            switch (eventName) {
                case "put_to_clipboard":
                    talkableOfferFragment.copyToClipboard(
                            JsonUtils.getJsonString(json, "text"));
                    break;
                case "offer_close":
                    talkableOfferFragment.offerClose();
                    break;
                case "offer_loaded":
                    talkableOfferFragment.offerLoaded(
                            JsonUtils.getJsonBoolean(json, "gleam_reward"),
                            JsonUtils.getJsonBoolean(json, "perform_snapshot"));
                    break;
                case "offer_triggered":
                    talkableOfferFragment.offerTriggered(
                            JsonUtils.getJsonString(json, "offer_share_path"));
                    break;
                case "change_offer_state":
                    talkableOfferFragment.changeOfferState(
                            JsonUtils.getJsonString(json, "offer_state_attribute"),
                            JsonUtils.getJsonString(json, "offer_state"),
                            JsonUtils.getJsonString(json, "action"));
                    break;
                case "responsive_iframe_height":
                    talkableOfferFragment.responsiveIframeHeight(
                            JsonUtils.getJsonInt(json, "height"));
                    break;
                case "share_offer_via_native_mail":
                    talkableOfferFragment.shareOfferViaNativeMail(
                            JsonUtils.getJsonString(json, "subject"),
                            JsonUtils.getJsonString(json, "message"),
                            JsonUtils.getJsonString(json, "claim_url")
                    );
                    break;
                case "share_offer_via_facebook":
                    talkableOfferFragment.shareOfferViaFacebook(
                            JsonUtils.getJsonString(json, "claim_url"),
                            JsonUtils.getJsonString(json, "message"));
                    break;

                case "share_offer_via_facebook_message":
                    talkableOfferFragment.shareOfferViaFacebookMessage(
                            JsonUtils.getJsonString(json, "claim_url"));
                    break;
                case "share_offer_via_twitter":
                    talkableOfferFragment.shareOfferViaTwitter(
                            JsonUtils.getJsonString(json, "message"));
                    break;
                case "share_offer_via_whatsapp":
                    talkableOfferFragment.shareOfferViaWhatsApp(
                            JsonUtils.getJsonString(json, "message"));
                    break;
                case "share_offer_via_sms":
                    talkableOfferFragment.shareOfferViaSms(
                            JsonUtils.getJsonString(json, "recipients"),
                            JsonUtils.getJsonString(json, "message"),
                            JsonUtils.getJsonString(json, "claim_url"));
                    break;
                case "coupon_issued":
                    talkableOfferFragment.couponIssued(
                            JsonUtils.getJsonString(json, "channel"),
                            JsonUtils.getJsonString(json, "coupon_code"));
                    break;
                case "import_contacts":
                    talkableOfferFragment.importContacts();
                    break;
                case "popup_opened":
                    talkableOfferFragment.popupOpened();
                    break;
                default:
                    talkableOfferFragment.customWebEvent(eventName, json);
                    break;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
