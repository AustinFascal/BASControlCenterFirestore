package com.ptbas.controlcenter.model;

import java.io.Serializable;

public class ProductItems implements Serializable {

    public String matName;
    public Integer matQuantity;
    public Double matSellPrice, matBuyPrice, matTotalSellPrice, matTotalBuyPrice;

    public ProductItems() {
    }

    public ProductItems(String matName, Integer matQuantity, Double matSellPrice, Double matBuyPrice, Double matTotalSellPrice, Double matTotalBuyPrice) {

        this.matName = matName;
        this.matQuantity = matQuantity;
        this.matSellPrice = matSellPrice;
        this.matBuyPrice = matBuyPrice;
        this.matTotalSellPrice = matTotalSellPrice;
        this.matTotalBuyPrice = matTotalBuyPrice;
    }

    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public Integer getMatQuantity() {
        return matQuantity;
    }

    public void setMatQuantity(Integer matQuantity) {
        this.matQuantity = matQuantity;
    }

    public Double getMatSellPrice() {
        return matSellPrice;
    }

    public void setMatSellPrice(Double matSellPrice) {
        this.matSellPrice = matSellPrice;
    }

    public Double getMatBuyPrice() {
        return matBuyPrice;
    }

    public void setMatBuyPrice(Double matBuyPrice) {
        this.matBuyPrice = matBuyPrice;
    }

    public Double getMatTotalSellPrice() {
        return matTotalSellPrice;
    }

    public void setMatTotalSellPrice(Double matTotalSellPrice) {
        this.matTotalSellPrice = matTotalSellPrice;
    }

    public Double getMatTotalBuyPrice() {
        return matTotalBuyPrice;
    }

    public void setMatTotalBuyPrice(Double matTotalBuyPrice) {
        this.matTotalBuyPrice = matTotalBuyPrice;
    }
}
