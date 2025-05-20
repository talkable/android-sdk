package com.talkable.sdk.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OriginTest {
    @Test
    public void testDefaultTrafficSource() throws Exception {
        Origin o = new Origin() { };
        assertEquals(o.getTrafficSource(), Origin.DEFAULT_TRAFFIC_SOURCE);
    }
}
