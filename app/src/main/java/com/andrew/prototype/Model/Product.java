package com.andrew.prototype.Model;

public class Product {
    private String productName, PID, URLImage;
    private int productValue;

    public Product() {
    }

    public Product(String productName, String PID, String URLImage, int productValue) {
        this.productName = productName;
        this.PID = PID;
        this.URLImage = URLImage;
        this.productValue = productValue;
    }

    public String getProductName() {
        return productName;
    }

    public String getPID() {
        return PID;
    }

    public String getURLImage() {
        return URLImage;
    }

    public int getProductValue() {
        return productValue;
    }
}
