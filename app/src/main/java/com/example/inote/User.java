package com.example.inote;

public class User {
    String fullname, email, password, uid, id, dateSignUp;

    public User() {
    }

    public User(String fullname, String email, String password, String uid, String id, String dateSignUp) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.uid = uid;
        this.id = id;
        this.dateSignUp=dateSignUp;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateSignUp() {
        return dateSignUp;
    }

    public void setDateSignUp(String dateSignUp) {
        this.dateSignUp = dateSignUp;
    }
}
