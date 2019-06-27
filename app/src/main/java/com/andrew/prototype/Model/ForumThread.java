package com.andrew.prototype.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ForumThread implements Parcelable {
    private String title, date, time, username, content, location;
    private int like, profile_picture;
    private boolean isLike;

    // for new reply with image
    public ForumThread(String date, String time, String username, String location, int like
            , int profile_picture, String content) {
        this.date = date;
        this.content = content;
        this.time = time;
        this.username = username;
        this.location = location;
        this.like = like;
        this.profile_picture = profile_picture;
    }

    // for list of reply
    public ForumThread(String date, String time, String content, String username, String location, int like, int profile_picture) {
        this.date = date;
        this.content = content;
        this.profile_picture = profile_picture;
        this.time = time;
        this.username = username;
        this.location = location;
        this.like = like;
    }

    // first set adapter
    public ForumThread(String title, String date, String time, String username, String content, int like, String location) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.username = username;
        this.content = content;
        this.like = like;
    }

    protected ForumThread(Parcel in) {
        title = in.readString();
        date = in.readString();
        time = in.readString();
        username = in.readString();
        content = in.readString();
        location = in.readString();
        like = in.readInt();
        profile_picture = in.readInt();
        isLike = in.readByte() != 0;
    }

    public static final Creator<ForumThread> CREATOR = new Creator<ForumThread>() {
        @Override
        public ForumThread createFromParcel(Parcel in) {
            return new ForumThread(in);
        }

        @Override
        public ForumThread[] newArray(int size) {
            return new ForumThread[size];
        }
    };

    public boolean isLike() {
        return isLike;
    }

    public int getProfile_picture() {
        return profile_picture;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public int getLike() {
        return like;
    }

    public String getLocation() {
        return location;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLike(boolean like) {
        this.isLike = like;
    }

    public void setLikeAmount(int like) {
        this.like = like;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(username);
        parcel.writeString(content);
        parcel.writeString(location);
        parcel.writeInt(like);
        parcel.writeInt(profile_picture);
        parcel.writeByte((byte) (isLike ? 1 : 0));
    }
}
