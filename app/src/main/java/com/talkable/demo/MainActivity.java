package com.talkable.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.talkable.sdk.Talkable;
import com.talkable.sdk.TalkableApi;
import com.talkable.sdk.TalkableDeepLinking;
import com.talkable.sdk.api.ApiError;
import com.talkable.sdk.interfaces.Callback1;
import com.talkable.sdk.interfaces.Callback2;
import com.talkable.sdk.interfaces.TalkableErrorCallback;
import com.talkable.sdk.models.AffiliateMember;
import com.talkable.sdk.models.Customer;
import com.talkable.sdk.models.Event;
import com.talkable.sdk.models.Item;
import com.talkable.sdk.models.Offer;
import com.talkable.sdk.models.Origin;
import com.talkable.sdk.models.Purchase;
import com.talkable.sdk.models.Reward;
import com.talkable.sdk.utils.TalkableOfferLoadException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static com.talkable.sdk.Talkable.UUID_KEY;
import static com.talkable.sdk.Talkable.VISITOR_OFFER_KEY;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Talkable.trackAppOpen(this);
    }

    public void onAffiliateMemberClick(View view) {
        Talkable.setServer("https://www.talkable.com");
        Talkable.setSiteSlug("android");

        AffiliateMember affiliateMember = new AffiliateMember(getCustomer());
        affiliateMember.setCampaignTag("android-fragments");
        Talkable.showOffer(this, affiliateMember, OverridenTalkableOfferFragment.class, new TalkableErrorCallback<TalkableOfferLoadException>() {
            @Override
            public void onError(final TalkableOfferLoadException error) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Offer Load Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void onPurchaseClick(View view) {
        TalkableApi.createOrigin(buildPurchase(), new Callback2<Origin, Offer>() {
            @Override
            public void onSuccess(Origin purchase, Offer offer) {
                showToast("Purchase created");
            }

            @Override
            public void onError(ApiError error) {
                showToast("Error: " + error.getMessage());
            }
        });
    }

    private Purchase buildPurchase() {
        Double subtotal = getSubtotal();
        String orderNumber = getOrderNumber();
        String[] coupons = getCoupons();
        Integer quantity = 1;
        String productId = "1";

        Purchase purchase = new Purchase(subtotal, orderNumber, coupons);
        purchase.setCustomer(getCustomer());

        Item item = new Item(subtotal, quantity, productId);
        item.setTitle("Item Title");
        item.setImageUrl("http://test.com/image.jpg");
        item.setUrl("http://test.com/product.html");

        purchase.addItem(item);
        purchase.setCampaignTag("post-purchase-fragments");

        return purchase;
    }

    public void onEventClick(View view) {
        String eventNumber = getOrderNumber();
        String eventCategory = getEventCategory();
        Double subtotal = getSubtotal();
        String[] coupons = getCoupons();

        Event event = new Event(eventNumber, eventCategory, subtotal, coupons);
        event.setCustomer(getCustomer());

        TalkableApi.createOrigin(event, new Callback2<Origin, Offer>() {
            @Override
            public void onSuccess(Origin origin, Offer offer) {
                showToast("Event created");
            }

            @Override
            public void onError(ApiError error) {
                showToast("Error: " + error.getMessage());
            }
        });
    }

    public void onAffiliateMemberViaApiClick(View view) {
        AffiliateMember affiliateMember = new AffiliateMember();
        affiliateMember.setCustomer(getCustomer());

        TalkableApi.createOrigin(affiliateMember, new Callback2<Origin, Offer>() {
            @Override
            public void onSuccess(Origin origin, Offer offer) {
                showToast("Affiliate Member created");
            }

            @Override
            public void onError(ApiError error) {
                showToast("Error: " + error.getMessage());
            }
        });
    }

    public void getRewardsClick(View view) {
        TalkableApi.retrieveRewards(new Callback1<Reward[]>() {
            @Override
            public void onSuccess(Reward[] rewards) {
                showToast("Rewards count: " + rewards.length);
            }

            @Override
            public void onError(ApiError error) {
                showToast("Error: " + error.getMessage());
            }
        });
    }

    private Customer getCustomer() {
        Customer customer = null;
        HashMap<String, String> customProperties = new HashMap<String, String>();
        customProperties.put("prop1", "value2");
        try {
            customer = new Customer(null, null, null, getEmail(), customProperties);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return customer;
    }

    private String getEmail() {
        EditText text = findViewById(R.id.emailText);
        String email = text != null ? text.getText().toString() : null;
        return email == null || email.length() == 0 ? null : email;
    }

    private String[] getCoupons() {
        EditText text = findViewById(R.id.couponText);
        if (text == null) return null;
        String couponsString = text.getText().toString();
        if (couponsString.isEmpty()) {
            return null;
        }
        return couponsString.trim().split(",");
    }

    private Double getSubtotal() {
        EditText text = findViewById(R.id.subtotalText);
        String subtotal = text.getText().toString();
        return (subtotal.length() > 0) ? Double.valueOf(subtotal) : null;
    }

    private String getOrderNumber() {
        EditText text = findViewById(R.id.orderNumberText);
        return text.getText().toString();
    }

    private String getEventCategory() {
        EditText text = findViewById(R.id.eventCategoryText);
        return text.getText().toString();
    }

    private void showToast(final String text) {
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onPostPurchaseClick(View view) {
        Talkable.setServer("https://www.talkable.com");
        Talkable.setSiteSlug("android");

        Talkable.showOffer(MainActivity.this, buildPurchase(), new TalkableErrorCallback<TalkableOfferLoadException>() {
            @Override
            public void onError(final TalkableOfferLoadException error) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Offer Load Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void onDeepLinkingClick(View view) {
        EditText webUuidText = findViewById(R.id.webUUIDText);
        String webUuid = webUuidText.getText().toString();
        EditText offerIdText = findViewById(R.id.offerIDText);
        String offerId = offerIdText.getText().toString();
        HashMap<String, String> paramsMap = new HashMap<>();
        if (webUuid.length() > 0) {
            paramsMap.put(UUID_KEY, webUuid);
        }
        if (offerId.length() > 0) {
            paramsMap.put(VISITOR_OFFER_KEY, offerId);
        }
        TalkableDeepLinking.track(paramsMap);
    }
}
