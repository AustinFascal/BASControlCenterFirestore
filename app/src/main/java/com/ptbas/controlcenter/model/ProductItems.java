package com.ptbas.controlcenter.model;

import java.io.Serializable;

public class ProductItems implements Serializable {

    public String productName;
    public Integer productQuantity;
    public Float productSellPrice, productBuyPrice, productTotalSellPrice, productTotalBuyPrice;

    public ProductItems() {
    }

    public ProductItems(String productName, Integer productQuantity, Float productSellPrice, Float productBuyPrice, Float productTotalSellPrice, Float productTotalBuyPrice) {
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productSellPrice = productSellPrice;
        this.productBuyPrice = productBuyPrice;
        this.productTotalSellPrice = productTotalSellPrice;
        this.productTotalBuyPrice = productTotalBuyPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Float getProductSellPrice() {
        return productSellPrice;
    }

    public void setProductSellPrice(Float productSellPrice) {
        this.productSellPrice = productSellPrice;
    }

    public Float getProductBuyPrice() {
        return productBuyPrice;
    }

    public void setProductBuyPrice(Float productBuyPrice) {
        this.productBuyPrice = productBuyPrice;
    }

    public Float getProductTotalSellPrice() {
        return productTotalSellPrice;
    }

    public void setProductTotalSellPrice(Float productTotalSellPrice) {
        this.productTotalSellPrice = productTotalSellPrice;
    }

    public Float getProductTotalBuyPrice() {
        return productTotalBuyPrice;
    }

    public void setProductTotalBuyPrice(Float productTotalBuyPrice) {
        this.productTotalBuyPrice = productTotalBuyPrice;
    }
}
