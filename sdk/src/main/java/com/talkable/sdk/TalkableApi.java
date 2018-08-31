package com.talkable.sdk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.talkable.sdk.api.ApiCallback;
import com.talkable.sdk.api.ApiError;
import com.talkable.sdk.interfaces.ApiSendable;
import com.talkable.sdk.interfaces.Callback0;
import com.talkable.sdk.interfaces.Callback1;
import com.talkable.sdk.interfaces.Callback2;
import com.talkable.sdk.interfaces.RequestSaver;
import com.talkable.sdk.models.AffiliateMember;
import com.talkable.sdk.models.EmailOfferShare;
import com.talkable.sdk.models.Event;
import com.talkable.sdk.models.Offer;
import com.talkable.sdk.models.OfferShare;
import com.talkable.sdk.models.Origin;
import com.talkable.sdk.models.Purchase;
import com.talkable.sdk.models.Reward;
import com.talkable.sdk.models.SocialOfferShare;
import com.talkable.sdk.models.Visitor;
import com.talkable.sdk.models.VisitorOffer;
import com.talkable.sdk.utils.ApiRequest;
import com.talkable.sdk.utils.JsonUtils;
import com.talkable.sdk.utils.Params;
import com.talkable.sdk.utils.UriUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;

public class TalkableApi {
    private static final List<String> HTTP_METHODS_NEED_SAVING = Arrays.asList("POST", "PATCH", "PUT", "DELETE");

    private static RequestSaver requestSaver;

    public static void initialize(Context context) {
        if (!Talkable.isInitialized()) {
            throw new IllegalStateException("Talkable SDK is not initialized. " +
                    "You must initialize Talkable SDK before using API module.");
        }

        requestSaver = new ContextRequestSaver(context);

        for (ApiRequest r : requestSaver.takeEntries()) {
            TalkableApi.call(r);
        }
    }

    //--------------------+
    //  Getters & Setters |
    //--------------------+

    public static OkHttpClient getClient() {
        return Talkable.getHttpClient();
    }

    public static RequestSaver getRequestSaver() {
        return requestSaver;
    }

    public static void setRequestSaver(RequestSaver newRequestSaver) {
        requestSaver = newRequestSaver;
    }

    public static String getUserAgent() {
        return "API " + Talkable.USER_AGENT_SUFFIX;
    }


    //------------+
    //  API calls |
    //------------+

    public static void createVisitor(final Visitor visitor) {
        createVisitor(visitor, new Callback1<Visitor>() {
            @Override
            public void onSuccess(Visitor arg1) {

            }

            @Override
            public void onError(ApiError e) {

            }
        });
    }

    public static void createVisitor() {
        createVisitor(new Visitor());
    }

    public static void createVisitor(final Callback1<Visitor> callback) {
        createVisitor(new Visitor(), callback);
    }

    public static void createVisitor(final Visitor visitor, final Callback1<Visitor> callback) {
        String uri = UriUtils.apiUriBuilder("visitors").toString();

        Log.d(Talkable.TAG, "create_visitor: " + uri);

        call("POST", uri, visitor, new ApiCallback() {
            @Override
            public void onSuccess(JsonObject json) {
                Visitor visitor = JsonUtils.fromJson(json, Visitor.class);
                callback.onSuccess(visitor);
            }

            @Override
            public void onFailure(ApiError e) {
                callback.onError(e);
            }
        });
    }

    public static void createOrigin(Origin origin) {
        createOrigin(origin, new Callback2<Origin, Offer>() {
            @Override
            public void onSuccess(Origin arg1, Offer arg2) {

            }

            @Override
            public void onError(ApiError e) {

            }
        });
    }

    public static void createOrigin(Origin origin, final Callback2<Origin, Offer> callback) {
        String uri = UriUtils.apiUriBuilder("origins").toString();

        call("POST", uri, origin, new ApiCallback() {
            @Override
            public void onSuccess(JsonObject json) {
                Origin origin = null;
                Offer offer = null;

                JsonObject originJson = json.getAsJsonObject("origin");
                JsonElement offerJson = json.get("offer");

                if (!offerJson.isJsonNull()) {
                    offer = JsonUtils.fromJson(offerJson, Offer.class);
                }

                String originType = originJson.getAsJsonPrimitive("type").getAsString();

                switch (Origin.Type.valueOf(originType)) {
                    case Purchase:
                        origin = JsonUtils.fromJson(originJson, Purchase.class);
                        break;
                    case Event:
                        origin = JsonUtils.fromJson(originJson, Event.class);
                        break;
                    case AffiliateMember:
                        origin = JsonUtils.fromJson(originJson, AffiliateMember.class);
                        break;
                }

                callback.onSuccess(origin, offer);
            }

            @Override
            public void onFailure(ApiError e) {
                callback.onError(e);
            }
        });
    }

    public static void retrieveRewards(Params params, final Callback1<Reward[]> callback) {
        if (!params.containsKey("visitor_uuid")) {
            params.put("visitor_uuid", TalkablePreferencesStore.getMainUUID());
        }

        call("GET", UriUtils.apiUriBuilder("rewards", params).toString(), new ApiCallback() {
            @Override
            public void onSuccess(JsonObject json) {
                JsonArray rewardsJson = json.getAsJsonArray("rewards");
                ArrayList<Reward> rewards = new ArrayList<>();

                for (JsonElement r : rewardsJson) {
                    rewards.add(JsonUtils.fromJson(r, Reward.class));
                }

                callback.onSuccess(rewards.toArray(new Reward[rewards.size()]));
            }

            @Override
            public void onFailure(ApiError e) {
                callback.onError(e);
            }
        });
    }

    public static void retrieveRewards(final Callback1<Reward[]> callback) {
        retrieveRewards(new Params(), callback);
    }

    public static void retrieveOffer(String offerCode, final Callback1<Offer> callback) {
        String builder = UriUtils.apiUriBuilder("offers/" + offerCode).toString();

        call("GET", builder, new ApiCallback() {
            @Override
            public void onSuccess(JsonObject json) {
                Offer offer = JsonUtils.fromJson(json.get("offer"), Offer.class);
                callback.onSuccess(offer);
            }

            @Override
            public void onFailure(ApiError e) {
                callback.onError(e);
            }
        });
    }

    public static void createEmailShare(@NonNull EmailOfferShare share,
                                        final Callback2<JsonObject, Reward> callback) {
        call("POST", shareUri(share), share, new ApiCallback() {
            @Override
            public void onSuccess(JsonObject json) {
                Reward reward = null;
                if (!JsonUtils.isNull(json.get("reward"))) {
                    reward = JsonUtils.fromJson(json.getAsJsonObject("reward"), Reward.class);
                }
                json.remove("reward");

                callback.onSuccess(json, reward);
            }

            @Override
            public void onFailure(ApiError e) {
                callback.onError(e);
            }
        });
    }

    public static void createSocialShare(@NonNull SocialOfferShare share,
                                         final Callback2<SocialOfferShare, Reward> callback) {
        call("POST", shareUri(share), share, new ApiCallback() {
            @Override
            public void onSuccess(JsonObject json) {
                Reward reward = null;
                if (!JsonUtils.isNull(json.get("reward"))) {
                    reward = JsonUtils.fromJson(json.getAsJsonObject("reward"), Reward.class);
                }

                SocialOfferShare createdShare = null;
                if (!JsonUtils.isNull(json.get("share"))) {
                    createdShare = JsonUtils.fromJson(json.get("share"), SocialOfferShare.class);
                }

                callback.onSuccess(createdShare, reward);
            }

            @Override
            public void onFailure(ApiError e) {
                callback.onError(e);
            }
        });
    }

    private static String shareUri(OfferShare share) {
        if (share == null) {
            throw new NullPointerException("Argument 'share' cannot be null");
        }
        Offer offer = share.getOffer();
        if (offer == null) {
            throw new NullPointerException("Offer cannot be null");
        }
        String shortUrlCode = offer.getCode();
        if (shortUrlCode == null) {
            throw new NullPointerException("Offer's code cannot be null");
        }

        String path = "offers/" + shortUrlCode + "/shares/" + (share instanceof EmailOfferShare ? "email" : "social");
        return UriUtils.apiUriBuilder(path).toString();
    }

    @Deprecated
    public static void createShare(OfferShare share) {
        createShare(share, new Callback2<OfferShare[], Reward>() {
            @Override
            public void onSuccess(OfferShare[] arg1, Reward arg2) {
            }

            @Override
            public void onError(ApiError e) {
            }
        });
    }

    @Deprecated
    public static void createShare(OfferShare share, final Callback2<OfferShare[], Reward> callback) {
        if (share instanceof EmailOfferShare) {
            createEmailShare((EmailOfferShare) share, new Callback2<JsonObject, Reward>() {
                @Override
                public void onSuccess(JsonObject arg1, Reward arg2) {
                    callback.onSuccess(null, arg2);
                }

                @Override
                public void onError(ApiError e) {
                    callback.onError(e);
                }
            });
        } else if (share instanceof SocialOfferShare) {
            createSocialShare((SocialOfferShare) share, new Callback2<SocialOfferShare, Reward>() {
                @Override
                public void onSuccess(SocialOfferShare arg1, Reward arg2) {
                    callback.onSuccess(null, arg2);
                }

                @Override
                public void onError(ApiError e) {
                    callback.onError(e);
                }
            });
        } else {
            throw new IllegalArgumentException(share.getClass().getName() +
                    " is not supported as a 'share' argument");
        }
    }

    public static void trackVisit(VisitorOffer visitorOffer) {
        trackVisit(visitorOffer, new Callback0() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(ApiError e) {

            }
        });
    }

    public static void trackVisit(VisitorOffer visitorOffer, final Callback0 callback) {
        String uri = UriUtils.apiUriBuilder("visitor_offers/" + visitorOffer.getId() + "/track_visit").toString();

        call("PUT", uri, null, new ApiCallback() {
            @Override
            public void onSuccess(JsonObject json) {
                callback.onSuccess();
            }

            @Override
            public void onFailure(ApiError e) {
                callback.onError(e);
            }
        });
    }

    static void call(ApiRequest apiRequest) {
        lowLevelCall(apiRequest.getMethod(), apiRequest.getUrl(), apiRequest.getBody(), new ApiCallback() {
            @Override
            public void onSuccess(JsonObject json) {

            }

            @Override
            public void onFailure(ApiError e) {

            }
        });
    }

    private static void call(String method, String url, final ApiCallback callback) {
        lowLevelCall(method, url, null, callback);
    }

    private static void call(final String method, final String url, final ApiSendable object, final ApiCallback callback) {
        lowLevelCall(method, url, JsonUtils.toJsonTree(object), callback);
    }

    private static void lowLevelCall(final String method, final String url, final JsonElement object, final ApiCallback callback) {
        if (url == null || method == null) return; // TODO: investigate why it happens

        final RequestBody body;
        if (object != null) {
            body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        } else {
            body = RequestBody.create(null, new byte[0]);
        }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(method.toUpperCase(), HttpMethod.requiresRequestBody(method) ? body : null)
                .header("User-Agent", getUserAgent())
                .header("Authorization", "Bearer " + Talkable.getApiKey());

        getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback == null) return;

                if (HTTP_METHODS_NEED_SAVING.contains(method.toUpperCase())) {
                    requestSaver.save(method, url, object);
                }

                callback.onFailure(new ApiError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback == null) return;

                String errorMessage;
                if (response.code() >= 500) {
                    errorMessage = String.format("Server error: %s", response.message());
                    callback.onFailure(new ApiError(errorMessage));
                    return;
                } else if (response.code() >= 400) {
                    errorMessage = String.format("Request can't be processed: %s", response.message());
                    callback.onFailure(new ApiError(errorMessage));
                    return;
                }

                String body = response.body().string();

                JsonObject json, result;
                boolean ok;
                try {
                    json = new JsonParser().parse(body).getAsJsonObject();
                    ok = JsonUtils.getJsonBoolean(json, "ok");
                    result = json.getAsJsonObject("result");
                    errorMessage = JsonUtils.getJsonString(json, "error_message");
                } catch (IllegalStateException ex) {
                    errorMessage = String.format("Response parsing error: %s", ex.getMessage());
                    callback.onFailure(new ApiError(errorMessage));
                    return;
                } catch (ClassCastException ex) {
                    errorMessage = String.format("Response parsing error: %s", ex.getMessage());
                    callback.onFailure(new ApiError(errorMessage));
                    return;
                } catch (JsonSyntaxException ex) {
                    errorMessage = String.format("Response parsing error: %s", ex.getMessage());
                    callback.onFailure(new ApiError(errorMessage));
                    return;
                }

                if (ok) {
                    callback.onSuccess(result);
                } else {
                    callback.onFailure(new ApiError(errorMessage, json));
                }
            }
        });
    }
}
