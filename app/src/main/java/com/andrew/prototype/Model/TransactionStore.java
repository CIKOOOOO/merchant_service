package com.andrew.prototype.Model;

public class TransactionStore {
    private String tid, date;
    private int bank_logo;
    private long earning;

    public TransactionStore(String tid, String date, int bank_logo, long earning) {
        this.tid = tid;
        this.date = date;
        this.bank_logo = bank_logo;
        this.earning = earning;
    }

    public String getTid() {
        return tid;
    }

    public String getDate() {
        return date;
    }

    public int getBank_logo() {
        return bank_logo;
    }

    public long getEarning() {
        return earning;
    }
}
