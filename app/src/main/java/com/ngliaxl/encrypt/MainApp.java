package com.ngliaxl.encrypt;

import android.app.Application;
import android.content.Context;


public class MainApp extends Application {
    private static Context mContext;
    private static MainApp sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        sApp = this;
    }

    public static MainApp getApp() {
        return sApp;
    }


    public static Context getContext() {
        return mContext;
    }


}
