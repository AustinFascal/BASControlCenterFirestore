package com.ptbas.controlcenter.model;

public class SupplierModel {

    String supplierID, bankName, bankAccountOwnerName, bankAccountNumber, supplierPhoneNumber,
            supplierPayeeName, supplierName;
    Boolean supplierStatus;

    public SupplierModel() {
    }

    public SupplierModel(String supplierID, String bankName, String bankAccountOwnerName, String bankAccountNumber,
                         String supplierPhoneNumber, String supplierPayeeName, String supplierName,
                         Boolean supplierStatus) {
        this.supplierID = supplierID;
        this.bankName = bankName;
        this.bankAccountOwnerName = bankAccountOwnerName;
        this.bankAccountNumber = bankAccountNumber;
        this.supplierPhoneNumber = supplierPhoneNumber;
        this.supplierPayeeName = supplierPayeeName;
        this.supplierName = supplierName;
        this.supplierStatus = supplierStatus;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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

    public String getSupplierPhoneNumber() {
        return supplierPhoneNumber;
    }

    public void setSupplierPhoneNumber(String supplierPhoneNumber) {
        this.supplierPhoneNumber = supplierPhoneNumber;
    }

    public String getSupplierPayeeName() {
        return supplierPayeeName;
    }

    public void setSupplierPayeeName(String supplierPayeeName) {
        this.supplierPayeeName = supplierPayeeName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Boolean getSupplierStatus() {
        return supplierStatus;
    }

    public void setSupplierStatus(Boolean supplierStatus) {
        this.supplierStatus = supplierStatus;
    }


}
