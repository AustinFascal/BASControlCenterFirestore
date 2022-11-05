package com.ptbas.controlcenter.model;

public class InvoiceModel {

    String invDateHandover, invHandoverBy, invDocumentUID, invUID, invCreatedBy, invDateNTimeCreated, invVerifiedBy,
            invDueDateNTime, invDateVerified, invTransferReference, invDateDeliveryPeriod, custDocumentID, bankDocumentID, roDocumentID,
            invTotalVol, invSubTotal, invDiscount, invTaxPPN, invTaxPPH, invTotalDue;

    private boolean isChecked = false;

    Boolean invRecalculate;

    public InvoiceModel() {
    }

    public InvoiceModel(String invDocumentUID, String invUID, String invCreatedBy, String invDateNTimeCreated, String invDueDateNTime, String invVerifiedBy, String invDateVerified, String invTransferReference, String invDateDeliveryPeriod, String custDocumentID, String bankDocumentID, String roDocumentID, String invDateHandover, String invHandoverBy, Boolean invRecalculate,
                        String invTotalVol, String invSubTotal,String invDiscount,String invTaxPPN,String invTaxPPH,String invTotalDue) {
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
        this.invDateHandover = invDateHandover;
        this.invHandoverBy = invHandoverBy;
        this.invRecalculate = invRecalculate;
        this.invTotalVol = invTotalVol;
        this.invSubTotal = invSubTotal;
        this.invDiscount = invDiscount;
        this.invTaxPPN = invTaxPPN;
        this.invTaxPPH = invTaxPPH;
        this.invTotalDue = invTotalDue;
    }



    public Boolean getInvRecalculate() {
        return invRecalculate;
    }

    public void setInvRecalculate(Boolean invRecalculate) {
        this.invRecalculate = invRecalculate;
    }

    public String getInvTotalVol() {
        return invTotalVol;
    }

    public void setInvTotalVol(String invTotalVol) {
        this.invTotalVol = invTotalVol;
    }

    public String getInvSubTotal() {
        return invSubTotal;
    }

    public void setInvSubTotal(String invSubTotal) {
        this.invSubTotal = invSubTotal;
    }

    public String getInvDiscount() {
        return invDiscount;
    }

    public void setInvDiscount(String invDiscount) {
        this.invDiscount = invDiscount;
    }

    public String getInvTaxPPN() {
        return invTaxPPN;
    }

    public void setInvTaxPPN(String invTaxPPN) {
        this.invTaxPPN = invTaxPPN;
    }

    public String getInvTaxPPH() {
        return invTaxPPH;
    }

    public void setInvTaxPPH(String invTaxPPH) {
        this.invTaxPPH = invTaxPPH;
    }

    public String getInvTotalDue() {
        return invTotalDue;
    }

    public void setInvTotalDue(String invTotalDue) {
        this.invTotalDue = invTotalDue;
    }

    public String getInvHandoverBy() {
        return invHandoverBy;
    }

    public void setInvHandoverBy(String invHandoverBy) {
        this.invHandoverBy = invHandoverBy;
    }

    public String getInvDateHandover() {
        return invDateHandover;
    }

    public void setInvDateHandover(String invDateHandover) {
        this.invDateHandover = invDateHandover;
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
