package com.ptbas.controlcenter.model;

public class CustomerModel {

    String custUID, custName, custAlias, custAddress, custNPWP, custPhone;

    public CustomerModel() {
    }

    public CustomerModel(String custUID, String custName, String custAlias, String custAddress, String custNPWP, String custPhone) {
        this.custUID = custUID;
        this.custName = custName;
        this.custAlias = custAlias;
        this.custAddress = custAddress;
        this.custNPWP = custNPWP;
        this.custPhone = custPhone;
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

    public String getCustAlias() {
        return custAlias;
    }

    public void setCustAlias(String custAlias) {
        this.custAlias = custAlias;
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
