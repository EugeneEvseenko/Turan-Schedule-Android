package com.turan.schedule;

import com.google.gson.annotations.SerializedName;

public class Teacher {
    @SerializedName("id")
    public int id = 0;
    @SerializedName("name")
    public String name = null;
    @SerializedName("email")
    public String email = null;
    @SerializedName("phone")
    public String phone = null;
    @SerializedName("group-id")
    public int gid = 0;

    public Teacher(String id, String name, String email, String phone,String gid){
        this.id = Integer.parseInt(id);
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gid = Integer.parseInt(gid);
    }
}
