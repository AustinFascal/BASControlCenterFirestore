package com.ptbas.controlcenter.model;

import java.io.Serializable;

public class ProductItems implements Serializable {

    public String productName;
    public Integer productQuantity;
    public Double productSellPrice, productBuyPrice, productTotalSellPrice, productTotalBuyPrice;

    public ProductItems() {
    }


    public ProductItems(String productName, Integer productQuantity, Double productSellPrice, Double productBuyPrice, Double productTotalSellPrice, Double productTotalBuyPrice) {
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

    public Double getProductSellPrice() {
        return productSellPrice;
    }

    public void setProductSellPrice(Double productSellPrice) {
        this.productSellPrice = productSellPrice;
    }

    public Double getProductBuyPrice() {
        return productBuyPrice;
    }

    public void setProductBuyPrice(Double productBuyPrice) {
        this.productBuyPrice = productBuyPrice;
    }

    public Double getProductTotalSellPrice() {
        return productTotalSellPrice;
    }

    public void setProductTotalSellPrice(Double productTotalSellPrice) {
        this.productTotalSellPrice = productTotalSellPrice;
    }

    public Double getProductTotalBuyPrice() {
        return productTotalBuyPrice;
    }

    public void setProductTotalBuyPrice(Double productTotalBuyPrice) {
        this.productTotalBuyPrice = productTotalBuyPrice;
    }
}
