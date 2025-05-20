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
    
    /**
     * For each test, we use a separate MockedStatic instance rather than class-level mocks
     * This avoids issues with incomplete stubbing and module access exceptions
     */
    @Before public void setup() {
        // We only set up the request saver in the setup method
        // All static mocking is now done in each test method with try-with-resources
        TalkableApi.setRequestSaver(new RequestSaverStub());
    }
    
    @After
    public void tearDown() {
        // Nothing to tear down since we're using try-with-resources for all static mocks
    }

    @Test
    public void createVisitor() throws Exception {
        // Use a separate MockedStatic instance for this test
        try (MockedStatic<Talkable> localMock = Mockito.mockStatic(Talkable.class);
             MockedStatic<TalkablePreferencesStore> localPrefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {
            
            // Set up all required stubs
            localMock.when(Talkable::getApiKey).thenReturn(apiKey);
            localMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
            localMock.when(Talkable::getServer).thenReturn(server);
            localMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
            localPrefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);
            
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
        } // The try-with-resources will automatically close the mock
    }

    @Test
    public void createPurchase() throws Exception {
        // Use a separate MockedStatic instance for this test
        try (MockedStatic<Talkable> localMock = Mockito.mockStatic(Talkable.class);
             MockedStatic<TalkablePreferencesStore> localPrefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {
            
            // Set up all required stubs
            localMock.when(Talkable::getApiKey).thenReturn(apiKey);
            localMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
            localMock.when(Talkable::getServer).thenReturn(server);
            localMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
            localPrefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);
            
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
                    Item item = new Item(productId, quantity);
                    purchase.addItem(item);

                    TalkableApi.createPurchase(purchase, new Callback2<Origin, Offer>() {
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
                    Date yesterday = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
                    purchase.setPurchaseDate(yesterday);
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(cal.getTimeInMillis() - (1000 * 60 * 60 * 24));
                    purchase.setPurchaseDate(cal.getTime());

                    TalkableApi.createPurchase(purchase, new Callback2<Origin, Offer>() {
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
        } // The try-with-resources will automatically close the mock
    }

    @Test
    public void createEvent() throws Exception {
        // Use a separate MockedStatic instance for this test
        try (MockedStatic<Talkable> localMock = Mockito.mockStatic(Talkable.class);
             MockedStatic<TalkablePreferencesStore> localPrefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {
            
            // Set up all required stubs
            localMock.when(Talkable::getApiKey).thenReturn(apiKey);
            localMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
            localMock.when(Talkable::getServer).thenReturn(server);
            localMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
            localPrefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);
            
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
    } // The try-with-resources will automatically close the mock
}

@Test
public void createPurchase() throws Exception {
    // Use a separate MockedStatic instance for this test
    try (MockedStatic<Talkable> localMock = Mockito.mockStatic(Talkable.class);
         MockedStatic<TalkablePreferencesStore> localPrefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {
        
        // Set up all required stubs
        localMock.when(Talkable::getApiKey).thenReturn(apiKey);
        localMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
        localMock.when(Talkable::getServer).thenReturn(server);
        localMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
        localPrefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);
        
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
                Item item = new Item(productId, quantity);
                purchase.addItem(item);

                TalkableApi.createPurchase(purchase, new Callback2<Origin, Offer>() {
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
                Date yesterday = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
                purchase.setPurchaseDate(yesterday);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(cal.getTimeInMillis() - (1000 * 60 * 60 * 24));
                purchase.setPurchaseDate(cal.getTime());

                TalkableApi.createPurchase(purchase, new Callback2<Origin, Offer>() {
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
    } // The try-with-resources will automatically close the mock
}

@Test
public void createEvent() throws Exception {
    // Use a separate MockedStatic instance for this test
    try (MockedStatic<Talkable> localMock = Mockito.mockStatic(Talkable.class);
         MockedStatic<TalkablePreferencesStore> localPrefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {
        
        // Set up all required stubs
        localMock.when(Talkable::getApiKey).thenReturn(apiKey);
        localMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
        localMock.when(Talkable::getServer).thenReturn(server);
        localMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
        localPrefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);
        
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
    } // The try-with-resources will automatically close the mock
}

@Test
public void createAffiliateMember() throws Exception {
    // Use a separate MockedStatic instance for this test
    try (MockedStatic<Talkable> localMock = Mockito.mockStatic(Talkable.class);
         MockedStatic<TalkablePreferencesStore> localPrefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {
        
        // Set up all required stubs
        localMock.when(Talkable::getApiKey).thenReturn(apiKey);
        localMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
        localMock.when(Talkable::getServer).thenReturn(server);
        localMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
        localPrefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);
        
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
    } // The try-with-resources will automatically close the mock
}

@Test
public void retrieveOffer() {
    // Use a separate MockedStatic instance for this test
    try (MockedStatic<Talkable> localMock = Mockito.mockStatic(Talkable.class);
         MockedStatic<TalkablePreferencesStore> localPrefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {
        
        // Set up all required stubs
        localMock.when(Talkable::getApiKey).thenReturn(apiKey);
        localMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
        localMock.when(Talkable::getServer).thenReturn(server);
        localMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
        localPrefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);
        
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
    } // The try-with-resources will automatically close the mock
}

/**
 * Test creating affiliate member, offer share and retrieving rewards
 */
@Test
public void testWorkflow() {
    // Use a separate MockedStatic instance for this test
    try (MockedStatic<Talkable> localMock = Mockito.mockStatic(Talkable.class);
         MockedStatic<TalkablePreferencesStore> localPrefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {
        
        // Set up all required stubs
        localMock.when(Talkable::getApiKey).thenReturn(apiKey);
        localMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
        localMock.when(Talkable::getServer).thenReturn(server);
        localMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
        localPrefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);
        
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
    } // The try-with-resources will automatically close the mock
}

@Test
public void makeRequestWithoutInternet() throws Exception {
    // Use a separate MockedStatic instance for this test
    try (MockedStatic<Talkable> localMock = Mockito.mockStatic(Talkable.class);
         MockedStatic<TalkablePreferencesStore> localPrefsMock = Mockito.mockStatic(TalkablePreferencesStore.class)) {
        
        // Set up all required stubs
        localMock.when(Talkable::getApiKey).thenReturn(apiKey);
        localMock.when(Talkable::getSiteSlug).thenReturn(siteSlug);
        localMock.when(Talkable::getServer).thenReturn("http://localhost:54321");
        localMock.when(Talkable::getHttpClient).thenReturn(new OkHttpClient());
        localPrefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(_uuid);

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
    } // The try-with-resources will automatically close the mock
}
