package com.andrew.prototype.Model;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImagePicker {
    private Bitmap image_bitmap;
    private String title,type;
    private int img_static;

    public ImagePicker(int img_static, String type) {
        this.img_static = img_static;
        this.type = type;
    }

    public int getImg_static() {
        return img_static;
    }

    public ImagePicker(Bitmap image_bitmap, String type, String title) {
        this.image_bitmap = image_bitmap;
        this.title = title;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage_bitmap() {
        return image_bitmap;
    }
}
