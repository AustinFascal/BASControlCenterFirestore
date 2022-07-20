package com.ptbas.controlcenter.model;

public class VehicleModel {

    String vhlManufactureYear, vhlRegistNumber, vhlBrand, vhlIdentityNumber, vhlEngineNumber, dateCreated, dateModified, vhlStatus;
    Integer vhlLength, vhlWidth, vhlHeight;

    VehicleModel() {
    }

    public VehicleModel(String vhlRegistNumber, String vhlBrand,
                        String vhlIdentityNumber, String vhlEngineNumber,String dateCreated, String dateModified,
                        String vhlManufactureYear, Integer vhlLength, Integer vhlWidth,
                        Integer vhlHeight, String vhlStatus) {
        this.vhlRegistNumber = vhlRegistNumber;
        this.vhlBrand = vhlBrand;
        this.vhlIdentityNumber = vhlIdentityNumber;
        this.vhlEngineNumber = vhlEngineNumber;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.vhlManufactureYear = vhlManufactureYear;
        this.vhlLength = vhlLength;
        this.vhlWidth = vhlWidth;
        this.vhlHeight = vhlHeight;
        this.vhlStatus = vhlStatus;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getVhlRegistNumber() {
        return vhlRegistNumber;
    }

    public void setVhlRegistNumber(String vhlRegistNumber) {
        this.vhlRegistNumber = vhlRegistNumber;
    }

    public String getVhlBrand() {
        return vhlBrand;
    }

    public void setVhlBrand(String vhlBrand) {
        this.vhlBrand = vhlBrand;
    }

    public String getVhlIdentityNumber() {
        return vhlIdentityNumber;
    }

    public void setVhlIdentityNumber(String vhlIdentityNumber) {
        this.vhlIdentityNumber = vhlIdentityNumber;
    }

    public String getVhlEngineNumber() {
        return vhlEngineNumber;
    }

    public void setVhlEngineNumber(String vhlEngineNumber) {
        this.vhlEngineNumber = vhlEngineNumber;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getVhlManufactureYear() {
        return vhlManufactureYear;
    }

    public void setVhlManufactureYear(String vhlManufactureYear) {
        this.vhlManufactureYear = vhlManufactureYear;
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

    public String getVhlStatus() {
        return vhlStatus;
    }

    public void setVhlStatus(String vhlStatus) {
        this.vhlStatus = vhlStatus;
    }
}
