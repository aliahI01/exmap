package com.example.exmap;

public class LoginModel {

    String emailUser, pass;

    public LoginModel() {
    }

    public LoginModel(String emailUser, String pass) {
        this.emailUser = emailUser;
        this.pass = pass;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String email) {
        this.emailUser = emailUser;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String password) {
        this.pass = pass;
    }
}
