package com.ptbas.controlcenter.model;

public class PurchaseOrderModel {

    String poPtBasNumber, poCurrency, poDateCreated, poInputDateCreated, poTOP, poTransportType,
            poCustomerName, poNumberCustomer;
    Double poSubTotalBuy, poSubTotalSell, poVAT, poTotalSellFinal, poEstProfit;
    Boolean poStatus;

    public PurchaseOrderModel() {
    }

    public PurchaseOrderModel(String poPtBasNumber, String poCurrency, String poDateCreated, String poInputDateCreated, String poTOP, String poTransportType, String poCustomerName, String poNumberCustomer, Double poSubTotalBuy, Double poSubTotalSell, Double poVAT, Double poTotalSellFinal, Double poEstProfit, Boolean poStatus) {
        this.poPtBasNumber = poPtBasNumber;
        this.poCurrency = poCurrency;
        this.poDateCreated = poDateCreated;
        this.poInputDateCreated = poInputDateCreated;
        this.poTOP = poTOP;
        this.poTransportType = poTransportType;
        this.poCustomerName = poCustomerName;
        this.poNumberCustomer = poNumberCustomer;
        this.poSubTotalBuy = poSubTotalBuy;
        this.poSubTotalSell = poSubTotalSell;
        this.poVAT = poVAT;
        this.poTotalSellFinal = poTotalSellFinal;
        this.poEstProfit = poEstProfit;
        this.poStatus = poStatus;
    }

    public String getPoPtBasNumber() {
        return poPtBasNumber;
    }

    public void setPoPtBasNumber(String poPtBasNumber) {
        this.poPtBasNumber = poPtBasNumber;
    }

    public String getPoCurrency() {
        return poCurrency;
    }

    public void setPoCurrency(String poCurrency) {
        this.poCurrency = poCurrency;
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

    public Double getPoSubTotalBuy() {
        return poSubTotalBuy;
    }

    public void setPoSubTotalBuy(Double poSubTotalBuy) {
        this.poSubTotalBuy = poSubTotalBuy;
    }

    public Double getPoSubTotalSell() {
        return poSubTotalSell;
    }

    public void setPoSubTotalSell(Double poSubTotalSell) {
        this.poSubTotalSell = poSubTotalSell;
    }

    public Double getPoVAT() {
        return poVAT;
    }

    public void setPoVAT(Double poVAT) {
        this.poVAT = poVAT;
    }

    public Double getPoTotalSellFinal() {
        return poTotalSellFinal;
    }

    public void setPoTotalSellFinal(Double poTotalSellFinal) {
        this.poTotalSellFinal = poTotalSellFinal;
    }

    public Double getPoEstProfit() {
        return poEstProfit;
    }

    public void setPoEstProfit(Double poEstProfit) {
        this.poEstProfit = poEstProfit;
    }

    public Boolean getPoStatus() {
        return poStatus;
    }

    public void setPoStatus(Boolean poStatus) {
        this.poStatus = poStatus;
    }
}

