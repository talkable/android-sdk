package com.talkable.sdk;

import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * There are some issues with this test.
 * 1. Need to add script to run instrumentation tests on CI
 * 2. Need to fix sdk manifest to be not conflicting with customers one
 */
// TODO: fix test
@RunWith(AndroidJUnit4.class)
public class InstallReferrerReceiverTest {

    @Test
    public void testOnReceive() throws Exception {
        String alternateUuid = "842c93ea-a306-49cd-95c8-350fc8596e6a";

        assertEquals(TalkablePreferencesStore.getAlternateUUID(), null);

        InstallReferrerReceiver receiver = new InstallReferrerReceiver();
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent = new Intent();
        intent.putExtra("referrer", alternateUuid);
        receiver.onReceive(context, intent);

        assertEquals(TalkablePreferencesStore.getAlternateUUID(), alternateUuid);
    }
}
