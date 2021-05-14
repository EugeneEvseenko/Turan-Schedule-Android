package com.turan.schedule;


import com.google.gson.annotations.SerializedName;

public class Error {
    @SerializedName("error_code")
    public int error_id = 0;
    @SerializedName("text")
    public String text;

    public Error(int error_id, String text){
        this.error_id = error_id;
        this.text = text;
    }
    @Override
    public String toString() {
        return String.format("id:%d text: %s",error_id,text);
    }
}