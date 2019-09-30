package com.andrew.prototype.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class PromoTransaction implements Parcelable {
    private String tid, mid, imageURLForAds, promotionTitle, timeRequest, dateRequest, dateStart, dateEnd, timeStart, timeEnd, promotionCategory, stuffTID, description, statusChecking, adsContent, phoneNumber, officePhoneNumber, emailAddress, address;
    private int adsTemplate, adsColorContent;

    public PromoTransaction(String promotionTitle, String dateStart, String dateEnd, String timeStart, String timeEnd
            , String promotionCategory, String description, String adsContent, String phoneNumber
            , String officePhoneNumber, String emailAddress, int adsTemplate, int adsColorContent, String address) {
        this.promotionTitle = promotionTitle;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.promotionCategory = promotionCategory;
        this.description = description;
        this.adsContent = adsContent;
        this.phoneNumber = phoneNumber;
        this.officePhoneNumber = officePhoneNumber;
        this.emailAddress = emailAddress;
        this.adsTemplate = adsTemplate;
        this.adsColorContent = adsColorContent;
        this.address = address;
    }

    public PromoTransaction() {
    }

    //    public PromoTransaction(String tid, String imageURLForAds, String promotionTitle, String dateStart, String dateEnd, String timeStart, String timeEnd, String promotionCategory, String stuffTID, List<PromoForm> promoFormList) {
//        this.tid = tid;
//        this.imageURLForAds = imageURLForAds;
//        this.promotionTitle = promotionTitle;
//        this.dateStart = dateStart;
//        this.dateEnd = dateEnd;
//        this.timeStart = timeStart;
//        this.timeEnd = timeEnd;
//        this.promotionCategory = promotionCategory;
//        this.stuffTID = stuffTID;
//        this.promoFormList = promoFormList;
//    }


    public PromoTransaction(String tid, String imageURLForAds, String promotionTitle, String timeRequest
            , String dateRequest, String dateStart, String dateEnd, String timeStart, String timeEnd
            , String promotionCategory, String description, String statusChecking
            , String phoneNumber, String officePhoneNumber, String emailAddress, String address) {
        this.tid = tid;
        this.imageURLForAds = imageURLForAds;
        this.promotionTitle = promotionTitle;
        this.timeRequest = timeRequest;
        this.dateRequest = dateRequest;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.promotionCategory = promotionCategory;
        this.description = description;
        this.statusChecking = statusChecking;
        this.phoneNumber = phoneNumber;
        this.officePhoneNumber = officePhoneNumber;
        this.emailAddress = emailAddress;
        this.address = address;
    }

    protected PromoTransaction(Parcel in) {
        tid = in.readString();
        imageURLForAds = in.readString();
        promotionTitle = in.readString();
        dateStart = in.readString();
        dateEnd = in.readString();
        timeStart = in.readString();
        timeEnd = in.readString();
        promotionCategory = in.readString();
        stuffTID = in.readString();
        description = in.readString();
        statusChecking = in.readString();
        adsContent = in.readString();
        adsTemplate = in.readInt();
        adsColorContent = in.readInt();
        phoneNumber = in.readString();
        officePhoneNumber = in.readString();
        emailAddress = in.readString();
        address = in.readString();
    }

    public static final Creator<PromoTransaction> CREATOR = new Creator<PromoTransaction>() {
        @Override
        public PromoTransaction createFromParcel(Parcel in) {
            return new PromoTransaction(in);
        }

        @Override
        public PromoTransaction[] newArray(int size) {
            return new PromoTransaction[size];
        }
    };

    public String getMid() {
        return mid;
    }

    public String getTimeRequest() {
        return timeRequest;
    }

    public String getDateRequest() {
        return dateRequest;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOfficePhoneNumber() {
        return officePhoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getAdsContent() {
        return adsContent;
    }

    public int getAdsTemplate() {
        return adsTemplate;
    }

    public String getDescription() {
        return description;
    }


    public String getStatusChecking() {
        return statusChecking;
    }

    public String getTid() {
        return tid;
    }

    public String getImageURLForAds() {
        return imageURLForAds;
    }

    public String getPromotionTitle() {
        return promotionTitle;
    }

    public String getDateStart() {
        return dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public String getPromotionCategory() {
        return promotionCategory;
    }

    public String getStuffTID() {
        return stuffTID;
    }

    public int getAdsColorContent() {
        return adsColorContent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tid);
        parcel.writeString(imageURLForAds);
        parcel.writeString(promotionTitle);
        parcel.writeString(dateStart);
        parcel.writeString(dateEnd);
        parcel.writeString(timeStart);
        parcel.writeString(timeEnd);
        parcel.writeString(promotionCategory);
        parcel.writeString(stuffTID);
        parcel.writeString(description);
        parcel.writeString(statusChecking);
        parcel.writeInt(adsTemplate);
        parcel.writeString(adsContent);
        parcel.writeInt(adsColorContent);
        parcel.writeString(phoneNumber);
        parcel.writeString(officePhoneNumber);
        parcel.writeString(emailAddress);
        parcel.writeString(address);
    }
}
