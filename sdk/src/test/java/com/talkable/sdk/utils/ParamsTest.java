package com.talkable.sdk.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParamsTest {

    @Test
    public void testPut() throws Exception {
        Params params = new Params();

        params.put("key", "value");
        assertEquals(Long.valueOf(params.getEntries().size()), Long.valueOf(1));

        params.put("key", "other_value");
        assertEquals(Long.valueOf(params.getEntries().size()), Long.valueOf(2));

        params.put("key", null);
        assertEquals(Long.valueOf(params.getEntries().size()), Long.valueOf(2));
    }
}