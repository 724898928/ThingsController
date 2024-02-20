package com.lixin.model;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


public class RTC_Room {
    private String roomId;
   // private User user;
    private Map<String, RTC_User> userMap = new ConcurrentHashMap<>();

    public void addUser(RTC_User user){
        if (null != user){
            this.userMap.put(user.getUserId(),user);
        }
    }

    public RTC_User getUserFromMap(String userId){
        return this.userMap.get(userId);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (null == o || getClass() != o.getClass()) return false;
        RTC_Room room = (RTC_Room) o;
        return this.roomId.equals(room.roomId);
    }

    public int hashCode(){
        return Objects.hash(this.roomId);
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
