package com.talkable.sdk;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import okhttp3.OkHttpClient;

/**
 * Helper class for Talkable SDK tests to manage static mocks properly.
 */
public class TestHelper {
    private static final String DEFAULT_API_KEY = "SVd5nKk3PojcjfuKVg";
    private static final String DEFAULT_SITE_SLUG = "android-specs";
    private static final String DEFAULT_SERVER = Talkable.DEFAULT_SERVER; 
    
    /**
     * Creates properly configured static mocks for Talkable and TalkablePreferencesStore.
     * This avoids issues with unfinished stubbing and ensures all tests use the same configuration.
     * 
     * @param uuid UUID to use for mocking
     * @return Array of MockedStatic objects that must be closed with try-with-resources
     */
    public static MockedStatic<?>[] createMockedStatics(String uuid) {
        // Create a no-op HTTP client that won't try to access SSL classes through reflection
        OkHttpClient httpClient = new OkHttpNoOpClient();
        
        // Create the mocked statics
        MockedStatic<Talkable> talkableMock = Mockito.mockStatic(Talkable.class);
        MockedStatic<TalkablePreferencesStore> prefsMock = Mockito.mockStatic(TalkablePreferencesStore.class);
        
        // Set up the standard mocks
        talkableMock.when(Talkable::getApiKey).thenReturn(DEFAULT_API_KEY);
        talkableMock.when(Talkable::getSiteSlug).thenReturn(DEFAULT_SITE_SLUG);
        talkableMock.when(Talkable::getServer).thenReturn(DEFAULT_SERVER);
        talkableMock.when(Talkable::getHttpClient).thenReturn(httpClient);
        prefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(uuid);
        
        // Return the mocked statics to be used in try-with-resources
        return new MockedStatic<?>[] { talkableMock, prefsMock };
    }
    
    /**
     * Override the server URL in the existing Talkable mock.
     * 
     * @param mock The existing mock to update
     * @param server The server URL to use
     */
    public static void setMockServer(MockedStatic<Talkable> mock, String server) {
        mock.when(Talkable::getServer).thenReturn(server);
    }
}
