package com.saddacampus.app.app.DataObjects;

/**
 * User model object
 */

public class saddaUser {

    private int id;
    private String uid;
    private String name;
    private String email;
    private String created_at;
    private String updated_at;
    private String mobile;

    public saddaUser(String uid, String name, String email, String mobile) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    public saddaUser() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
