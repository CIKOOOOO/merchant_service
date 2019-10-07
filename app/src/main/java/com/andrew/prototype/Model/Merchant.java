package com.andrew.prototype.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Merchant implements Parcelable {

    private String mid, merchant_name, merchant_location, merchant_profile_picture, merchant_email
            , merchant_background_picture, merchant_position, merchant_phone_number, merchant_owner_name
            , merchant_address;
    private int merchant_coin, merchant_exp;

    public Merchant(String mid, String merchant_name, String merchant_location, String merchant_profile_picture, String merchant_email, String merchant_background_picture, String merchant_position, String merchant_phone_number, String merchant_owner_name, String merchant_address, int merchant_coin, int merchant_exp) {
        this.mid = mid;
        this.merchant_name = merchant_name;
        this.merchant_location = merchant_location;
        this.merchant_profile_picture = merchant_profile_picture;
        this.merchant_email = merchant_email;
        this.merchant_background_picture = merchant_background_picture;
        this.merchant_position = merchant_position;
        this.merchant_phone_number = merchant_phone_number;
        this.merchant_owner_name = merchant_owner_name;
        this.merchant_address = merchant_address;
        this.merchant_coin = merchant_coin;
        this.merchant_exp = merchant_exp;
    }

    public Merchant() {
    }

    protected Merchant(Parcel in) {
        mid = in.readString();
        merchant_name = in.readString();
        merchant_location = in.readString();
        merchant_profile_picture = in.readString();
        merchant_email = in.readString();
        merchant_background_picture = in.readString();
        merchant_position = in.readString();
        merchant_phone_number = in.readString();
        merchant_owner_name = in.readString();
        merchant_address = in.readString();
        merchant_coin = in.readInt();
        merchant_exp = in.readInt();
    }

    public static final Creator<Merchant> CREATOR = new Creator<Merchant>() {
        @Override
        public Merchant createFromParcel(Parcel in) {
            return new Merchant(in);
        }

        @Override
        public Merchant[] newArray(int size) {
            return new Merchant[size];
        }
    };

    public String getMid() {
        return mid;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public String getMerchant_location() {
        return merchant_location;
    }

    public String getMerchant_profile_picture() {
        return merchant_profile_picture;
    }

    public String getMerchant_email() {
        return merchant_email;
    }

    public String getMerchant_background_picture() {
        return merchant_background_picture;
    }

    public String getMerchant_position() {
        return merchant_position;
    }

    public int getMerchant_coin() {
        return merchant_coin;
    }

    public int getMerchant_exp() {
        return merchant_exp;
    }

    public String getMerchant_phone_number() {
        return merchant_phone_number;
    }

    public String getMerchant_owner_name() {
        return merchant_owner_name;
    }

    public String getMerchant_address() {
        return merchant_address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mid);
        parcel.writeString(merchant_name);
        parcel.writeString(merchant_location);
        parcel.writeString(merchant_profile_picture);
        parcel.writeString(merchant_email);
        parcel.writeString(merchant_background_picture);
        parcel.writeString(merchant_position);
        parcel.writeString(merchant_phone_number);
        parcel.writeString(merchant_owner_name);
        parcel.writeString(merchant_address);
        parcel.writeInt(merchant_coin);
        parcel.writeInt(merchant_exp);
    }
}
