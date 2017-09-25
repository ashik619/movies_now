package com.ashik619.nowplaying;

import android.app.Application;

import com.ashik619.nowplaying.helper.PrefHandler;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ashik619 on 23-05-2017.
 */
public class NowPlayingApplication extends Application {

    public static PrefHandler localStorageHandler;
    public static PrefHandler getLocalPrefInstance() {
        return localStorageHandler;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());
        if (localStorageHandler == null) {
            localStorageHandler = new PrefHandler(getApplicationContext());
        }
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
