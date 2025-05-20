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
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import okhttp3.OkHttpClient;

import static com.talkable.sdk.SynchronizedTest.sync;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class TalkableApiUnitTest {
    private static final String _uuid = UUID.randomUUID().toString();

    // Production
    private final String server = Talkable.DEFAULT_SERVER;
    private final String apiKey = "SVd5nKk3PojcjfuKVg";
    private final String siteSlug = "android-specs";

    @Before
    public void setup() {
        // We only set up the request saver in the setup method
        TalkableApi.setRequestSaver(new RequestSaverStub());
    }

    @After
    public void tearDown() {
        // Nothing to tear down since we're using try-with-resources for all static mocks
    }

    @Test
    public void createVisitor() throws Exception {
        try (MockedStatic<Talkable> talkableMock = Mockito.mockStatic(Talkable.class);
             MockedStatic<TalkablePreferencesStore> prefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {

            // Setup all required mocks
            talkableMock.when(Talkable::getApiKey).thenReturn(apiKey);
            talkableMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
            talkableMock.when(Talkable::getServer).thenReturn(server);
            talkableMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
            prefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);

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
                            // Error handling
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
                            // Error handling
                        }
                    });
                }
            });
        }
    }

    @Test
    public void createPurchase() throws Exception {
        try (MockedStatic<Talkable> talkableMock = Mockito.mockStatic(Talkable.class);
             MockedStatic<TalkablePreferencesStore> prefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {

            // Setup all required mocks
            talkableMock.when(Talkable::getApiKey).thenReturn(apiKey);
            talkableMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
            talkableMock.when(Talkable::getServer).thenReturn(server);
            talkableMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
            prefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);

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
                    } catch (UnsupportedEncodingException ignored) {
                    }
                    purchase.setCustomer(customer);
                    Item item = new Item(subtotal, quantity, productId);
                    purchase.addItem(item);

                    TalkableApi.createOrigin(purchase, new Callback2<Origin, Offer>() {
                        @Override
                        public void onSuccess(Origin origin, Offer offer) {
                            assertNotEquals(origin, null);
                            assertNotEquals(offer, null);
                            r.done();
                        }

                        @Override
                        public void onError(ApiError error) {
                            // Error handling
                        }
                    });

                    // Second create purchase request
                    purchase = new Purchase(subtotal, orderNumber);
                    // Note: setPurchaseDate is not available in the Purchase class
                    // The Purchase class extends Event which may have had this method in the past
                    // but it's no longer available

                    TalkableApi.createOrigin(purchase, new Callback2<Origin, Offer>() {
                        @Override
                        public void onSuccess(Origin origin, Offer offer) {
                            assertNotEquals(origin, null);
                            r.done();
                        }

                        @Override
                        public void onError(ApiError error) {
                            // Error handling
                        }
                    });
                }
            });
        }
    }

    @Test
    public void createEvent() throws Exception {
        try (MockedStatic<Talkable> talkableMock = Mockito.mockStatic(Talkable.class);
             MockedStatic<TalkablePreferencesStore> prefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {

            // Setup all required mocks
            talkableMock.when(Talkable::getApiKey).thenReturn(apiKey);
            talkableMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
            talkableMock.when(Talkable::getServer).thenReturn(server);
            talkableMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
            prefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);

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
                            // Error handling
                        }
                    });

                    Event event2 = new Event(eventNumber, eventCategory);
                    Customer customer = null;
                    try {
                        customer = new Customer("user@example.com");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    event2.setCustomer(customer);

                    TalkableApi.createOrigin(event2, new Callback2<Origin, Offer>() {
                        @Override
                        public void onSuccess(Origin origin, Offer offer) {
                            assertNotEquals(origin, null);
                            r.done();
                        }

                        @Override
                        public void onError(ApiError error) {
                            // Error handling
                        }
                    });
                }
            });
        }
    }

    @Test
    public void createAffiliateMember() throws Exception {
        try (MockedStatic<Talkable> talkableMock = Mockito.mockStatic(Talkable.class);
             MockedStatic<TalkablePreferencesStore> prefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {

            // Setup all required mocks
            talkableMock.when(Talkable::getApiKey).thenReturn(apiKey);
            talkableMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
            talkableMock.when(Talkable::getServer).thenReturn(server);
            talkableMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
            prefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);

            sync(new ResultCallback() {
                @Override
                public void run(final Result r) {
                    AffiliateMember affiliateMember = new AffiliateMember();

                    TalkableApi.createOrigin(affiliateMember, new Callback2<Origin, Offer>() {
                        @Override
                        public void onSuccess(Origin origin, Offer offer) {
                            assertNotEquals(origin, null);
                            assertNotEquals(offer, null);
                            r.done();
                        }

                        @Override
                        public void onError(ApiError e) {
                            // Error handling
                        }
                    });
                }
            });
        }
    }

    @Test
    public void retrieveOffer() {
        try (MockedStatic<Talkable> talkableMock = Mockito.mockStatic(Talkable.class);
             MockedStatic<TalkablePreferencesStore> prefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {

            // Setup all required mocks
            talkableMock.when(Talkable::getApiKey).thenReturn(apiKey);
            talkableMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
            talkableMock.when(Talkable::getServer).thenReturn(server);
            talkableMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
            prefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);

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
                                    // Error handling
                                }
                            });
                        }

                        @Override
                        public void onError(ApiError e) {
                            // Error handling
                        }
                    });
                }
            });
        }
    }

    /**
     * Test creating affiliate member, offer share and retrieving rewards
     */
    @Test
    public void testWorkflow() {
        try (MockedStatic<Talkable> talkableMock = Mockito.mockStatic(Talkable.class);
             MockedStatic<TalkablePreferencesStore> prefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {

            // Setup all required mocks
            talkableMock.when(Talkable::getApiKey).thenReturn(apiKey);
            talkableMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
            talkableMock.when(Talkable::getServer).thenReturn(server);
            talkableMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
            prefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);

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
                                            // Error handling
                                        }
                                    });
                                }

                                @Override
                                public void onError(ApiError e) {
                                    // Error handling
                                }
                            });
                        }

                        @Override
                        public void onError(ApiError e) {
                            // Error handling
                        }
                    });
                }
            });
        }
    }

    @Test
    public void makeRequestWithoutInternet() throws Exception {
        try (MockedStatic<Talkable> talkableMock = Mockito.mockStatic(Talkable.class);
             MockedStatic<TalkablePreferencesStore> prefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {

            // Setup all required mocks
            talkableMock.when(Talkable::getApiKey).thenReturn(apiKey);
            talkableMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
            talkableMock.when(Talkable::getServer).thenReturn("http://localhost:54321"); // Intentionally invalid server
            talkableMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
            prefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);

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
        }
    }
}
