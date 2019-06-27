package com.andrew.prototype.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class PromoForm implements Parcelable {
    private String stuffName;
    private Bitmap bitmap;
    private int cashback, installment, discount;
    private long special_price;

    public PromoForm() {

    }

    public PromoForm(String stuffName) {
        this.stuffName = stuffName;
    }

    protected PromoForm(Parcel in) {
        stuffName = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        cashback = in.readInt();
        installment = in.readInt();
        discount = in.readInt();
        special_price = in.readLong();
    }

    public static final Creator<PromoForm> CREATOR = new Creator<PromoForm>() {
        @Override
        public PromoForm createFromParcel(Parcel in) {
            return new PromoForm(in);
        }

        @Override
        public PromoForm[] newArray(int size) {
            return new PromoForm[size];
        }
    };

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setCashback(int cashback) {
        this.cashback = cashback;
    }

    public void setInstallment(int installment) {
        this.installment = installment;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setSpecial_price(long special_price) {
        this.special_price = special_price;
    }

    public String getStuffName() {
        return stuffName;
    }

    public int getCashback() {
        return cashback;
    }

    public int getInstallment() {
        return installment;
    }

    public int getDiscount() {
        return discount;
    }

    public long getSpecial_price() {
        return special_price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(stuffName);
        parcel.writeParcelable(bitmap, i);
        parcel.writeInt(cashback);
        parcel.writeInt(installment);
        parcel.writeInt(discount);
        parcel.writeLong(special_price);
    }
}
