package com.ptbas.controlcenter.model;

public class ProductModel {

    String productName;
    Double priceBuy, priceSell;
    Boolean productStatus;

    public ProductModel() {
    }

    public ProductModel(String productName, Double priceBuy, Double priceSell, Boolean productStatus) {
        this.productName = productName;
        this.priceBuy = priceBuy;
        this.priceSell = priceSell;
        this.productStatus = productStatus;
    }

    public Boolean getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Boolean productStatus) {
        this.productStatus = productStatus;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(Double priceBuy) {
        this.priceBuy = priceBuy;
    }

    public Double getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(Double priceSell) {
        this.priceSell = priceSell;
    }
}
