package com.talkable.sdk.utils;

import android.net.Uri;

import com.talkable.sdk.BuildConfig;
import com.talkable.sdk.Talkable;
import com.talkable.sdk.TalkablePreferencesStore;
import com.talkable.sdk.models.Customer;
import com.talkable.sdk.models.Event;
import com.talkable.sdk.models.Item;
import com.talkable.sdk.models.Origin;
import com.talkable.sdk.models.Purchase;

import java.util.Map;

public class UriUtils {
    public static final String API_ENDPOINT = "api/v2";

    //-----------+
    //  Builders |
    //-----------+

    public static Uri.Builder publicUriBuilder(String controller, String action, Params params) {
        Uri server = Uri.parse(Talkable.getServer());
        Uri.Builder builder = new Uri.Builder()
                .scheme(server.getScheme())
                .encodedAuthority(server.getAuthority())
                .appendPath("public")
                .appendPath(Talkable.getSiteSlug())
                .appendPath(controller)
                .appendPath(action + ".html")
                .appendQueryParameter("v", "android-" + BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE);

        for (Map.Entry<String, String> entry : params.getEntries()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder;
    }

    public static Uri.Builder publicUriBuilder(String controller, String action) {
        return publicUriBuilder(controller, action, new Params());
    }

    public static Uri.Builder apiUriBuilder(String path, Params params) {
        Uri.Builder builder = Uri.parse(Talkable.getServer()).buildUpon();

        // Concating arrays in java is hellish
        for (String part : API_ENDPOINT.split("/")) {
            builder.appendPath(part);
        }

        for (String part : path.split("/")) {
            builder.appendPath(part);
        }

        builder.appendQueryParameter("site_slug", Talkable.getSiteSlug());

        for (Map.Entry<String, String> entry : params.getEntries()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder;
    }

    public static Uri.Builder apiUriBuilder(String path) {
        return apiUriBuilder(path, new Params());
    }

    //-----------+
    //    Uri    |
    //-----------+

    public static Uri offerUri(Origin origin) {
        String controller = "affiliate_members";

        Params params = new Params();
        params.put("current_visitor_uuid", TalkablePreferencesStore.getMainUUID());
        for (String campaignTag : origin.getCampaignTags()) {
            params.put("campaign_tags[]", campaignTag);
        }
        params.put("o[traffic_source]", origin.getTrafficSource());

        Customer customer = origin.getCustomer();

        if (customer != null) {
            String encodedEmail = customer.getEncodedEmail();
            params.put("o[email]", encodedEmail);
            params.put("o[first_name]", customer.getFirstName());
            params.put("o[last_name]", customer.getLastName());
        }

        if (origin instanceof Event) {
            controller = "events";

            Event event = (Event) origin;

            params.put("o[subtotal]", event.getSubtotal());

            if (event.getCouponCodes() != null) {
                for (String couponCode : event.getCouponCodes()) {
                    params.put("o[coupon_code][]", couponCode);
                }
            }

            if (origin instanceof Purchase) {
                controller = "purchases";

                Purchase purchase = (Purchase) origin;

                params.put("o[order_number]", purchase.getOrderNumber());

                for (Item i : purchase.getItems()) {
                    params.put("o[i][][product_id]", i.getProductId());
                    params.put("o[i][][price]", i.getPrice());
                    params.put("o[i][][quantity]", i.getQuantity());
                }
            } else {
                params.put("o[event_number]", event.getEventNumber());
                params.put("o[event_category]", event.getEventCategory());
            }
        }

        Map<String, String> customProperties = origin.getCustomProperties();
        if (customProperties != null) {
            for (Map.Entry<String, String> prop : customProperties.entrySet()) {
                if (prop.getValue() != null) {
                    params.put(String.format("custom_properties[%s]", prop.getKey()), prop.getValue());
                }
            }
        }

        return publicUriBuilder(controller, "create", params).build();
    }

    public static Uri productUri(Item item) {
        String controller = "products";

        Params params = new Params();
        params.put("p[product_id]", item.getProductId());
        params.put("p[title]", item.getTitle());
        params.put("p[url]", item.getUrl());
        params.put("p[image_url]", item.getImageUrl());

        return publicUriBuilder(controller, "create", params).build();
    }
}
