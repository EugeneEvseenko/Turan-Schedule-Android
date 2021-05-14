package com.turan.schedule;

import com.google.gson.annotations.SerializedName;

public class Group {
    @SerializedName("id")
    public int id = 0;
    @SerializedName("name")
    public String name = null;
    @SerializedName("teacher")
    public Teacher teacher = null;
    @SerializedName("headman")
    public Profile headman = null;

    public Group(String id, String name, Teacher teacher, Profile headman){
        this.id = Integer.parseInt(id);
        this.name = name;
        this.teacher = teacher;
        this.headman = headman;
    }
}
