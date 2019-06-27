package com.andrew.prototype.Model;

public class TransactionStore {
    private String TID, date;
    private int bank_logo;
    private long earning;

    public TransactionStore(String TID, String date, int bank_logo, long earning) {
        this.TID = TID;
        this.date = date;
        this.bank_logo = bank_logo;
        this.earning = earning;
    }

    public String getTID() {
        return TID;
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
