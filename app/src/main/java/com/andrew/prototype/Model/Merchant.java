package com.andrew.prototype.Model;

public class Merchant {

    private String merchant_name, merchant_location, merchant_profile_picture, merchant_email, merchant_background_picture, merchant_position;
    private int MID, merchant_coin, merchant_exp;

    public Merchant() {
    }

    public int getMID() {
        return MID;
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
}
