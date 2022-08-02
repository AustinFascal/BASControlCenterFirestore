package com.ptbas.controlcenter.model;

public class GoodIssueModel {

    //MANDATORY
    String giUID, giCreatedBy, giRoUID, giPoCustNumber, giMatName, giMatType, vhlUID,
            giDateCreated, giTimeCreted;
    Integer vhlLength, vhlWidth, vhlHeight, vhlHeightCorrection, vhlHeightAfterCorrection;
    Float giVhlCubication;
    Boolean giStatus, giInvoiced;

    GoodIssueModel() {
    }

    public GoodIssueModel(String giUID, String giCreatedBy, String giRoUID, String giPoCustNumber, String giMatName, String giMatType, String vhlUID, String giDateCreated, String giTimeCreted, Integer vhlLength, Integer vhlWidth, Integer vhlHeight, Integer vhlHeightCorrection, Integer vhlHeightAfterCorrection, Float giVhlCubication, Boolean giStatus, Boolean giInvoiced) {
        this.giUID = giUID;
        this.giCreatedBy = giCreatedBy;
        this.giRoUID = giRoUID;
        this.giPoCustNumber = giPoCustNumber;
        this.giMatName = giMatName;
        this.giMatType = giMatType;
        this.vhlUID = vhlUID;
        this.giDateCreated = giDateCreated;
        this.giTimeCreted = giTimeCreted;
        this.vhlLength = vhlLength;
        this.vhlWidth = vhlWidth;
        this.vhlHeight = vhlHeight;
        this.vhlHeightCorrection = vhlHeightCorrection;
        this.vhlHeightAfterCorrection = vhlHeightAfterCorrection;
        this.giVhlCubication = giVhlCubication;
        this.giStatus = giStatus;
        this.giInvoiced = giInvoiced;
    }

    public String getGiUID() {
        return giUID;
    }

    public void setGiUID(String giUID) {
        this.giUID = giUID;
    }

    public String getGiCreatedBy() {
        return giCreatedBy;
    }

    public void setGiCreatedBy(String giCreatedBy) {
        this.giCreatedBy = giCreatedBy;
    }

    public String getGiRoUID() {
        return giRoUID;
    }

    public void setGiRoUID(String giRoUID) {
        this.giRoUID = giRoUID;
    }

    public String getGiPoCustNumber() {
        return giPoCustNumber;
    }

    public void setGiPoCustNumber(String giPoCustNumber) {
        this.giPoCustNumber = giPoCustNumber;
    }

    public String getGiMatName() {
        return giMatName;
    }

    public void setGiMatName(String giMatName) {
        this.giMatName = giMatName;
    }

    public String getGiMatType() {
        return giMatType;
    }

    public void setGiMatType(String giMatType) {
        this.giMatType = giMatType;
    }

    public String getVhlUID() {
        return vhlUID;
    }

    public void setVhlUID(String vhlUID) {
        this.vhlUID = vhlUID;
    }

    public String getGiDateCreated() {
        return giDateCreated;
    }

    public void setGiDateCreated(String giDateCreated) {
        this.giDateCreated = giDateCreated;
    }

    public String getGiTimeCreted() {
        return giTimeCreted;
    }

    public void setGiTimeCreted(String giTimeCreted) {
        this.giTimeCreted = giTimeCreted;
    }

    public Integer getVhlLength() {
        return vhlLength;
    }

    public void setVhlLength(Integer vhlLength) {
        this.vhlLength = vhlLength;
    }

    public Integer getVhlWidth() {
        return vhlWidth;
    }

    public void setVhlWidth(Integer vhlWidth) {
        this.vhlWidth = vhlWidth;
    }

    public Integer getVhlHeight() {
        return vhlHeight;
    }

    public void setVhlHeight(Integer vhlHeight) {
        this.vhlHeight = vhlHeight;
    }

    public Integer getVhlHeightCorrection() {
        return vhlHeightCorrection;
    }

    public void setVhlHeightCorrection(Integer vhlHeightCorrection) {
        this.vhlHeightCorrection = vhlHeightCorrection;
    }

    public Integer getVhlHeightAfterCorrection() {
        return vhlHeightAfterCorrection;
    }

    public void setVhlHeightAfterCorrection(Integer vhlHeightAfterCorrection) {
        this.vhlHeightAfterCorrection = vhlHeightAfterCorrection;
    }

    public Float getGiVhlCubication() {
        return giVhlCubication;
    }

    public void setGiVhlCubication(Float giVhlCubication) {
        this.giVhlCubication = giVhlCubication;
    }

    public Boolean getGiStatus() {
        return giStatus;
    }

    public void setGiStatus(Boolean giStatus) {
        this.giStatus = giStatus;
    }

    public Boolean getGiInvoiced() {
        return giInvoiced;
    }

    public void setGiInvoiced(Boolean giInvoiced) {
        this.giInvoiced = giInvoiced;
    }
}
