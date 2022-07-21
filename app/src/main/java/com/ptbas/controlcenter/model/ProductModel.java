package com.ptbas.controlcenter.model;

public class ProductModel {

    String name;
    Float priceBuy, priceSell;

    public ProductModel() {
    }

    public ProductModel(String name, Float priceBuy, Float priceSell) {
        this.name = name;
        this.priceBuy = priceBuy;
        this.priceSell = priceSell;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(Float priceBuy) {
        this.priceBuy = priceBuy;
    }

    public Float getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(Float priceSell) {
        this.priceSell = priceSell;
    }
}
