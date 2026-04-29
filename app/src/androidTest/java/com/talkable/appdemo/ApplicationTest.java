package com.talkable.appdemo;

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
        // Getting the application context using AndroidX ApplicationProvider
        Application app = ApplicationProvider.getApplicationContext();
        assertNotNull(app);  // Assert that the Application context is not null
    }
}
