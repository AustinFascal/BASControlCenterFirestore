package com.ptbas.controlcenter.model;

public class RecapGIModel {

    private boolean isChecked = false;

    String rcpGiDocumentID, rcpGiUID, rcpGiDateAndTimeCreated, rcpGiCreatedBy, roDocumentID, rcpGiCoUID;
    float roCubication;
   // Boolean rcpGiStatus;

    public RecapGIModel() {
    }

    public RecapGIModel(String rcpGiDocumentID, String rcpGiUID, String rcpGiDateAndTimeCreated,
                        String rcpGiCreatedBy, String roDocumentID, float roCubication, String rcpGiCoUID) {
        this.rcpGiDocumentID = rcpGiDocumentID;
        this.rcpGiUID = rcpGiUID;
        this.rcpGiDateAndTimeCreated = rcpGiDateAndTimeCreated;
        this.rcpGiCreatedBy = rcpGiCreatedBy;
        this.roDocumentID = roDocumentID;
        this.roCubication = roCubication;
        this.rcpGiCoUID = rcpGiCoUID;
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
