package com.turan.schedule;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

public class Lesson {
    @SerializedName("num-lesson")
    public int num_lesson = 0;
    @SerializedName("num-room")
    public int num_room = 0;
    @SerializedName("name")
    public String name = null;
    @SerializedName("active")
    public boolean active = false;
    @SerializedName("teacher")
    public Teacher teacher = null;
    @SerializedName("call")
    public Call call = null;

    public Lesson(String num_lesson, String num_room, String name, boolean active, Teacher teacher, Call call){
        this.num_lesson = Integer.parseInt(num_lesson);
        this.num_room = Integer.parseInt(num_room);
        this.name = name;
        this.active = active;
        this.teacher = teacher;
        this.call = call;
    }

    public String getTitle(){
        return String.format("%d.%s", this.num_lesson, this.name);
    }
    public String getSubtitle(Context context){
        return String.format(context.getString(R.string.lesson_mask), this.call.start.substring(0, this.call.start.lastIndexOf(":")), this.call.end.substring(0, this.call.end.lastIndexOf(":")), teacher.name, this.num_room);
    }
}
