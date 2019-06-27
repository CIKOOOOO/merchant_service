package com.andrew.prototype.Model;

public class Store {

    private String name;
    private int amount_transaction, store_image;
    private long earning;

    public Store(String name, long earning, int amount_transaction, int store_image) {
        this.name = name;
        this.earning = earning;
        this.amount_transaction = amount_transaction;
        this.store_image = store_image;
    }

    public String getName() {
        return name;
    }

    public long getEarning() {
        return earning;
    }

    public int getAmount_transaction() {
        return amount_transaction;
    }

    public int getStore_image() {
        return store_image;
    }
}
