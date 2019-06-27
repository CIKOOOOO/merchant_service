package com.andrew.prototype.Model;

public class ProfileModel {
    private String parent, child;

    public String getParent() {
        return parent;
    }

    public String getChild() {
        return child;
    }

    public ProfileModel(String parent, String child) {

        this.parent = parent;
        this.child = child;
    }
}
