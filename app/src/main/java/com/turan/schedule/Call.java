package com.turan.schedule;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

import com.google.gson.annotations.SerializedName;

public class Call {
    @SerializedName("id")
    public int id = 0;
    @SerializedName("start")
    public String start = null;
    @SerializedName("end")
    public String end = null;
    @SerializedName("pause-min")
    public int pause = 0;
    @SerializedName("active")
    public boolean active = false;

    public Call(String id, String start, String end, String pause, boolean active){
        this.id = Integer.parseInt(id);
        this.start = start.substring(0, start.lastIndexOf(":") - 1);
        this.end = end;
        this.pause = Integer.parseInt(pause);
        this.active = active;
    }

    public String getTitle(Context context) {
        return String.format(context.getString(R.string.call_title), this.id);
    }
    public String getSubtitle(Context context) {
        if(this.pause != 0) {
            return String.format(context.getString(R.string.call_long_desc), this.start.substring(0, this.start.lastIndexOf(":")), this.end.substring(0, this.end.lastIndexOf(":")), this.pause);
        }
        else {
            return String.format(context.getString(R.string.call_short_desc), this.start.substring(0, this.start.lastIndexOf(":")), this.end.substring(0, this.end.lastIndexOf(":")));
        }
    }
}
