package com.ptbas.controlcenter.model;

public class CashOutRequestModel {

    String coDateCreated, coApprovedBy, coTotal, coSendToAcc, coCustomer;
    String coDateApproved;
    Boolean coStatus;

    public CashOutRequestModel(String coDateCreated, String coApprovedBy, String coTotal, String coSendToAcc, String coCustomer) {
        this.coDateCreated = coDateCreated;
        this.coApprovedBy = coApprovedBy;
        this.coTotal = coTotal;
        this.coSendToAcc = coSendToAcc;
        this.coCustomer = coCustomer;
    }

    public String getCoDateCreated() {
        return coDateCreated;
    }

    public void setCoDateCreated(String coDateCreated) {
        this.coDateCreated = coDateCreated;
    }

    public String getCoApprovedBy() {
        return coApprovedBy;
    }

    public void setCoApprovedBy(String coApprovedBy) {
        this.coApprovedBy = coApprovedBy;
    }

    public String getCoTotal() {
        return coTotal;
    }

    public void setCoTotal(String coTotal) {
        this.coTotal = coTotal;
    }

    public String getCoSendToAcc() {
        return coSendToAcc;
    }

    public void setCoSendToAcc(String coSendToAcc) {
        this.coSendToAcc = coSendToAcc;
    }

    public String getCoCustomer() {
        return coCustomer;
    }

    public void setCoCustomer(String coCustomer) {
        this.coCustomer = coCustomer;
    }
}
