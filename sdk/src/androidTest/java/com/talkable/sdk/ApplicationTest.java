package com.talkable.sdk;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <a href="https://developer.android.com/studio/test">Testing Fundamentals</a>
 */
public class ApplicationTest {
    @Test
    public void testApplication() {
        Application app = ApplicationProvider.getApplicationContext();
        assertNotNull(app);
    }
}
