package com.lixin.model;

import com.google.gson.Gson;


public class RTC_Message<T extends Object> {
    private String type;

    private T data;

    public RTC_Message(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public Object getData(){
        return this.data;
    }

    public String getType(){
        return this.type;
    }

    @Override
    public String toString(){
        return new Gson().toJson(this);
    }
}
