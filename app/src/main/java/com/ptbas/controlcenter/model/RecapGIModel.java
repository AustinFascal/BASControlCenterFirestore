package com.ptbas.controlcenter.model;

public class RecapGIModel {

    private boolean isChecked = false;

    String rcpGiDocumentID, rcpGiUID, rcpGiDateAndTimeCreated, rcpGiCreatedBy, roDocumentID;

    public RecapGIModel() {
    }

    public RecapGIModel(String rcpGiDocumentID, String rcpGiUID, String rcpGiDateAndTimeCreated, String rcpGiCreatedBy, String roDocumentID) {
        this.rcpGiDocumentID = rcpGiDocumentID;
        this.rcpGiUID = rcpGiUID;
        this.rcpGiDateAndTimeCreated = rcpGiDateAndTimeCreated;
        this.rcpGiCreatedBy = rcpGiCreatedBy;
        this.roDocumentID = roDocumentID;
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
}
