package com.ptbas.controlcenter.model;

public class GoodIssueModel {

    String giUID, giPoCustNumber, giPoBasNumber, giMaterialName, giMaterialType, giVhlRegistNumber,
            giTimeCreated, giDateCreated, giInputDateCreated, giInputDateModified;
    Integer giHeightCorrection, vhlLength, vhlWidth, vhlHeight;
    Float giVhlCubication;
    Boolean giStatus;

    GoodIssueModel() {
    }

    public GoodIssueModel(String giUID, String giPoCustNumber, String giPoBasNumber, String giMaterialName,
                          String giMaterialType, String giVhlRegistNumber, Integer giHeightCorrection, String giTimeCreated,
                          String giDateCreated, String giInputDateCreated, String giInputDateModified,
                          Integer vhlLength, Integer vhlWidth, Integer vhlHeight,
                          Float giVhlCubication, Boolean giStatus) {
        this.giUID = giUID;
        this.giPoCustNumber = giPoCustNumber;
        this.giPoBasNumber = giPoBasNumber;
        this.giMaterialName = giMaterialName;
        this.giMaterialType = giMaterialType;
        this.giVhlRegistNumber = giVhlRegistNumber;
        this.giTimeCreated = giTimeCreated;
        this.giDateCreated = giDateCreated;
        this.giInputDateCreated = giInputDateCreated;
        this.giInputDateModified = giInputDateModified;
        this.giHeightCorrection = giHeightCorrection;
        this.vhlLength = vhlLength;
        this.vhlWidth = vhlWidth;
        this.vhlHeight = vhlHeight;
        this.giVhlCubication = giVhlCubication;
        this.giStatus = giStatus;
    }

    public String getGiUID() {
        return giUID;
    }

    public void setGiUID(String giUID) {
        this.giUID = giUID;
    }

    public String getGiPoCustNumber() {
        return giPoCustNumber;
    }

    public void setGiPoCustNumber(String giPoCustNumber) {
        this.giPoCustNumber = giPoCustNumber;
    }

    public String getGiPoBasNumber() {
        return giPoBasNumber;
    }

    public void setGiPoBasNumber(String giPoBasNumber) {
        this.giPoBasNumber = giPoBasNumber;
    }

    public String getGiMaterialName() {
        return giMaterialName;
    }

    public void setGiMaterialName(String giMaterialName) {
        this.giMaterialName = giMaterialName;
    }

    public String getGiMaterialType() {
        return giMaterialType;
    }

    public void setGiMaterialType(String giMaterialType) {
        this.giMaterialType = giMaterialType;
    }

    public String getGiVhlRegistNumber() {
        return giVhlRegistNumber;
    }

    public void setGiVhlRegistNumber(String giVhlRegistNumber) {
        this.giVhlRegistNumber = giVhlRegistNumber;
    }

    public String getGiTimeCreated() {
        return giTimeCreated;
    }

    public void setGiTimeCreated(String giTimeCreated) {
        this.giTimeCreated = giTimeCreated;
    }

    public String getGiDateCreated() {
        return giDateCreated;
    }

    public void setGiDateCreated(String giDateCreated) {
        this.giDateCreated = giDateCreated;
    }

    public String getGiInputDateCreated() {
        return giInputDateCreated;
    }

    public void setGiInputDateCreated(String giInputDateCreated) {
        this.giInputDateCreated = giInputDateCreated;
    }

    public String getGiInputDateModified() {
        return giInputDateModified;
    }

    public void setGiInputDateModified(String giInputDateModified) {
        this.giInputDateModified = giInputDateModified;
    }

    public Integer getGiHeightCorrection() {
        return giHeightCorrection;
    }

    public void setGiHeightCorrection(Integer giHeightCorrection) {
        this.giHeightCorrection = giHeightCorrection;
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
}
