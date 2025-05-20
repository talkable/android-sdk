package com.talkable.sdk;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A minimal OkHttpClient that doesn't use reflection on SSL 
 * classes during construction, which would cause InaccessibleObjectException 
 * in newer JVMs.
 */
public class OkHttpNoOpClient extends OkHttpClient {
    @Override
    public Call newCall(Request request) {
        return new NoOpCall(request);
    }
    
    private class NoOpCall implements Call {
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
            // No-op
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
    }
}
