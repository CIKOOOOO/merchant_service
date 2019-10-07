package com.andrew.prototype.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.andrew.prototype.Model.Merchant;
import com.andrew.prototype.R;


public class PrefConfig {
    private SharedPreferences sharedPreferences;
    private Context context;

    public PrefConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file), Context.MODE_PRIVATE);
    }

    public void insertMerchantData(String mid, String name, String location, String profile_picture, String email
            , String background_picture, int coin, int exp, String position, String phone_number
            , String address, String owner_name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_mid), mid);
        editor.putInt(context.getString(R.string.pref_coin), coin);
        editor.putInt(context.getString(R.string.pref_exp), exp);
        editor.putString(context.getString(R.string.pref_name), name);
        editor.putString(context.getString(R.string.pref_location), location);
        editor.putString(context.getString(R.string.pref_profile_picture), profile_picture);
        editor.putString(context.getString(R.string.pref_email), email);
        editor.putString(context.getString(R.string.pref_background_picture), background_picture);
        editor.putString(context.getString(R.string.pref_position), position);
        editor.putString(context.getString(R.string.pref_phone_number), phone_number);
        editor.putString(context.getString(R.string.pref_address), address);
        editor.putString(context.getString(R.string.pref_owner), owner_name);
        editor.commit();
    }

    public void insertPreferencesID(int pref_id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.pref_profile_picture), pref_id);
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
        editor.putInt(context.getString(R.string.pref_id), id);
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

    public int getPrefID() {
        return sharedPreferences.getInt(context.getString(R.string.pref_coin), 0);
    }

    public String getPosition() {
        return sharedPreferences.getString(context.getString(R.string.pref_position), "");
    }

    public String getMID() {
        return sharedPreferences.getString(context.getString(R.string.pref_mid), "");
    }

    public String getName() {
        return sharedPreferences.getString(context.getString(R.string.pref_name), "");
    }

    public String getLocation() {
        return sharedPreferences.getString(context.getString(R.string.pref_location), "");
    }

    public String getOwnerName() {
        return sharedPreferences.getString(context.getString(R.string.pref_owner), "");
    }

    public String getStoreAddress() {
        return sharedPreferences.getString(context.getString(R.string.pref_address), "");
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString(context.getString(R.string.pref_phone_number), "");
    }

    public Merchant getMerchantData() {
        return new Merchant(getMID(), getName(), getLocation(), getProfilePicture(), getEmail()
                , getBackgroundPicture(), getPosition(), getPhoneNumber(), getOwnerName()
                , getStoreAddress(), getCoin(), getExp());
    }
}

