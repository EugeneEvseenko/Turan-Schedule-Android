package com.turan.schedule;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Profile {
    @SerializedName("id")
    public int id = 0;
    @SerializedName("login")
    public String login = null;
    @SerializedName("password")
    public String password = null;
    @SerializedName("name")
    public String name = null;
    @SerializedName("email")
    public String email = null;
    @SerializedName("gender")
    public byte gender = -1;
    @SerializedName("date-of-birth")
    public String dob = null;
    @SerializedName("phone")
    public String phone = null;
    @SerializedName("status")
    public String status = null;
    @SerializedName("group")
    public Group group = null;
    @SerializedName("last-session")
    public String last_session = null;

    public Profile(String id, String login, String password, String name, String email, String gender, String dob, String phone, String status, Group group, String last_session){
        this.id = Integer.parseInt(id);
        this.login = login;
        this.password = password;
        this.name = name;
        this.email = email;
        this.gender = Byte.parseByte(gender);
        this.dob = dob;
        this.phone = phone;
        this.status = status;
        this.group = group;
        this.last_session = last_session;
    }
}
