package com.talkable.sdk;

import android.content.Context;

import com.talkable.sdk.interfaces.RequestSaver;
import com.talkable.sdk.utils.ApiRequest;
import com.talkable.sdk.utils.JsonUtils;
import com.talkable.sdk.utils.PreferencesStore;

import java.util.ArrayList;
import java.util.Set;

public class ContextRequestSaver extends RequestSaver {
    private static final String SHARED_PREFERENCES_KEY = "TKBL_PENDING_REQUESTS";

    private PreferencesStore preferencesStore;

    public ContextRequestSaver(Context context) {
        preferencesStore = new PreferencesStore(context, SHARED_PREFERENCES_KEY);
    }

    @Override
    public void save(ApiRequest apiRequest) {
        String json = JsonUtils.toJson(apiRequest);

        Set<String> set = getEntriesSet();
        set.add(json);

        preferencesStore.putStringSet(SHARED_PREFERENCES_KEY, set);
    }

    @Override
    public ArrayList<ApiRequest> takeEntries() {
        ArrayList<ApiRequest> set = new ArrayList<>();
        for (String entry : getEntriesSet()) {
            set.add(JsonUtils.fromJson(entry, ApiRequest.class));
        }

        if (!set.isEmpty()) {
            preferencesStore.putStringSet(SHARED_PREFERENCES_KEY, null);
        }

        return new ArrayList<>(set);
    }

    private Set<String> getEntriesSet() {
        return preferencesStore.getStringSet(SHARED_PREFERENCES_KEY);
    }
}
