package com.turan.schedule;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Schedule {
    @SerializedName("1")
    public ArrayList<Lesson> monday = null;
    @SerializedName("2")
    public ArrayList<Lesson> tuesday = null;
    @SerializedName("3")
    public ArrayList<Lesson> wednesday = null;
    @SerializedName("4")
    public ArrayList<Lesson> thursday = null;
    @SerializedName("5")
    public ArrayList<Lesson> friday = null;
    @SerializedName("6")
    public ArrayList<Lesson> saturday = null;
    @SerializedName("7")
    public ArrayList<Lesson> sunday = null;

    public Schedule(ArrayList<Lesson> monday, ArrayList<Lesson> tuesday, ArrayList<Lesson> wednesday, ArrayList<Lesson> thursday, ArrayList<Lesson> friday, ArrayList<Lesson> saturday, ArrayList<Lesson> sunday){
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }
}
