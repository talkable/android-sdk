package com.talkable.demo;

import android.app.Application;

import com.talkable.sdk.Talkable;

public class DemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Talkable.initialize(this,null,true,null);
    }
}
