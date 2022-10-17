package com.ptbas.controlcenter.model;

public class InvoiceModel {

    String invDocumentUID, invUID, invCreatedBy, invDateNTimeCreated, invVerifiedBy,
            invDueDateNTime, invDateVerified, invTransferReference, invDateDeliveryPeriod, custDocumentID, bankDocumentID, roDocumentID;

    private boolean isChecked = false;

    public InvoiceModel() {
    }

    public InvoiceModel(String invDocumentUID, String invUID, String invCreatedBy, String invDateNTimeCreated, String invDueDateNTime, String invVerifiedBy, String invDateVerified, String invTransferReference, String invDateDeliveryPeriod, String custDocumentID, String bankDocumentID, String roDocumentID) {
        this.invDocumentUID = invDocumentUID;
        this.invUID = invUID;
        this.invCreatedBy = invCreatedBy;
        this.invDateNTimeCreated = invDateNTimeCreated;
        this.invDueDateNTime = invDueDateNTime;
        this.invVerifiedBy = invVerifiedBy;
        this.invDateVerified = invDateVerified;
        this.invTransferReference = invTransferReference;
        this.invDateDeliveryPeriod = invDateDeliveryPeriod;
        this.custDocumentID = custDocumentID;
        this.bankDocumentID = bankDocumentID;
        this.roDocumentID = roDocumentID;
    }

    public String getInvTransferReference() {
        return invTransferReference;
    }

    public void setInvTransferReference(String invTransferReference) {
        this.invTransferReference = invTransferReference;
    }

    public String getInvDueDateNTime() {
        return invDueDateNTime;
    }

    public void setInvDueDateNTime(String invDueDateNTime) {
        this.invDueDateNTime = invDueDateNTime;
    }

    public String getInvDocumentUID() {
        return invDocumentUID;
    }

    public void setInvDocumentUID(String invDocumentUID) {
        this.invDocumentUID = invDocumentUID;
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

    public String getInvDateNTimeCreated() {
        return invDateNTimeCreated;
    }

    public void setInvDateNTimeCreated(String invDateNTimeCreated) {
        this.invDateNTimeCreated = invDateNTimeCreated;
    }

    public String getInvVerifiedBy() {
        return invVerifiedBy;
    }

    public void setInvVerifiedBy(String invVerifiedBy) {
        this.invVerifiedBy = invVerifiedBy;
    }

    public String getInvDateVerified() {
        return invDateVerified;
    }

    public void setInvDateVerified(String invDateVerified) {
        this.invDateVerified = invDateVerified;
    }

    public String getInvDateDeliveryPeriod() {
        return invDateDeliveryPeriod;
    }

    public void setInvDateDeliveryPeriod(String invDateDeliveryPeriod) {
        this.invDateDeliveryPeriod = invDateDeliveryPeriod;
    }

    public String getCustDocumentID() {
        return custDocumentID;
    }

    public void setCustDocumentID(String custDocumentID) {
        this.custDocumentID = custDocumentID;
    }

    public String getBankDocumentID() {
        return bankDocumentID;
    }

    public void setBankDocumentID(String bankDocumentID) {
        this.bankDocumentID = bankDocumentID;
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
