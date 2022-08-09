package com.ptbas.controlcenter.model;

public class UserModel {
    public String fullName, doB, gender, phone, accessCode;

    public UserModel() {
    }

    public UserModel(String txtFullName, String txtDob, String txtGender, String txtPhone, String txtAccessCode) {
        this.fullName = txtFullName;
        this.doB = txtDob;
        this.gender = txtGender;
        this.phone = txtPhone;
        this.accessCode = txtAccessCode;
    }

}
