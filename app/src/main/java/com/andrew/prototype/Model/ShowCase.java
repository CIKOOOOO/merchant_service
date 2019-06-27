package com.andrew.prototype.Model;

import android.graphics.Bitmap;

public class ShowCase {
    private String desc, merchantName;
    private int image;
    private Bitmap imgBitmap;

    public ShowCase(String desc, String merchantName, Bitmap imgBitmap) {
        this.desc = desc;
        this.merchantName = merchantName;
        this.imgBitmap = imgBitmap;
    }

    public ShowCase(String desc, String merchantName, int image) {
        this.desc = desc;
        this.merchantName = merchantName;
        this.image = image;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public String getDesc() {
        return desc;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public int getImage() {
        return image;
    }
}
