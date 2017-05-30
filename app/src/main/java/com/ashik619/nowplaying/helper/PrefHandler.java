package com.ashik619.nowplaying.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ashik619 on 14-03-2017.
 */
public class PrefHandler {
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;

    public PrefHandler(Context context) {
        pref = context.getSharedPreferences(SharedPrefConstant.PREFS, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setName(String name) {
        editor.putString(SharedPrefConstant.NAME, name);
        editor.commit();
    }
    public String getName() {
        return pref.getString(SharedPrefConstant.NAME, null);
    }

    public void setImageBaseUrl(String playerId) {
        editor.putString(SharedPrefConstant.PLAYER_ID, playerId);
        editor.commit();
    }
    public String getImageBaseUrl() {
        return pref.getString(SharedPrefConstant.PLAYER_ID, null);
    }

    public void setImageSize(String number) {
        editor.putString(SharedPrefConstant.PHONE_NUMBER, number);
        editor.commit();
    }
    public String getImageSize() {
        return pref.getString(SharedPrefConstant.PHONE_NUMBER, null);
    }

    public void setLargeImageSize(String task) {
        editor.putString(SharedPrefConstant.TASK, task);
        editor.commit();
    }
    public String getLargeImageSize() {
        return pref.getString(SharedPrefConstant.TASK, null);
    }

}
