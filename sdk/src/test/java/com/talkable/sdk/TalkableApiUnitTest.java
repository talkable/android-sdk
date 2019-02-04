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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import okhttp3.OkHttpClient;

import static com.talkable.sdk.SynchronizedTest.sync;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Talkable.class, TalkablePreferencesStore.class})
@PowerMockIgnore("javax.net.ssl.*")
public class TalkableApiUnitTest {
    private static final String _uuid = UUID.randomUUID().toString();

    // Development
    // String server = "http://localhost:3000";
    // String apiKey = "qNhtjwqo35u3W0fI4uo";
    // String siteSlug = "android-specs";

    // Production
    String server = Talkable.DEFAULT_SERVER;
    String apiKey = "SVd5nKk3PojcjfuKVg";
    String siteSlug = "android-specs";

    @Before public void setup() {
        PowerMockito.mockStatic(Talkable.class);
        PowerMockito.mockStatic(TalkablePreferencesStore.class);

        PowerMockito.when(Talkable.getApiKey()).thenReturn(apiKey);
        PowerMockito.when(Talkable.getSiteSlug()).thenReturn(siteSlug);
        PowerMockito.when(Talkable.getServer()).thenReturn(server);
        PowerMockito.when(Talkable.getHttpClient()).thenReturn(new OkHttpClient());
        PowerMockito.when(TalkablePreferencesStore.getMainUUID()).thenReturn(_uuid);

        TalkableApi.setRequestSaver(new RequestSaverStub());
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
        PowerMockito.when(Talkable.getServer()).thenReturn("http://localhost:54321");

        sync(new ResultCallback() {
            @Override
            public void run(final Result r) {
                AffiliateMember affiliateMember = new AffiliateMember();

                TalkableApi.createOrigin(affiliateMember, new Callback2<Origin, Offer>() {
                    @Override
                    public void onSuccess(Origin origin, Offer offer) {

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

        PowerMockito.when(Talkable.getServer()).thenReturn(server);

    }
}