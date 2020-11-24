package com.sr.datasync;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.sr.datasync.data.LocalDataHelper;

public class App extends Application {
    private static LocalDataHelper localDB;

    public static LocalDataHelper getLocalDB() {
        return localDB;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localDB = new LocalDataHelper(getApplicationContext());
    }
}
