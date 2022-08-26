package com.ptbas.controlcenter.model;

public class InvoiceModel {

    String invDocumentID, invUID, invPoType, invCreatedBy, invDateCreated, invPoUID, invPoDate, invCustName;
    Double invTotal, invTax1, invTax2;
    Boolean invStatus;

    public InvoiceModel() {
    }

    public InvoiceModel(String invDocumentID, String invUID, String invPoType, String invCreatedBy, String invDateCreated, String invPoUID, String invPoDate, String invCustName, Double invTotal, Double invTax1, Double invTax2, Boolean invStatus) {
        this.invDocumentID = invDocumentID;
        this.invUID = invUID;
        this.invPoType = invPoType;
        this.invCreatedBy = invCreatedBy;
        this.invDateCreated = invDateCreated;
        this.invPoUID = invPoUID;
        this.invPoDate = invPoDate;
        this.invCustName = invCustName;
        this.invTotal = invTotal;
        this.invTax1 = invTax1;
        this.invTax2 = invTax2;
        this.invStatus = invStatus;
    }

    public String getInvPoType() {
        return invPoType;
    }

    public void setInvPoType(String invPoType) {
        this.invPoType = invPoType;
    }

    public String getInvDocumentID() {
        return invDocumentID;
    }

    public void setInvDocumentID(String invDocumentID) {
        this.invDocumentID = invDocumentID;
    }

    public String getInvUID() {
        return invUID;
    }

    public void setInvUID(String invUID) {
        this.invUID = invUID;
    }

    public String getInvCreatedBy() {
        return invCreatedBy;
    }

    public void setInvCreatedBy(String invCreatedBy) {
        this.invCreatedBy = invCreatedBy;
    }

    public String getInvDateCreated() {
        return invDateCreated;
    }

    public void setInvDateCreated(String invDateCreated) {
        this.invDateCreated = invDateCreated;
    }

    public String getInvPoUID() {
        return invPoUID;
    }

    public void setInvPoUID(String invPoUID) {
        this.invPoUID = invPoUID;
    }

    public String getInvPoDate() {
        return invPoDate;
    }

    public void setInvPoDate(String invPoDate) {
        this.invPoDate = invPoDate;
    }

    public String getInvCustName() {
        return invCustName;
    }

    public void setInvCustName(String invCustName) {
        this.invCustName = invCustName;
    }

    public Double getInvTotal() {
        return invTotal;
    }

    public void setInvTotal(Double invTotal) {
        this.invTotal = invTotal;
    }

    public Double getInvTax1() {
        return invTax1;
    }

    public void setInvTax1(Double invTax1) {
        this.invTax1 = invTax1;
    }

    public Double getInvTax2() {
        return invTax2;
    }

    public void setInvTax2(Double invTax2) {
        this.invTax2 = invTax2;
    }

    public Boolean getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Boolean invStatus) {
        this.invStatus = invStatus;
    }
}
