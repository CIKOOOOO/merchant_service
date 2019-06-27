package com.andrew.prototype.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class SyncImg implements Parcelable {
    private String name, fileName;
    private Bitmap img;

    public String getName() {
        return name;
    }

    public Bitmap getImg() {
        return img;
    }

    public String getFileName() {
        return fileName;
    }

    public SyncImg(String name, Bitmap img, String fileName) {
        this.name = name;
        this.fileName = fileName;
        this.img = img;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(fileName);
        parcel.writeParcelable(img, i);
    }

    protected SyncImg(Parcel in) {
        name = in.readString();
        fileName = in.readString();
        img = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<SyncImg> CREATOR = new Creator<SyncImg>() {
        @Override
        public SyncImg createFromParcel(Parcel in) {
            return new SyncImg(in);
        }

        @Override
        public SyncImg[] newArray(int size) {
            return new SyncImg[size];
        }
    };
}
