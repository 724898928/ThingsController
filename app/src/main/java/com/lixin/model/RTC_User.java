package com.lixin.model;

import com.google.gson.Gson;

import lombok.Data;

@Data
public class RTC_User {
    public String userId;
    public String userName;

    public String getUserId() {
       return this.userId;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
