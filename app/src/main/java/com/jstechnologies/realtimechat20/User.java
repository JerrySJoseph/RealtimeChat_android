package com.jstechnologies.realtimechat20;

import androidx.annotation.NonNull;

public class User {
    String sid,name,room;

    public User(String sid, String name, String room) {
        this.sid = sid;
        this.name = name;
        this.room = room;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @NonNull
    @Override
    public String toString() {
        return "{sid:"+sid+" name:"+name+" room:"+room;
    }
}
