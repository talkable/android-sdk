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
     * Creates a configured static mock for Talkable.
     * This avoids issues with unfinished stubbing by setting up all the required mocks.
     * 
     * @param uuid UUID to use for mocking
     * @return A MockedStatic for Talkable that must be closed with try-with-resources
     */
    public static MockedStatic<Talkable> createTalkableMock(String uuid) {
        // Create a no-op HTTP client that won't try to access SSL classes through reflection
        OkHttpClient httpClient = new OkHttpNoOpClient();
        
        // Create the mocked static
        MockedStatic<Talkable> talkableMock = Mockito.mockStatic(Talkable.class);
        
        // Set up the standard mocks
        talkableMock.when(Talkable::getApiKey).thenReturn(DEFAULT_API_KEY);
        talkableMock.when(Talkable::getSiteSlug).thenReturn(DEFAULT_SITE_SLUG);
        talkableMock.when(Talkable::getServer).thenReturn(DEFAULT_SERVER);
        talkableMock.when(Talkable::getHttpClient).thenReturn(httpClient);
        
        return talkableMock;
    }
    
    /**
     * Creates a configured static mock for TalkablePreferencesStore.
     * 
     * @param uuid UUID to use for mocking
     * @return A MockedStatic for TalkablePreferencesStore that must be closed with try-with-resources
     */
    public static MockedStatic<TalkablePreferencesStore> createPreferencesMock(String uuid) {
        MockedStatic<TalkablePreferencesStore> prefsMock = Mockito.mockStatic(TalkablePreferencesStore.class);
        prefsMock.when(TalkablePreferencesStore::getMainUUID).thenReturn(uuid);
        return prefsMock;
    }
}
