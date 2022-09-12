package com.ptbas.controlcenter.model;

public class BankAccountModel {

    String bacnkAccountID, bankName, bankType, bankAccountOwnerName, bankAccountNumber;
    Boolean bankStatus;

    public BankAccountModel() {
    }

    public BankAccountModel(String bacnkAccountID, String bankName, String bankType, String bankAccountOwnerName, String bankAccountNumber, Boolean bankStatus) {
        this.bacnkAccountID = bacnkAccountID;
        this.bankName = bankName;
        this.bankType = bankType;
        this.bankAccountOwnerName = bankAccountOwnerName;
        this.bankAccountNumber = bankAccountNumber;
        this.bankStatus = bankStatus;
    }

    public String getBacnkAccountID() {
        return bacnkAccountID;
    }

    public void setBacnkAccountID(String bacnkAccountID) {
        this.bacnkAccountID = bacnkAccountID;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getBankAccountOwnerName() {
        return bankAccountOwnerName;
    }

    public void setBankAccountOwnerName(String bankAccountOwnerName) {
        this.bankAccountOwnerName = bankAccountOwnerName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public Boolean getBankStatus() {
        return bankStatus;
    }

    public void setBankStatus(Boolean bankStatus) {
        this.bankStatus = bankStatus;
    }
}