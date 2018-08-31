package com.talkable.sdk.utils;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

public class Params {
    public class Entry extends SimpleEntry<String, String> {
        public Entry(String theKey, String theValue) {
            super(theKey, theValue);
        }
    }

    private ArrayList<Entry> entries = new ArrayList<>();

    public void put(String key, String value) {
        if (value != null) {
            entries.add(new Entry(key, value));
        }
    }
    public void put(String key, Object value) {
        entries.add(new Entry(key, value.toString()));
    }

    public boolean containsKey(String key) {
        for(Entry e : getEntries()) {
            if (e.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }
}
