package com.talkable.sdk;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SynchronizedTest {

    public static final int TIMEOUT = 10;

    public static void sync(Integer asyncCallbacksCount, ResultCallback event) {
        CountDownLatch signal = new CountDownLatch(asyncCallbacksCount);
        event.run(new Result(signal));
        try {
            signal.await(TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        assertEquals(0, signal.getCount(), "See previous errors. Something happened in other threads");
    }

    public static void sync(ResultCallback event) {
        sync(1, event);
    }
}
