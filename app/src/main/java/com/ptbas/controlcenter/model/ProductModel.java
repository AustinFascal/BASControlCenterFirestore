package com.ptbas.controlcenter.model;

public class ProductModel {

    String productName;
    Double priceBuy, priceSell;

    public ProductModel() {
    }

    public ProductModel(String productName, Double priceBuy, Double priceSell) {
        this.productName = productName;
        this.priceBuy = priceBuy;
        this.priceSell = priceSell;
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
