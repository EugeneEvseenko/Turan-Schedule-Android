package com.turan.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Day {
    public String name = null;
    public boolean active = false;
    public ArrayList<Lesson> lessons = null;

    public Day(String name, ArrayList<Lesson> lessons, int num_day){
        this.name = name;
        this.lessons = lessons;
        if (new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1 == num_day) this.active = true;
        System.out.println(this.name + String.valueOf(this.active) + String.valueOf(new GregorianCalendar().get(Calendar.DAY_OF_WEEK)));
    }
}
