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
                // Instead of failing, return a successful empty response
                // This ensures that our test callbacks are properly invoked
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!canceled) {
                            executed = true;
                            try {
                                callback.onResponse(NoOpCall.this, createMockResponse());
                            } catch (IOException e) {
                                // This should not happen with our mock response, but handle it anyway
                                callback.onFailure(NoOpCall.this, e);
                            }
                        }
                    }
                }).start();
            }
        }

        private Response createMockResponse() throws IOException {
            // Create a realistic mock response for TalkableApi to parse
            String requestUrl = request.url().toString();
            String responseJson;

            // Different responses based on endpoint being called
            if (requestUrl.contains("/origins")) {
                responseJson = "{\"ok\":true,\"result\":{\"origin\":{\"id\":123,\"type\":\"Purchase\"},\"offer\":null}}";
            } else if (requestUrl.contains("/visitors")) {
                responseJson = "{\"ok\":true,\"result\":{\"uuid\":\"test-uuid\"}}";
            } else {
                // Generic response for other endpoints
                responseJson = "{\"ok\":true,\"result\":{}}";
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
