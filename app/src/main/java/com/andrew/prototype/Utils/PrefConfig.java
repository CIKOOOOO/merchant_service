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

    public void userData(String name, String address) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_name), name);
        editor.putString(context.getString(R.string.pref_location), address);
        editor.commit();
    }

    public void insertId(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.pref_id), id);
        editor.commit();
    }

    public int getID() {
        return sharedPreferences.getInt(context.getString(R.string.pref_id), -1);
    }

    public String getName() {
        return sharedPreferences.getString(context.getString(R.string.pref_name), "Andrew Abednego Gunawan");
    }

    public String getAddress() {
        return sharedPreferences.getString(context.getString(R.string.pref_location), "Bekasi Timur");
    }

}

