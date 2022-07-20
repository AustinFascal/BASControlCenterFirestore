package com.ptbas.controlcenter.model;

public class UserModel {
    public String doB, gender, phone, accessCode;

    public UserModel() {
    }

    public UserModel(String txtDob, String txtGender, String txtPhone, String txtAccessCode) {
        this.doB = txtDob;
        this.gender = txtGender;
        this.phone = txtPhone;
        this.accessCode = txtAccessCode;
    }

}
