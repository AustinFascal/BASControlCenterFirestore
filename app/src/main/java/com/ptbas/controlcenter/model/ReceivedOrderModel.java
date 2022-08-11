package com.ptbas.controlcenter.model;

public class ReceivedOrderModel {

    String roUID, roCreatedBy, roDateCreated, roTOP, roMatType, roCurrency, roPoCustNumber,
            roCustName;
    Double roSubTotalBuy, roSubTotalSell, roVATPPN, roTotalSellFinal, roEstProfit;
    Boolean roStatus;

    public ReceivedOrderModel() {
    }

    public ReceivedOrderModel(String roUID, String roCreatedBy, String roDateCreated, String roTOP,
                              String roMatType, String roCurrency, String roPoCustNumber,
                              String roCustName, Double roSubTotalBuy, Double roSubTotalSell,
                              Double roVATPPN, Double roTotalSellFinal, Double roEstProfit,
                              Boolean roStatus) {
        this.roUID = roUID;
        this.roCreatedBy = roCreatedBy;
        this.roDateCreated = roDateCreated;
        this.roTOP = roTOP;
        this.roMatType = roMatType;
        this.roCurrency = roCurrency;
        this.roPoCustNumber = roPoCustNumber;
        this.roCustName = roCustName;
        this.roSubTotalBuy = roSubTotalBuy;
        this.roSubTotalSell = roSubTotalSell;
        this.roVATPPN = roVATPPN;
        this.roTotalSellFinal = roTotalSellFinal;
        this.roEstProfit = roEstProfit;
        this.roStatus = roStatus;
    }

    public String getRoUID() {
        return roUID;
    }

    public void setRoUID(String roUID) {
        this.roUID = roUID;
    }

    public String getRoCreatedBy() {
        return roCreatedBy;
    }

    public void setRoCreatedBy(String roCreatedBy) {
        this.roCreatedBy = roCreatedBy;
    }

    public String getRoDateCreated() {
        return roDateCreated;
    }

    public void setRoDateCreated(String roDateCreated) {
        this.roDateCreated = roDateCreated;
    }

    public String getRoTOP() {
        return roTOP;
    }

    public void setRoTOP(String roTOP) {
        this.roTOP = roTOP;
    }

    public String getRoMatType() {
        return roMatType;
    }

    public void setRoMatType(String roMatType) {
        this.roMatType = roMatType;
    }

    public String getRoCurrency() {
        return roCurrency;
    }

    public void setRoCurrency(String roCurrency) {
        this.roCurrency = roCurrency;
    }

    public String getRoPoCustNumber() {
        return roPoCustNumber;
    }

    public void setRoPoCustNumber(String roPoCustNumber) {
        this.roPoCustNumber = roPoCustNumber;
    }

    public String getRoCustName() {
        return roCustName;
    }

    public void setRoCustName(String roCustName) {
        this.roCustName = roCustName;
    }

    public Double getRoSubTotalBuy() {
        return roSubTotalBuy;
    }

    public void setRoSubTotalBuy(Double roSubTotalBuy) {
        this.roSubTotalBuy = roSubTotalBuy;
    }

    public Double getRoSubTotalSell() {
        return roSubTotalSell;
    }

    public void setRoSubTotalSell(Double roSubTotalSell) {
        this.roSubTotalSell = roSubTotalSell;
    }

    public Double getRoVATPPN() {
        return roVATPPN;
    }

    public void setRoVATPPN(Double roVATPPN) {
        this.roVATPPN = roVATPPN;
    }

    public Double getRoTotalSellFinal() {
        return roTotalSellFinal;
    }

    public void setRoTotalSellFinal(Double roTotalSellFinal) {
        this.roTotalSellFinal = roTotalSellFinal;
    }

    public Double getRoEstProfit() {
        return roEstProfit;
    }

    public void setRoEstProfit(Double roEstProfit) {
        this.roEstProfit = roEstProfit;
    }

    public Boolean getRoStatus() {
        return roStatus;
    }

    public void setRoStatus(Boolean roStatus) {
        this.roStatus = roStatus;
    }
}
