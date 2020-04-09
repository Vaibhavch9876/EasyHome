package com.example.easyhome.model;


import java.io.Serializable;

public class Transaction implements Serializable {

    public String transactionType;
    public String transactionCategory;
    public String transactionDescription;

    public int transactionId;
    public String transactionDate;
    public Double transactionAmount;

    public String transactionSource = null;
    public String transactionPayee = null;
    public String transactionComment = null;

    public Transaction() {
    }

    public Transaction(String transactionType, String transactionCategory, String transactionDescription, int transactionId, String transactionDate, Double transactionAmount) {
        this.transactionType = transactionType;
        this.transactionCategory = transactionCategory;
        this.transactionDescription = transactionDescription;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.transactionAmount = transactionAmount;
    }

    public Transaction(String transactionType, String transactionCategory, String transactionDescription, int transactionId, String transactionDate, Double transactionAmount, String transactionSource, String transactionPayee, String transactionComment) {
        this.transactionType = transactionType;
        this.transactionCategory = transactionCategory;
        this.transactionDescription = transactionDescription;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.transactionAmount = transactionAmount;
        this.transactionSource = transactionSource;
        this.transactionPayee = transactionPayee;
        this.transactionComment = transactionComment;
    }


    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionCategory() {
        return transactionCategory;
    }

    public void setTransactionCategory(String transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionSource() {
        return transactionSource;
    }

    public void setTransactionSource(String transactionSource) {
        this.transactionSource = transactionSource;
    }

    public String getTransactionPayee() {
        return transactionPayee;
    }

    public void setTransactionPayee(String transactionPayee) {
        this.transactionPayee = transactionPayee;
    }

    public String getTransactionComment() {
        return transactionComment;
    }

    public void setTransactionComment(String transactionComment) {
        this.transactionComment = transactionComment;
    }
}
