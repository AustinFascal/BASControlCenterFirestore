package com.wingscorp.basreport;

public class UserDetails {
    public String doB, gender, phone, accessCode;

    public UserDetails() {
    }

    public UserDetails(String txtDob, String txtGender, String txtPhone, String txtAccessCode) {
        this.doB = txtDob;
        this.gender = txtGender;
        this.phone = txtPhone;
        this.accessCode = txtAccessCode;
    }

}
