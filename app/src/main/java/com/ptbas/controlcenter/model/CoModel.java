package com.ptbas.controlcenter.model;

public class CoModel {

    private boolean isChecked = false;
    String coDocumentID, coUID, coDateAndTimeCreated, coCreatedBy,
            coDateAndTimeApproved, coApprovedBy, coDateAndTimeACC, coAccBy, coSupplier, roDocumentID,
            coDateDeliveryPeriod;
    String coBookedStep1By, coBookedStep1Date, coBookedStep2By, coBookedStep2Date, bankDocumentID, coTransferReference, rcpGiCoUID, invDocumentUID;
    Boolean coStatusApproval, coStatusPayment;
    Double coTotal;

    public CoModel() {
    }

    public CoModel(String coDocumentID, String coUID, String coDateAndTimeCreated,
                   String coCreatedBy, String coDateAndTimeApproved, String coApprovedBy,
                   String coDateAndTimeACC, String coAccBy, String coSupplier,
                   String roDocumentID, String coDateDeliveryPeriod, Boolean coStatusApproval,
                   Boolean coStatusPayment, Double coTotal,
                   String coBookedStep1By, String coBookedStep1Date, String coBookedStep2By,
                   String coBookedStep2Date, String bankDocumentID, String coTransferReference, String rcpGiCoUID, String invDocumentUID) {
        this.coDocumentID = coDocumentID;
        this.coUID = coUID;
        this.coDateAndTimeCreated = coDateAndTimeCreated;
        this.coCreatedBy = coCreatedBy;
        this.coDateAndTimeApproved = coDateAndTimeApproved;
        this.coApprovedBy = coApprovedBy;
        this.coDateAndTimeACC = coDateAndTimeACC;
        this.coAccBy = coAccBy;
        this.coSupplier = coSupplier;
        this.roDocumentID = roDocumentID;
        this.coDateDeliveryPeriod = coDateDeliveryPeriod;
        this.coStatusApproval = coStatusApproval;
        this.coStatusPayment = coStatusPayment;
        this.coTotal = coTotal;
        this.coBookedStep1By = coBookedStep1By;
        this.coBookedStep1Date = coBookedStep1Date;
        this.coBookedStep2By = coBookedStep2By;
        this.coBookedStep2Date = coBookedStep2Date;
        this.bankDocumentID = bankDocumentID;
        this.coTransferReference = coTransferReference;
        this.rcpGiCoUID = rcpGiCoUID;
        this.invDocumentUID = invDocumentUID;
    }

    public String getInvDocumentUID() {
        return invDocumentUID;
    }

    public void setInvDocumentUID(String invDocumentUID) {
        this.invDocumentUID = invDocumentUID;
    }

    public String getRcpGiCoUID() {
        return rcpGiCoUID;
    }

    public void setRcpGiCoUID(String rcpGiCoUID) {
        this.rcpGiCoUID = rcpGiCoUID;
    }

    public String getCoDocumentID() {
        return coDocumentID;
    }

    public void setCoDocumentID(String coDocumentID) {
        this.coDocumentID = coDocumentID;
    }

    public String getCoUID() {
        return coUID;
    }

    public void setCoUID(String coUID) {
        this.coUID = coUID;
    }

    public String getCoDateAndTimeCreated() {
        return coDateAndTimeCreated;
    }

    public void setCoDateAndTimeCreated(String coDateAndTimeCreated) {
        this.coDateAndTimeCreated = coDateAndTimeCreated;
    }

    public String getCoCreatedBy() {
        return coCreatedBy;
    }

    public void setCoCreatedBy(String coCreatedBy) {
        this.coCreatedBy = coCreatedBy;
    }

    public String getCoDateAndTimeApproved() {
        return coDateAndTimeApproved;
    }

    public void setCoDateAndTimeApproved(String coDateAndTimeApproved) {
        this.coDateAndTimeApproved = coDateAndTimeApproved;
    }

    public String getCoApprovedBy() {
        return coApprovedBy;
    }

    public void setCoApprovedBy(String coApprovedBy) {
        this.coApprovedBy = coApprovedBy;
    }

    public String getCoDateAndTimeACC() {
        return coDateAndTimeACC;
    }

    public void setCoDateAndTimeACC(String coDateAndTimeACC) {
        this.coDateAndTimeACC = coDateAndTimeACC;
    }

    public String getCoAccBy() {
        return coAccBy;
    }

    public void setCoAccBy(String coAccBy) {
        this.coAccBy = coAccBy;
    }

    public String getCoSupplier() {
        return coSupplier;
    }

    public void setCoSupplier(String coSupplier) {
        this.coSupplier = coSupplier;
    }

    public String getRoDocumentID() {
        return roDocumentID;
    }

    public void setRoDocumentID(String roDocumentID) {
        this.roDocumentID = roDocumentID;
    }

    public String getCoDateDeliveryPeriod() {
        return coDateDeliveryPeriod;
    }

    public void setCoDateDeliveryPeriod(String coDateDeliveryPeriod) {
        this.coDateDeliveryPeriod = coDateDeliveryPeriod;
    }

    public Boolean getCoStatusApproval() {
        return coStatusApproval;
    }

    public void setCoStatusApproval(Boolean coStatusApproval) {
        this.coStatusApproval = coStatusApproval;
    }

    public Boolean getCoStatusPayment() {
        return coStatusPayment;
    }

    public void setCoStatusPayment(Boolean coStatusPayment) {
        this.coStatusPayment = coStatusPayment;
    }

    public Double getCoTotal() {
        return coTotal;
    }

    public void setCoTotal(Double coTotal) {
        this.coTotal = coTotal;
    }


    public String getCoBookedStep1By() {
        return coBookedStep1By;
    }

    public void setCoBookedStep1By(String coBookedStep1By) {
        this.coBookedStep1By = coBookedStep1By;
    }

    public String getCoBookedStep1Date() {
        return coBookedStep1Date;
    }

    public void setCoBookedStep1Date(String coBookedStep1Date) {
        this.coBookedStep1Date = coBookedStep1Date;
    }

    public String getCoBookedStep2By() {
        return coBookedStep2By;
    }

    public void setCoBookedStep2By(String coBookedStep2By) {
        this.coBookedStep2By = coBookedStep2By;
    }

    public String getCoBookedStep2Date() {
        return coBookedStep2Date;
    }

    public void setCoBookedStep2Date(String coBookedStep2Date) {
        this.coBookedStep2Date = coBookedStep2Date;
    }

    public String getBankDocumentID() {
        return bankDocumentID;
    }

    public void setBankDocumentID(String bankDocumentID) {
        this.bankDocumentID = bankDocumentID;
    }

    public String getCoTransferReference() {
        return coTransferReference;
    }

    public void setCoTransferReference(String coTransferReference) {
        this.coTransferReference = coTransferReference;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}