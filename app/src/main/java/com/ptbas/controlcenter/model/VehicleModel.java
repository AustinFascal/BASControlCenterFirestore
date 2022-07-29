package com.ptbas.controlcenter.model;

public class VehicleModel {

    //MANDATORY
    String vhlUID;
    Boolean vhlStatus;
    Integer vhlLength, vhlWidth, vhlHeight;

    //OPTIONAL
    String vhlBrand, vhlEngineNumb, vhlIdentityNumber, vhlManufactureYear;

    VehicleModel() {
    }

    public VehicleModel(String vhlUID, Boolean vhlStatus, Integer vhlLength, Integer vhlWidth,
                        Integer vhlHeight, String vhlBrand, String vhlEngineNumb,
                        String vhlIdentityNumber, String vhlManufactureYear) {
        this.vhlUID = vhlUID;
        this.vhlStatus = vhlStatus;
        this.vhlLength = vhlLength;
        this.vhlWidth = vhlWidth;
        this.vhlHeight = vhlHeight;
        this.vhlBrand = vhlBrand;
        this.vhlEngineNumb = vhlEngineNumb;
        this.vhlIdentityNumber = vhlIdentityNumber;
        this.vhlManufactureYear = vhlManufactureYear;
    }

    public String getVhlUID() {
        return vhlUID;
    }

    public void setVhlUID(String vhlUID) {
        this.vhlUID = vhlUID;
    }

    public Boolean getVhlStatus() {
        return vhlStatus;
    }

    public void setVhlStatus(Boolean vhlStatus) {
        this.vhlStatus = vhlStatus;
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

    public String getVhlBrand() {
        return vhlBrand;
    }

    public void setVhlBrand(String vhlBrand) {
        this.vhlBrand = vhlBrand;
    }

    public String getVhlEngineNumb() {
        return vhlEngineNumb;
    }

    public void setVhlEngineNumb(String vhlEngineNumb) {
        this.vhlEngineNumb = vhlEngineNumb;
    }

    public String getVhlIdentityNumber() {
        return vhlIdentityNumber;
    }

    public void setVhlIdentityNumber(String vhlIdentityNumber) {
        this.vhlIdentityNumber = vhlIdentityNumber;
    }

    public String getVhlManufactureYear() {
        return vhlManufactureYear;
    }

    public void setVhlManufactureYear(String vhlManufactureYear) {
        this.vhlManufactureYear = vhlManufactureYear;
    }
}
