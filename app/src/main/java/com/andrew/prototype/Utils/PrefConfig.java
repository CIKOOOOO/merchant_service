package com.andrew.prototype.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.andrew.prototype.R;


public class PrefConfig {
    private SharedPreferences sharedPreferences;
    private Context context;

    public PrefConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file), Context.MODE_PRIVATE);
    }

    public void insertMerchantData(int mid, String name, String location, String profile_picture, String email
            , String background_picture, int coin, int exp, String position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.pref_mid), mid);
        editor.putInt(context.getString(R.string.pref_coin), coin);
        editor.putInt(context.getString(R.string.pref_exp), exp);
        editor.putString(context.getString(R.string.pref_name), name);
        editor.putString(context.getString(R.string.pref_location), location);
        editor.putString(context.getString(R.string.pref_profile_picture), profile_picture);
        editor.putString(context.getString(R.string.pref_email), email);
        editor.putString(context.getString(R.string.pref_background_picture), background_picture);
        editor.putString(context.getString(R.string.pref_position), position);
        editor.commit();
    }

    public void insertProfilePic(String profile_picture) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_profile_picture), profile_picture);
        editor.commit();
    }

    public void insertBackgroundPic(String background_picture) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_background_picture), background_picture);
        editor.commit();
    }

    public void insertId(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(context.getString(R.string.pref_mid), id);
        editor.commit();
    }

    public int getExp() {
        return sharedPreferences.getInt(context.getString(R.string.pref_exp), 0);
    }

    public String getProfilePicture() {
        return sharedPreferences.getString(context.getString(R.string.pref_profile_picture), "");
    }

    public String getEmail() {
        return sharedPreferences.getString(context.getString(R.string.pref_email), "");
    }

    public String getBackgroundPicture() {
        return sharedPreferences.getString(context.getString(R.string.pref_background_picture), "");
    }

    public int getCoin() {
        return sharedPreferences.getInt(context.getString(R.string.pref_coin), 0);
    }

    public String getPosition() {
        return sharedPreferences.getString(context.getString(R.string.pref_position), "");
    }

    public int getMID() {
        return sharedPreferences.getInt(context.getString(R.string.pref_mid), -1);
    }

    public String getName() {
        return sharedPreferences.getString(context.getString(R.string.pref_name), "");
    }

    public String getLocation() {
        return sharedPreferences.getString(context.getString(R.string.pref_location), "");
    }

}

