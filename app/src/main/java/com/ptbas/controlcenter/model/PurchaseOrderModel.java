package com.ptbas.controlcenter.model;

public class PurchaseOrderModel {

    String poPtBasNumber, poDateCreated, poInputDateCreated, poTOP, poTransportType, poCustomerName, poNumberCustomer;
    Boolean poStatus;

    public PurchaseOrderModel() {
    }

    public PurchaseOrderModel(String poPtBasNumber, String poDateCreated, String poInputDateCreated, String poTOP, String poTransportType, String poCustomerName, String poNumberCustomer, Boolean poStatus) {
        this.poPtBasNumber = poPtBasNumber;
        this.poDateCreated = poDateCreated;
        this.poInputDateCreated = poInputDateCreated;
        this.poTOP = poTOP;
        this.poTransportType = poTransportType;
        this.poCustomerName = poCustomerName;
        this.poNumberCustomer = poNumberCustomer;
        this.poStatus = poStatus;
    }

    public Boolean getPoStatus() {
        return poStatus;
    }

    public void setPoStatus(Boolean poStatus) {
        this.poStatus = poStatus;
    }

    public String getPoPtBasNumber() {
        return poPtBasNumber;
    }

    public void setPoPtBasNumber(String poPtBasNumber) {
        this.poPtBasNumber = poPtBasNumber;
    }

    public String getPoDateCreated() {
        return poDateCreated;
    }

    public void setPoDateCreated(String poDateCreated) {
        this.poDateCreated = poDateCreated;
    }

    public String getPoInputDateCreated() {
        return poInputDateCreated;
    }

    public void setPoInputDateCreated(String poInputDateCreated) {
        this.poInputDateCreated = poInputDateCreated;
    }

    public String getPoTOP() {
        return poTOP;
    }

    public void setPoTOP(String poTOP) {
        this.poTOP = poTOP;
    }

    public String getPoTransportType() {
        return poTransportType;
    }

    public void setPoTransportType(String poTransportType) {
        this.poTransportType = poTransportType;
    }

    public String getPoCustomerName() {
        return poCustomerName;
    }

    public void setPoCustomerName(String poCustomerName) {
        this.poCustomerName = poCustomerName;
    }

    public String getPoNumberCustomer() {
        return poNumberCustomer;
    }

    public void setPoNumberCustomer(String poNumberCustomer) {
        this.poNumberCustomer = poNumberCustomer;
    }
}
