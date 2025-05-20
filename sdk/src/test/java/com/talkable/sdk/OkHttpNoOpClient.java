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

        private Response createMockResponse() throws IOException {
            // Create a realistic mock response for TalkableApi to parse
            String requestUrl = request.url().toString();
            String responseJson;

            // Different responses based on endpoint being called
            if (requestUrl.contains("/origins")) {
                // Origins endpoint - return a proper purchase structure
                if (requestMethod().equals("POST")) {
                    responseJson = "{\"ok\":true,\"result\":{"
                        + "\"origin\":{"
                        + "\"id\":123,"
                        + "\"type\":\"Purchase\","
                        + "\"subtotal\":10.99,"
                        + "\"order_number\":\"TEST-123\","
                        + "\"coupon_code\":\"TESTCOUPON\""
                        + "},"
                        + "\"offer\":null"
                        + "}}";
                } else {
                    // GET for origins returns an origin with offer
                    responseJson = "{\"ok\":true,\"result\":{"
                        + "\"origin\":{"
                        + "\"id\":456,"
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
                }
            } else if (requestUrl.contains("/visitors")) {
                // Visitor endpoint
                responseJson = "{\"ok\":true,\"result\":{\"uuid\":\"test-uuid\"}}";
            } else if (requestUrl.contains("/events")) {
                // Events endpoint
                responseJson = "{\"ok\":true,\"result\":{"
                    + "\"event\":{"
                    + "\"id\":123,"
                    + "\"category\":\"signup\","
                    + "\"subtotal\":10.99,"
                    + "\"coupon_code\":\"TESTCOUPON\""
                    + "}"
                    + "}}";
            } else if (requestUrl.contains("/affiliate_members")) {
                // Affiliate members endpoint
                responseJson = "{\"ok\":true,\"result\":{"
                    + "\"affiliate_member\":{"
                    + "\"id\":123,"
                    + "\"first_name\":\"Test\","
                    + "\"last_name\":\"User\","
                    + "\"email\":\"test@example.com\""
                    + "}"
                    + "}}";
            } else {
                // Generic response for other endpoints
                responseJson = "{\"ok\":true,\"result\":{\"id\":123}}";
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
