package com.talkable.sdk;

import com.talkable.sdk.api.ApiError;
import com.talkable.sdk.interfaces.Callback1;
import com.talkable.sdk.interfaces.Callback2;
import com.talkable.sdk.models.AffiliateMember;
import com.talkable.sdk.models.Customer;
import com.talkable.sdk.models.Event;
import com.talkable.sdk.models.Item;
import com.talkable.sdk.models.Offer;
import com.talkable.sdk.models.OfferShare;
import com.talkable.sdk.models.Origin;
import com.talkable.sdk.models.Purchase;
import com.talkable.sdk.models.Reward;
import com.talkable.sdk.models.SharingChannel;
import com.talkable.sdk.models.SocialOfferShare;
import com.talkable.sdk.models.Visitor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import okhttp3.OkHttpClient;

import static com.talkable.sdk.SynchronizedTest.sync;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TalkableApiUnitTest {
    private static final String _uuid = UUID.randomUUID().toString();
    private MockedStatic<Talkable> talkableMock;
    private MockedStatic<TalkablePreferencesStore> prefsMock;

    // Development
    // String server = "http://localhost:3000";
    // String apiKey = "qNhtjwqo35u3W0fI4uo";
    // String siteSlug = "android-specs";

    // Production
    String server = Talkable.DEFAULT_SERVER;
    String apiKey = "SVd5nKk3PojcjfuKVg";
    String siteSlug = "android-specs";

    @Before public void setup() {
        talkableMock = Mockito.mockStatic(Talkable.class);
        prefsMock = Mockito.mockStatic(TalkablePreferencesStore.class);

        talkableMock.when(Talkable::getApiKey).thenReturn(apiKey);
        talkableMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
        talkableMock.when(Talkable::getServer).thenReturn(server);
        talkableMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
        prefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);

        TalkableApi.setRequestSaver(new RequestSaverStub());
    }

    @After
    public void tearDown() {
        talkableMock.close();
        prefsMock.close();
    }

    @Test
    public void createVisitor() throws Exception {
        sync(2, new ResultCallback() {
            @Override
            public void run(final Result r) {
                Visitor visitor = new Visitor(_uuid);
                TalkableApi.createVisitor(visitor, new Callback1<Visitor>() {
                    @Override
                    public void onSuccess(Visitor apiVisitor) {
                        assertEquals(apiVisitor.getUuid(), _uuid);
                        r.done();
                    }

                    @Override
                    public void onError(ApiError error) {

                    }
                });

                TalkableApi.createVisitor(new Callback1<Visitor>() {
                    @Override
                    public void onSuccess(Visitor apiVisitor) {
                        assertNotEquals(apiVisitor.getUuid(), null);
                        r.done();
                    }

                    @Override
                    public void onError(ApiError error) {

                    }
                });
            }
        });
    }

    @Test
    public void createPurchase() throws Exception {
        sync(2, new ResultCallback() {
            @Override
            public void run(final Result r) {
                Double subtotal = 10.99;
                String orderNumber = "1";
                String couponCode = "COUPON";
                Integer quantity = 1;
                String productId = "1";

                Purchase purchase = new Purchase(subtotal, orderNumber, couponCode);
                Customer customer = null;
                try {
                    customer = new Customer("user@example.com");
                    HashMap<String, String> customProperties = new HashMap<String, String>();
                    customProperties.put("badger", "mushroom");
                    customer.setCustomProperties(customProperties);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                purchase.setCustomer(customer);
                purchase.addItem(new Item(subtotal, quantity, productId));

                TalkableApi.createOrigin(purchase, new Callback2<Origin, Offer>() {
                    @Override
                    public void onSuccess(Origin origin, Offer offer) {
                        assertNotEquals(origin, null);
                        assertEquals(offer, null);
                        r.done();
                    }

                    @Override
                    public void onError(ApiError error) {

                    }
                });

                purchase.setCustomer(null);
                TalkableApi.createOrigin(purchase, new Callback2<Origin, Offer>() {
                    @Override
                    public void onSuccess(Origin origin, Offer offer) {

                    }

                    @Override
                    public void onError(ApiError error) {
                        assertNotEquals(error, null);
                        r.done();
                    }
                });
            }
        });
    }

    @Test
    public void createEvent() throws Exception {
        sync(2, new ResultCallback() {
            @Override
            public void run(final Result r) {
                String eventNumber = "1";
                String eventCategory = "signup";
                Double subtotal = 10.99;
                String couponCode = "COUPON";

                Event event = new Event(eventNumber, eventCategory, subtotal, couponCode);

                TalkableApi.createOrigin(event, new Callback2<Origin, Offer>() {
                    @Override
                    public void onSuccess(Origin origin, Offer offer) {
                        assertNotEquals(origin, null);
                        assertEquals(offer, null);
                        r.done();
                    }

                    @Override
                    public void onError(ApiError e) {

                    }
                });

                Event event2 = new Event(eventNumber, null, null, (String[]) null);
                TalkableApi.createOrigin(event2, new Callback2<Origin, Offer>() {
                    @Override
                    public void onSuccess(Origin origin, Offer offer) {
                        assertEquals(origin, null);
                        assertEquals(offer, null);
                        r.done();
                    }

                    @Override
                    public void onError(ApiError error) {
                        assertNotEquals(error, null);
                        r.done();
                    }
                });
            }
        });
    }


    @Test
    public void createAffiliateMember() throws Exception {
        sync(new ResultCallback() {
            @Override
            public void run(final Result r) {
                AffiliateMember affiliateMember = new AffiliateMember();

                TalkableApi.createOrigin(affiliateMember, new Callback2<Origin, Offer>() {
                    @Override
                    public void onSuccess(Origin origin, Offer offer) {
                        assertNotEquals(origin, null);
                        assertEquals(offer, null);
                        r.done();
                    }

                    @Override
                    public void onError(ApiError e) {

                    }
                });
            }
        });
    }

    @Test
    public void retrieveOffer() {
        sync(new ResultCallback() {
            @Override
            public void run(final Result r) {
                AffiliateMember affiliateMember = new AffiliateMember();
                affiliateMember.setCampaignTags(new String[]{"test-android"});

                TalkableApi.createOrigin(affiliateMember, new Callback2<Origin, Offer>() {
                    @Override
                    public void onSuccess(final Origin origin, final Offer offer) {
                        assertNotEquals(origin, null);
                        assertNotEquals(offer, null);

                        TalkableApi.retrieveOffer(offer.getCode(), new Callback1<Offer>() {
                            @Override
                            public void onSuccess(Offer newOffer) {
                                assertEquals(newOffer.getShowUrl(), offer.getShowUrl());

                                r.done();
                            }

                            @Override
                            public void onError(ApiError e) {

                            }
                        });
                    }

                    @Override
                    public void onError(ApiError e) {

                    }
                });
            }
        });
    }

    /**
     * Test creating affiliate member, offer share and retrieving rewards
     */
    @Test
    public void testWorkflow() {
        sync(new ResultCallback() {
            @Override
            public void run(final Result r) {
                AffiliateMember affiliateMember = new AffiliateMember();
                affiliateMember.setCampaignTags(new String[]{"test-android"});

                TalkableApi.createOrigin(affiliateMember, new Callback2<Origin, Offer>() {
                    @Override
                    public void onSuccess(final Origin origin, Offer offer) {
                        assertNotEquals(origin, null);
                        assertNotEquals(offer, null);

                        SocialOfferShare share = new SocialOfferShare(offer, SharingChannel.OTHER);
                        TalkableApi.createSocialShare(share, new Callback2<SocialOfferShare, Reward>() {
                            @Override
                            public void onSuccess(final SocialOfferShare createdShare, Reward reward) {
                                assertNotEquals(reward, null);

                                TalkableApi.retrieveRewards(new Callback1<Reward[]>() {
                                    @Override
                                    public void onSuccess(Reward[] rewards) {
                                        assertEquals(rewards.length, 1);
                                        assertEquals(rewards[0].getCouponCode(), "AD_3_OFF");
                                        assertEquals(rewards[0].getAmount(), 3, 0);
                                        assertEquals(rewards[0].getReason(), "shared");

                                        r.done();
                                    }

                                    @Override
                                    public void onError(ApiError e) {

                                    }
                                });
                            }

                            @Override
                            public void onError(ApiError e) {

                            }
                        });
                    }

                    @Override
                    public void onError(ApiError e) {

                    }
                });
            }
        });
    }

    @Test
    public void makeRequestWithoutInternet() throws Exception {
        // Save the original server value
        String originalServer = server;

        // Use the mocked static instance to change the server
        talkableMock.when(Talkable::getServer).thenReturn("http://localhost:54321");

        sync(new ResultCallback() {
            @Override
            public void run(final Result r) {
                AffiliateMember affiliateMember = new AffiliateMember();

                TalkableApi.createOrigin(affiliateMember, new Callback2<Origin, Offer>() {
                    @Override
                    public void onSuccess(Origin origin, Offer offer) {
                        // This should not be called
                    }

                    @Override
                    public void onError(ApiError e) {
                        assertNotEquals(e, null);
                        assertEquals(TalkableApi.getRequestSaver().takeEntries().size(), 1);
                        r.done();
                    }
                });
            }
        });

        // Restore the original server value
        talkableMock.when(Talkable::getServer).thenReturn(originalServer);
    }
}
