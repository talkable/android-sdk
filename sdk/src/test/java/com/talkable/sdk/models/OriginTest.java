package com.talkable.sdk.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OriginTest {
    @Test
    public void testDefaultTrafficSource() throws Exception {
        Origin o = new Origin() { };
        assertEquals(o.getTrafficSource(), Origin.DEFAULT_TRAFFIC_SOURCE);
    }
}