package com.ptbas.controlcenter.model;

public class PurchaseOrder {

    String poPtBasNumber, poDateCreated, poInputDateCreated, poTOP, poTransportType, poCustomerName, poNumberCustomer;

    public PurchaseOrder() {
    }

    public PurchaseOrder(String poPtBasNumber, String poDateCreated, String poInputDateCreated, String poTOP, String poTransportType, String poCustomerName, String poNumberCustomer) {
        this.poPtBasNumber = poPtBasNumber;
        this.poDateCreated = poDateCreated;
        this.poInputDateCreated = poInputDateCreated;
        this.poTOP = poTOP;
        this.poTransportType = poTransportType;
        this.poCustomerName = poCustomerName;
        this.poNumberCustomer = poNumberCustomer;
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
