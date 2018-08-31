package com.talkable.sdk;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * There are some issues with this test.
 * 1. Need to add script to run instrumentation tests on CI
 * 2. Need to fix sdk manifest to be not conflicting with customers one
 */
// TODO: fix test
@RunWith(AndroidJUnit4.class)
public class InstallReferrerReceiverTest extends ActivityInstrumentationTestCase2<TalkableActivity> {
    public InstallReferrerReceiverTest() {
        super(TalkableActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void testOnReceive() throws Exception {
        String alternateUuid = "842c93ea-a306-49cd-95c8-350fc8596e6a";

        assertEquals(TalkablePreferencesStore.getAlternateUUID(), null);

        InstallReferrerReceiver receiver = new InstallReferrerReceiver();
        Context context = getInstrumentation().getContext();
        Intent intent = new Intent();
        intent.putExtra("referrer", alternateUuid);
        receiver.onReceive(context, intent);

        assertEquals(TalkablePreferencesStore.getAlternateUUID(), alternateUuid);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}