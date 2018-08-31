package com.talkable.sdk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WebAppInterfaceTest {
    @Test
    public void testPublish() throws Exception {
        TalkableOfferFragment fragment = new TalkableOfferFragment() {
            @Override
            public void shareOfferViaSms(String recipients, String message, String claimUrl) {
                assertEquals(recipients, null);
                assertEquals(message, "hello");
                assertEquals(claimUrl, null);
            }

            @Override
            public void offerLoaded(Boolean gleamReward, Boolean performSnapshot) {
                assertEquals(gleamReward, true);
                assertEquals(performSnapshot, false);
            }

            @Override
            public void responsiveIframeHeight(Integer height) {
                assertEquals(Long.valueOf(height), Long.valueOf(123));
            }
        };

        WebAppInterface _interface = new WebAppInterface(fragment);
        _interface.publish("share_offer_via_sms", "{\"recipients\":null,\"message\":\"hello\"}");
        _interface.publish("offer_loaded", "{\"gleam_reward\":true}");
        _interface.publish("responsive_iframe_height", "{\"height\":123}");
    }
}