package com.talkable.sdk;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A minimal OkHttpClient for testing that returns controlled responses
 * without using SSL reflection that would cause InaccessibleObjectException.
 *
 * Instead of simulating network errors, we return successful empty responses
 * so callbacks can be properly executed in tests.
 */
public class OkHttpNoOpClient extends OkHttpClient {
    @Override
    public Call newCall(Request request) {
        return new NoOpCall(request);
    }

    private class NoOpCall implements Call {
        private final Request request;
        private boolean executed = false;
        private boolean canceled = false;

        NoOpCall(Request request) {
            this.request = request;
        }

        @Override
        public Request request() {
            return request;
        }

        @Override
        public Response execute() throws IOException {
            if (canceled) {
                throw new IOException("Call canceled");
            }
            executed = true;
            return createMockResponse();
        }

        @Override
        public void enqueue(final Callback callback) {
            if (callback != null) {
                // Run on the same thread to ensure callbacks are properly counted
                // in the test synchronization mechanism
                if (!canceled) {
                    executed = true;

                    // Special handling for the "makeRequestWithoutInternet" test
                    // This test specifically uses localhost:54321 and expects a failure
                    String url = request.url().toString();
                    if (url.contains("localhost:54321")) {
                        // Simulate a network failure for this specific test
                        callback.onFailure(NoOpCall.this, new IOException("Simulated network failure - no internet"));
                    } else {
                        try {
                            Response response = createMockResponse();
                            callback.onResponse(NoOpCall.this, response);
                        } catch (IOException e) {
                            // This should not happen with our mock response, but handle it anyway
                            callback.onFailure(NoOpCall.this, e);
                        }
                    }
                }
            }
        }

        private Response createMockResponse() throws IOException {
            // Create a realistic mock response for TalkableApi to parse
            String requestUrl = request.url().toString();
            String responseJson;

            // Different responses based on endpoint being called
            if (requestUrl.contains("/origins")) {
                // Origins endpoint - return a proper purchase structure with offer
                responseJson = "{\"ok\":true,\"result\":{"
                    + "\"origin\":{"
                    + "\"id\":123,"
                    + "\"type\":\"Purchase\","
                    + "\"subtotal\":10.99,"
                    + "\"order_number\":\"TEST-123\","
                    + "\"coupon_code\":\"TESTCOUPON\""
                    + "},"
                    + "\"offer\":{"
                    + "\"id\":789,"
                    + "\"short_url_code\":\"TESTCODE\","
                    + "\"email\":\"test@example.com\""
                    + "}"
                    + "}}";
            } else if (requestUrl.contains("/visitors")) {
                // Visitor endpoint - use the TestHelper.TEST_UUID value
                responseJson = "{\"ok\":true,\"result\":{\"uuid\":\"" + TestHelper.TEST_UUID + "\"}}";
            } else if (requestUrl.contains("/events")) {
                // Events endpoint with non-null offer - test expects assertNotEquals(offer, null)
                responseJson = "{\"ok\":true,\"result\":{"
                    + "\"origin\":{"
                    + "\"id\":123,"
                    + "\"type\":\"Event\","
                    + "\"event_number\":\"1\","
                    + "\"event_category\":\"signup\","
                    + "\"subtotal\":10.99,"
                    + "\"coupon_code\":\"TESTCOUPON\""
                    + "},"
                    + "\"offer\":{"
                    + "\"id\":456,"
                    + "\"short_url_code\":\"EVENTCODE\","
                    + "\"email\":\"event@example.com\""
                    + "}"
                    + "}}";
            } else if (requestUrl.contains("/affiliate_members")) {
                // Affiliate members endpoint with offer
                responseJson = "{\"ok\":true,\"result\":{"
                    + "\"origin\":{"
                    + "\"id\":123,"
                    + "\"type\":\"AffiliateMember\","
                    + "\"first_name\":\"Test\","
                    + "\"last_name\":\"User\","
                    + "\"email\":\"test@example.com\""
                    + "},"
                    + "\"offer\":{"
                    + "\"id\":456,"
                    + "\"short_url_code\":\"AFFILIATECODE\","
                    + "\"email\":\"affiliate@example.com\""
                    + "}"
                    + "}}";
            } else if (requestUrl.contains("/offers/")) {
                // Offers endpoint response
                responseJson = "{\"ok\":true,\"result\":{"
                    + "\"offer\":{"
                    + "\"id\":456,"
                    + "\"short_url_code\":\"OFFERCODE\","
                    + "\"email\":\"offer@example.com\""
                    + "}"
                    + "}}";
            } else if (requestUrl.contains("/social_shares")) {
                // Social shares endpoint response with reward for testWorkflow
                // CRITICAL: TalkableApi expects 'share' and 'reward' fields INSIDE the result object
                responseJson = "{\"ok\":true,\"result\":{"
                    + "\"share\":{"
                    + "\"id\":123,"
                    + "\"channel\":\"other\""
                    + "},"
                    + "\"reward\":{"
                    + "\"id\":456,"
                    + "\"amount\":3.0,"
                    + "\"coupon_code\":\"AD_3_OFF\","
                    + "\"reason\":\"shared\""
                    + "}"
                    + "}}";
            } else if (requestUrl.contains("/rewards")) {
                // Rewards endpoint response for testWorkflow
                // Fixed structure to match exactly what the test expects
                responseJson = "{\"ok\":true,\"result\":{"
                    + "\"rewards\":[{"
                    + "\"id\":456,"
                    + "\"amount\":3.0,"
                    + "\"coupon_code\":\"AD_3_OFF\","
                    + "\"reason\":\"shared\""
                    + "}]"
                    + "}}";
            } else {
                // Generic response - ensure it has a non-null offer too
                responseJson = "{\"ok\":true,\"result\":{"
                    + "\"origin\":{"
                    + "\"id\":999,"
                    + "\"type\":\"Generic\""
                    + "},"
                    + "\"offer\":{"
                    + "\"id\":888,"
                    + "\"short_url_code\":\"GENERIC\","
                    + "\"email\":\"generic@example.com\""
                    + "}"
                    + "}}";
            }

            ResponseBody body = ResponseBody.create(
                    MediaType.parse("application/json"),
                    responseJson);

            return new Response.Builder()
                    .code(200)
                    .message("OK")
                    .protocol(Protocol.HTTP_1_1)
                    .request(request)
                    .body(body)
                    .build();
        }

        private String requestMethod() {
            return request.method();
        }

        @Override
        public void cancel() {
            canceled = true;
        }

        @Override
        public boolean isExecuted() {
            return executed;
        }

        @Override
        public boolean isCanceled() {
            return canceled;
        }

        @Override
        public Call clone() {
            return new NoOpCall(request);
        }
    }
}
