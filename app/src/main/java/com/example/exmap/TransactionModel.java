package com.example.exmap;

public class TransactionModel {
    private String id, amountTrans, category, note, type, date, receipt, time;

    public TransactionModel() {}

    public TransactionModel(String amountTrans, String category, String note, String type, String date, String receipt, String time) {
        this.amountTrans = amountTrans;
        this.category = category;
        this.note = note;
        this.type = type;
        this.date = date;
        this.receipt = receipt;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmountTrans() {
        return amountTrans;
    }

    public void setAmountTrans(String amountTrans) {
        this.amountTrans = amountTrans;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
