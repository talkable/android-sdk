package com.talkable.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedHashSet;
import java.util.Set;

public class PreferencesStore {

    private SharedPreferences sharedPreferences;

    public PreferencesStore(Context context, String sharedPreferencesName) {
        this.sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void putStringSet(String key, Set<String> values) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, values);
        editor.apply();
    }

    public Set<String> getStringSet(String key) {
        return sharedPreferences.getStringSet(key, new LinkedHashSet<String>());
    }

    public void appendStringSet(String key, String value) {
        Set<String> stringSet = getStringSet(key);
        stringSet.add(value);
        putStringSet(key, stringSet);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
