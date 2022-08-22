package com.ptbas.controlcenter.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceivedOrderModel {

    String roDocumentID, roUID, roCreatedBy, roDateCreated, roTOP, roMatType, roCurrency, roPoCustNumber,
            roCustName;
    Integer roType;
    Double roSubTotalBuy, roSubTotalSell, roVATPPN, roTotalSellFinal, roEstProfit;
    Boolean roStatus;
    HashMap<String, List<ProductItems>> roOrderedItems;

    public ReceivedOrderModel() {
    }

    public ReceivedOrderModel(String roDocumentID, String roUID, String roCreatedBy, String roDateCreated, String roTOP,
                              String roMatType, String roCurrency, String roPoCustNumber,
                              String roCustName, Integer roType, Double roSubTotalBuy, Double roSubTotalSell,
                              Double roVATPPN, Double roTotalSellFinal, Double roEstProfit,
                              Boolean roStatus, HashMap<String, List<ProductItems>> roOrderedItems) {
        this.roDocumentID = roDocumentID;
        this.roUID = roUID;
        this.roCreatedBy = roCreatedBy;
        this.roDateCreated = roDateCreated;
        this.roTOP = roTOP;
        this.roMatType = roMatType;
        this.roCurrency = roCurrency;
        this.roPoCustNumber = roPoCustNumber;
        this.roCustName = roCustName;
        this.roType = roType;
        this.roSubTotalBuy = roSubTotalBuy;
        this.roSubTotalSell = roSubTotalSell;
        this.roVATPPN = roVATPPN;
        this.roTotalSellFinal = roTotalSellFinal;
        this.roEstProfit = roEstProfit;
        this.roStatus = roStatus;
        this.roOrderedItems = roOrderedItems;
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

    public Integer getRoType() {
        return roType;
    }

    public void setRoType(Integer roType) {
        this.roType = roType;
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

    public String getRoDocumentID() {
        return roDocumentID;
    }

    public void setRoDocumentID(String roDocumentID) {
        this.roDocumentID = roDocumentID;
    }

    public void setRoOrderedItems(HashMap<String, List<ProductItems>> roOrderedItems) {
        this.roOrderedItems = roOrderedItems;
    }

    public HashMap<String, List<ProductItems>> getRoOrderedItems() {
        return roOrderedItems;
    }
}

