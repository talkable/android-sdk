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
                            callback.onResponse(NoOpCall.this, createMockResponse());
                        }
                    }
                }).start();
            }
        }

        private Response createMockResponse() {
            // Create a minimal valid response so callbacks can be properly executed
            ResponseBody body = ResponseBody.create(
                    MediaType.parse("application/json"),
                    "{\"result\":{},\"ok\":true}");

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
