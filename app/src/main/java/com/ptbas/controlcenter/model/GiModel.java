package com.ptbas.controlcenter.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GiModel implements Parcelable {

    private boolean isChecked = false;
    //MANDATORY
    String giUID, giCreatedBy, giVerifiedBy, roDocumentID, giMatName, giMatType, giNoteNumber, vhlUID,
            giDateCreated, giTimeCreted, giCashedOutTo, giRecappedTo, giInvoicedTo;
    Integer vhlLength, vhlWidth, vhlHeight, vhlHeightCorrection, vhlHeightAfterCorrection;
    Double giVhlCubication;
    Boolean giStatus, giRecapped, giInvoiced, giCashedOut;
    private boolean isSelected;

    public GiModel() {
    }

    public GiModel(String giUID, String giCreatedBy, String giVerifiedBy,
                   String roDocumentID, String giMatName,
                   String giMatType, String giNoteNumber, String vhlUID,
                   String giDateCreated, String giTimeCreted, Integer vhlLength,
                   Integer vhlWidth, Integer vhlHeight, Integer vhlHeightCorrection,
                   Integer vhlHeightAfterCorrection, Double giVhlCubication, Boolean giStatus,
                   Boolean giRecapped, Boolean giInvoiced, String giInvoicedTo, Boolean giCashedOut, String giCashedOutTo, String giRecappedTo) {
        this.giUID = giUID;
        this.giCreatedBy = giCreatedBy;
        this.giVerifiedBy = giVerifiedBy;
        this.roDocumentID = roDocumentID;
        this.giMatName = giMatName;
        this.giMatType = giMatType;
        this.giNoteNumber = giNoteNumber;
        this.vhlUID = vhlUID;
        this.giDateCreated = giDateCreated;
        this.giTimeCreted = giTimeCreted;
        this.vhlLength = vhlLength;
        this.vhlWidth = vhlWidth;
        this.vhlHeight = vhlHeight;
        this.vhlHeightCorrection = vhlHeightCorrection;
        this.vhlHeightAfterCorrection = vhlHeightAfterCorrection;
        this.giVhlCubication = giVhlCubication;
        this.giStatus = giStatus;
        this.giRecapped = giRecapped;
        this.giInvoiced = giInvoiced;
        this.giInvoicedTo = giInvoicedTo;
        this.giCashedOut = giCashedOut;
        this.giCashedOutTo = giCashedOutTo;
        this.giRecappedTo = giRecappedTo;
    }

    protected GiModel(Parcel in) {
        giUID = in.readString();
        giCreatedBy = in.readString();
        giVerifiedBy = in.readString();
        roDocumentID = in.readString();
        giMatName = in.readString();
        giMatType = in.readString();
        giNoteNumber = in.readString();
        vhlUID = in.readString();
        giDateCreated = in.readString();
        giTimeCreted = in.readString();
        giInvoicedTo = in.readString();
        giCashedOutTo = in.readString();
        giRecappedTo = in.readString();
        if (in.readByte() == 0) {
            vhlLength = null;
        } else {
            vhlLength = in.readInt();
        }
        if (in.readByte() == 0) {
            vhlWidth = null;
        } else {
            vhlWidth = in.readInt();
        }
        if (in.readByte() == 0) {
            vhlHeight = null;
        } else {
            vhlHeight = in.readInt();
        }
        if (in.readByte() == 0) {
            vhlHeightCorrection = null;
        } else {
            vhlHeightCorrection = in.readInt();
        }
        if (in.readByte() == 0) {
            vhlHeightAfterCorrection = null;
        } else {
            vhlHeightAfterCorrection = in.readInt();
        }
        if (in.readByte() == 0) {
            giVhlCubication = null;
        } else {
            giVhlCubication = in.readDouble();
        }
        byte tmpGiStatus = in.readByte();
        giStatus = tmpGiStatus == 0 ? null : tmpGiStatus == 1;
        byte tmpGiRecapped = in.readByte();
        giRecapped = tmpGiRecapped == 0 ? null : tmpGiRecapped == 1;
        byte tmpGiInvoiced = in.readByte();
        giInvoiced = tmpGiInvoiced == 0 ? null : tmpGiInvoiced == 1;
        byte tmpGiCashedOut = in.readByte();
        giCashedOut = tmpGiCashedOut == 0 ? null : tmpGiCashedOut == 1;
    }

    public static final Parcelable.Creator<GiModel> CREATOR = new Parcelable.Creator<GiModel>() {
        @Override
        public GiModel createFromParcel(Parcel in) {
            return new GiModel(in);
        }

        @Override
        public GiModel[] newArray(int size) {
            return new GiModel[size];
        }
    };

    public String getGiInvoicedTo() {
        return giInvoicedTo;
    }

    public void setGiInvoicedTo(String giInvoicedTo) {
        this.giInvoicedTo = giInvoicedTo;
    }

    public String getGiRecappedTo() {
        return giRecappedTo;
    }

    public void setGiRecappedTo(String giRecappedTo) {
        this.giRecappedTo = giRecappedTo;
    }

    public String getGiCashedOutTo() {
        return giCashedOutTo;
    }

    public void setGiCashedOutTo(String giCashedOutTo) {
        this.giCashedOutTo = giCashedOutTo;
    }

    public String getGiNoteNumber() {
        return giNoteNumber;
    }

    public void setGiNoteNumber(String giNoteNumber) {
        this.giNoteNumber = giNoteNumber;
    }

    public Boolean getGiCashedOut() {
        return giCashedOut;
    }

    public void setGiCashedOut(Boolean giCashedOut) {
        this.giCashedOut = giCashedOut;
    }

    public String getGiUID() {
        return giUID;
    }

    public void setGiUID(String giUID) {
        this.giUID = giUID;
    }

    public String getGiCreatedBy() {
        return giCreatedBy;
    }

    public void setGiCreatedBy(String giCreatedBy) {
        this.giCreatedBy = giCreatedBy;
    }

    public String getGiVerifiedBy() {
        return giVerifiedBy;
    }

    public void setGiVerifiedBy(String giVerifiedBy) {
        this.giVerifiedBy = giVerifiedBy;
    }

    public String getRoDocumentID() {
        return roDocumentID;
    }

    public void setRoDocumentID(String roDocumentID) {
        this.roDocumentID = roDocumentID;
    }

    public String getGiMatName() {
        return giMatName;
    }

    public void setGiMatName(String giMatName) {
        this.giMatName = giMatName;
    }

    public String getGiMatType() {
        return giMatType;
    }

    public void setGiMatType(String giMatType) {
        this.giMatType = giMatType;
    }

    public String getVhlUID() {
        return vhlUID;
    }

    public void setVhlUID(String vhlUID) {
        this.vhlUID = vhlUID;
    }

    public String getGiDateCreated() {
        return giDateCreated;
    }

    public void setGiDateCreated(String giDateCreated) {
        this.giDateCreated = giDateCreated;
    }

    public String getGiTimeCreted() {
        return giTimeCreted;
    }

    public void setGiTimeCreted(String giTimeCreted) {
        this.giTimeCreted = giTimeCreted;
    }

    public Integer getVhlLength() {
        return vhlLength;
    }

    public void setVhlLength(Integer vhlLength) {
        this.vhlLength = vhlLength;
    }

    public Integer getVhlWidth() {
        return vhlWidth;
    }

    public void setVhlWidth(Integer vhlWidth) {
        this.vhlWidth = vhlWidth;
    }

    public Integer getVhlHeight() {
        return vhlHeight;
    }

    public void setVhlHeight(Integer vhlHeight) {
        this.vhlHeight = vhlHeight;
    }

    public Integer getVhlHeightCorrection() {
        return vhlHeightCorrection;
    }

    public void setVhlHeightCorrection(Integer vhlHeightCorrection) {
        this.vhlHeightCorrection = vhlHeightCorrection;
    }

    public Integer getVhlHeightAfterCorrection() {
        return vhlHeightAfterCorrection;
    }

    public void setVhlHeightAfterCorrection(Integer vhlHeightAfterCorrection) {
        this.vhlHeightAfterCorrection = vhlHeightAfterCorrection;
    }

    public Double getGiVhlCubication() {
        return giVhlCubication;
    }

    public void setGiVhlCubication(Double giVhlCubication) {
        this.giVhlCubication = giVhlCubication;
    }

    public Boolean getGiStatus() {
        return giStatus;
    }

    public void setGiStatus(Boolean giStatus) {
        this.giStatus = giStatus;
    }

    public Boolean getGiRecapped() {
        return giRecapped;
    }

    public void setGiRecapped(Boolean giRecapped) {
        this.giRecapped = giRecapped;
    }

    public Boolean getGiInvoiced() {
        return giInvoiced;
    }

    public void setGiInvoiced(Boolean giInvoiced) {
        this.giInvoiced = giInvoiced;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(giUID);
        parcel.writeString(giCreatedBy);
        parcel.writeString(giVerifiedBy);
        parcel.writeString(roDocumentID);
        parcel.writeString(giMatName);
        parcel.writeString(giMatType);
        parcel.writeString(giNoteNumber);
        parcel.writeString(vhlUID);
        parcel.writeString(giDateCreated);
        parcel.writeString(giTimeCreted);
        parcel.writeString(giInvoicedTo);
        parcel.writeString(giCashedOutTo);
        parcel.writeString(giRecappedTo);
        if (vhlLength == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(vhlLength);
        }
        if (vhlWidth == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(vhlWidth);
        }
        if (vhlHeight == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(vhlHeight);
        }
        if (vhlHeightCorrection == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(vhlHeightCorrection);
        }
        if (vhlHeightAfterCorrection == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(vhlHeightAfterCorrection);
        }
        if (giVhlCubication == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(giVhlCubication);
        }
        parcel.writeByte((byte) (giStatus == null ? 0 : giStatus ? 1 : 2));
        parcel.writeByte((byte) (giRecapped == null ? 0 : giRecapped ? 1 : 2));
        parcel.writeByte((byte) (giInvoiced == null ? 0 : giInvoiced ? 1 : 2));
        parcel.writeByte((byte) (giCashedOut == null ? 0 : giCashedOut ? 1 : 2));
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
