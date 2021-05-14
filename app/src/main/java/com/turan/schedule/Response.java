package com.turan.schedule;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Response {
    @SerializedName("is_error")
    public boolean is_error = true;
    @SerializedName("errors")
    public List<Error> errors = null;
    @SerializedName("token")
    public String token = null;
    @SerializedName("response")
    public String response = null;
    @SerializedName("profile")
    public Profile profile = null;
    @SerializedName("calls")
    public ArrayList<Call> calls = null;
    @SerializedName("schedule")
    public Schedule schedule = null;
    public Response(boolean is_error, List<Error> errors, String token, String response, Profile profile, ArrayList<Call> calls, Schedule schedule){
        this.is_error = is_error;
        this.errors = errors;
        this.token = token;
        this.response = response;
        this.profile = profile;
        this.calls = calls;
        this.schedule = schedule;
    }

    public void printErrors() {
        if(errors.size() > 0){
            for (Error error:errors) {
                System.out.println(error.toString());
            }
        }else {
            System.out.println("Errors not found!");
        }
    }
}
