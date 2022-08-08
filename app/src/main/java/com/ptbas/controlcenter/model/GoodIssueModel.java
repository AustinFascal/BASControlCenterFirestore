package com.ptbas.controlcenter.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GoodIssueModel implements Parcelable {

    //MANDATORY
    String giUID, giCreatedBy, giRoUID, giPoCustNumber, giMatName, giMatType, vhlUID,
            giDateCreated, giTimeCreted;
    Integer vhlLength, vhlWidth, vhlHeight, vhlHeightCorrection, vhlHeightAfterCorrection;
    Float giVhlCubication;
    Boolean giStatus, giInvoiced;

    GoodIssueModel() {
    }

    public GoodIssueModel(String giUID, String giCreatedBy, String giRoUID, String giPoCustNumber, String giMatName, String giMatType, String vhlUID, String giDateCreated, String giTimeCreted, Integer vhlLength, Integer vhlWidth, Integer vhlHeight, Integer vhlHeightCorrection, Integer vhlHeightAfterCorrection, Float giVhlCubication, Boolean giStatus, Boolean giInvoiced) {
        this.giUID = giUID;
        this.giCreatedBy = giCreatedBy;
        this.giRoUID = giRoUID;
        this.giPoCustNumber = giPoCustNumber;
        this.giMatName = giMatName;
        this.giMatType = giMatType;
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
        this.giInvoiced = giInvoiced;
    }

    protected GoodIssueModel(Parcel in) {
        giUID = in.readString();
        giCreatedBy = in.readString();
        giRoUID = in.readString();
        giPoCustNumber = in.readString();
        giMatName = in.readString();
        giMatType = in.readString();
        vhlUID = in.readString();
        giDateCreated = in.readString();
        giTimeCreted = in.readString();
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
            giVhlCubication = in.readFloat();
        }
        byte tmpGiStatus = in.readByte();
        giStatus = tmpGiStatus == 0 ? null : tmpGiStatus == 1;
        byte tmpGiInvoiced = in.readByte();
        giInvoiced = tmpGiInvoiced == 0 ? null : tmpGiInvoiced == 1;
    }

    public static final Parcelable.Creator<GoodIssueModel> CREATOR = new Parcelable.Creator<GoodIssueModel>() {
        @Override
        public GoodIssueModel createFromParcel(Parcel in) {
            return new GoodIssueModel(in);
        }

        @Override
        public GoodIssueModel[] newArray(int size) {
            return new GoodIssueModel[size];
        }
    };

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

    public String getGiRoUID() {
        return giRoUID;
    }

    public void setGiRoUID(String giRoUID) {
        this.giRoUID = giRoUID;
    }

    public String getGiPoCustNumber() {
        return giPoCustNumber;
    }

    public void setGiPoCustNumber(String giPoCustNumber) {
        this.giPoCustNumber = giPoCustNumber;
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

    public Float getGiVhlCubication() {
        return giVhlCubication;
    }

    public void setGiVhlCubication(Float giVhlCubication) {
        this.giVhlCubication = giVhlCubication;
    }

    public Boolean getGiStatus() {
        return giStatus;
    }

    public void setGiStatus(Boolean giStatus) {
        this.giStatus = giStatus;
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
        parcel.writeString(giRoUID);
        parcel.writeString(giPoCustNumber);
        parcel.writeString(giMatName);
        parcel.writeString(giMatType);
        parcel.writeString(vhlUID);
        parcel.writeString(giDateCreated);
        parcel.writeString(giTimeCreted);
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
            parcel.writeFloat(giVhlCubication);
        }
        parcel.writeByte((byte) (giStatus == null ? 0 : giStatus ? 1 : 2));
        parcel.writeByte((byte) (giInvoiced == null ? 0 : giInvoiced ? 1 : 2));
    }
}
