package com.ptbas.controlcenter.model;

public class RecapGIModel {

    private boolean isChecked = false;

    String rcpGiDocumentID, rcpGiUID, rcpGiDateAndTimeCreated, rcpGiCreatedBy, roDocumentID, rcpGiCoUID, rcpDateDeliveryPeriod, rcpGiInvoicedTo;
    float roCubication;
    Boolean rcpGiStatus;

    public RecapGIModel() {
    }

    public RecapGIModel(String rcpGiDocumentID, String rcpGiUID, String rcpGiDateAndTimeCreated,
                        String rcpGiCreatedBy, String roDocumentID, float roCubication, String rcpGiCoUID, Boolean rcpGiStatus, String rcpDateDeliveryPeriod, String rcpGiInvoicedTo) {
        this.rcpGiDocumentID = rcpGiDocumentID;
        this.rcpGiUID = rcpGiUID;
        this.rcpGiDateAndTimeCreated = rcpGiDateAndTimeCreated;
        this.rcpGiCreatedBy = rcpGiCreatedBy;
        this.roDocumentID = roDocumentID;
        this.roCubication = roCubication;
        this.rcpGiCoUID = rcpGiCoUID;
        this.rcpGiStatus = rcpGiStatus;
        this.rcpDateDeliveryPeriod = rcpDateDeliveryPeriod;
        this.rcpGiInvoicedTo = rcpGiInvoicedTo;
    }

    public String getRcpGiInvoicedTo() {
        return rcpGiInvoicedTo;
    }

    public void setRcpGiInvoicedTo(String rcpGiInvoicedTo) {
        this.rcpGiInvoicedTo = rcpGiInvoicedTo;
    }

    public String getRcpDateDeliveryPeriod() {
        return rcpDateDeliveryPeriod;
    }

    public void setRcpDateDeliveryPeriod(String rcpDateDeliveryPeriod) {
        this.rcpDateDeliveryPeriod = rcpDateDeliveryPeriod;
    }

    public Boolean getRcpGiStatus() {
        return rcpGiStatus;
    }

    public void setRcpGiStatus(Boolean rcpGiStatus) {
        this.rcpGiStatus = rcpGiStatus;
    }

    public String getRcpGiCoUID() {
        return rcpGiCoUID;
    }

    public void setRcpGiCoUID(String rcpGiCoUID) {
        this.rcpGiCoUID = rcpGiCoUID;
    }

    public float getRoCubication() {
        return roCubication;
    }

    public void setRoCubication(float roCubication) {
        this.roCubication = roCubication;
    }

    public String getRcpGiDocumentID() {
        return rcpGiDocumentID;
    }

    public void setRcpGiDocumentID(String rcpGiDocumentID) {
        this.rcpGiDocumentID = rcpGiDocumentID;
    }

    public String getRcpGiUID() {
        return rcpGiUID;
    }

    public void setRcpGiUID(String rcpGiUID) {
        this.rcpGiUID = rcpGiUID;
    }

    public String getRcpGiDateAndTimeCreated() {
        return rcpGiDateAndTimeCreated;
    }

    public void setRcpGiDateAndTimeCreated(String rcpGiDateAndTimeCreated) {
        this.rcpGiDateAndTimeCreated = rcpGiDateAndTimeCreated;
    }

    public String getRcpGiCreatedBy() {
        return rcpGiCreatedBy;
    }

    public void setRcpGiCreatedBy(String rcpGiCreatedBy) {
        this.rcpGiCreatedBy = rcpGiCreatedBy;
    }

    public String getRoDocumentID() {
        return roDocumentID;
    }

    public void setRoDocumentID(String roDocumentID) {
        this.roDocumentID = roDocumentID;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
