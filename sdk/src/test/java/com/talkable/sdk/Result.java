package com.talkable.sdk;

import java.util.concurrent.CountDownLatch;

public class Result {
    private CountDownLatch counter;

    public Result(CountDownLatch counter) {
        this.counter = counter;
    }

    public void done() {
        counter.countDown();
    }
}
