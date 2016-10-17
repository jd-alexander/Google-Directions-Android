package com.directions.sample;

import android.app.Application;

/**
 * Created by rsavutiu on 17/10/2016.
 */

public class SampleApp extends Application {
    private static SampleApp _instance;

    public static SampleApp getInstance() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
    }
}
