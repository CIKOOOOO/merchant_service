package com.andrew.prototype.Model;

public class MerchantStory {
    private String story_picture, sid, story_date, mid;

    public MerchantStory() {
    }

    public MerchantStory(String story_picture, String sid, String mid, String story_date) {
        this.story_picture = story_picture;
        this.sid = sid;
        this.mid = mid;
        this.story_date = story_date;
    }

    public String getStory_date() {
        return story_date;
    }

    public String getSid() {
        return sid;
    }

    public String getStory_picture() {
        return story_picture;
    }

    public String getMid() {
        return mid;
    }
}
