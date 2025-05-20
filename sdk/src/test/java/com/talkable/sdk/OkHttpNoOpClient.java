package com.talkable.sdk;

import java.io.IOException;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.ws.RealWebSocket;
import java.util.concurrent.TimeUnit;

/**
 * A no-op OkHttpClient to use in tests without making actual network calls or 
 * performing SSL reflection that causes InaccessibleObjectException in newer JVMs.
 * This avoids the reflection used by OkHttp that accesses SSL classes.
 */
public class OkHttpNoOpClient extends OkHttpClient {

    public OkHttpNoOpClient() {
        // Empty constructor - doesn't use super() to avoid SSL reflection
    }
    
    // Provide implementations for commonly used methods
    @Override
    public List<Protocol> protocols() {
        return Collections.singletonList(Protocol.HTTP_1_1);
    }
    
    @Override
    public int connectTimeoutMillis() {
        return 10000; // 10 seconds
    }
    
    @Override
    public int readTimeoutMillis() {
        return 10000; // 10 seconds
    }
    
    @Override
    public int writeTimeoutMillis() {
        return 10000; // 10 seconds
    }
    
    @Override
    public Call newCall(Request request) {
        return new NoOpCall(request);
    }
    
    private static class NoOpCall implements Call {
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
            executed = true;
            // For testing purposes, we'll simulate a network error in execute
            throw new IOException("Simulated network failure in test");
        }
        
        @Override
        public void enqueue(Callback callback) {
            executed = true;
            if (callback != null) {
                callback.onFailure(this, new IOException("Simulated network failure in test"));
            }
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
        
        @Override
        public Timeout timeout() {
            return new Timeout() {
                @Override
                public Timeout timeout(long timeout, TimeUnit unit) {
                    return this;
                }

                @Override
                public long timeoutNanos() {
                    return 0;
                }

                @Override
                public boolean hasDeadline() {
                    return false;
                }

                @Override
                public long deadlineNanoTime() {
                    return 0;
                }

                @Override
                public Timeout deadlineNanoTime(long deadlineNanoTime) {
                    return this;
                }

                @Override
                public Timeout clearTimeout() {
                    return this;
                }

                @Override
                public Timeout clearDeadline() {
                    return this;
                }

                @Override
                public void throwIfReached() throws IOException {
                    // Do nothing
                }
            };
        }
    }
}
