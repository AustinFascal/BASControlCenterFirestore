package com.ptbas.controlcenter.model;

public class CustModel {

    String custDocumentID, custUID, custName, custAddress, custNPWP, custPhone;
    Boolean custStatus;

    public CustModel() {
    }

    public CustModel(String custDocumentID, String custUID, String custName, String custAddress, String custNPWP, String custPhone, Boolean custStatus) {
        this.custDocumentID = custDocumentID;
        this.custUID = custUID;
        this.custName = custName;
        this.custAddress = custAddress;
        this.custNPWP = custNPWP;
        this.custPhone = custPhone;
        this.custStatus = custStatus;
    }

    public String getCustDocumentID() {
        return custDocumentID;
    }

    public void setCustDocumentID(String custDocumentID) {
        this.custDocumentID = custDocumentID;
    }

    public Boolean getCustStatus() {
        return custStatus;
    }

    public void setCustStatus(Boolean custStatus) {
        this.custStatus = custStatus;
    }

    public String getCustUID() {
        return custUID;
    }

    public void setCustUID(String custUID) {
        this.custUID = custUID;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustAddress() {
        return custAddress;
    }

    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    public String getCustNPWP() {
        return custNPWP;
    }

    public void setCustNPWP(String custNPWP) {
        this.custNPWP = custNPWP;
    }

    public String getCustPhone() {
        return custPhone;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }
}
