package com.talkable.sdk;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A minimal no-op OkHttpClient to use in tests without making actual network calls
 * or performing SSL reflection that causes InaccessibleObjectException in newer JVMs.
 */
public class OkHttpNoOpClient extends OkHttpClient {
    
    @Override
    public Call newCall(Request request) {
        return new NoOpCall(request);
    }
    
    private static class NoOpCall implements Call {
        private final Request request;
        
        NoOpCall(Request request) {
            this.request = request;
        }
        
        @Override
        public Request request() {
            return request;
        }
        
        @Override
        public Response execute() throws IOException {
            throw new IOException("Simulated network failure in test");
        }
        
        @Override
        public void enqueue(Callback callback) {
            if (callback != null) {
                callback.onFailure(this, new IOException("Simulated network failure in test"));
            }
        }
        
        @Override
        public void cancel() {
        }
        
        @Override
        public boolean isExecuted() {
            return false;
        }
        
        @Override
        public boolean isCanceled() {
            return false;
        }
        
        @Override
        public Call clone() {
            return new NoOpCall(request);
        }
        
        // In newer versions of OkHttp, this method is required
        // Since we're mocking, we can just return null
        @Override
        public okio.Timeout timeout() {
            return null;
        }
    }
}
