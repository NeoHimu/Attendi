package com.helloworld.www.helloworld.Model;


import android.content.pm.PackageManager;

import com.helloworld.www.helloworld.SharedPreferencesVariables;

/**
 * Created by himanshu on 20/7/18.
 */

public class RoomModel {
    private String createdBy_uid;
    private String createdAtTime;
    private String roomName;
    private String roomDescription;

    public RoomModel()
    {

    }
    public RoomModel(String createdBy, String createdAtTime, String roomName, String roomDescription) {
        this.createdBy_uid = createdBy;
        this.createdAtTime = createdAtTime;
        this.roomName = roomName;
        this.roomDescription = roomDescription;
    }

    public String getCreatedBy()
    {
        return this.createdBy_uid;
    }
    public String getCreatedAtTime()
    {
        return this.createdAtTime;
    }
    public String getRoomName()
    {
        return this.roomName;
    }

    public String getRoomDescription()
    {
        return this.roomDescription;
    }

    void setCreatedBy(String user)
    {
        this.createdBy_uid = user;
    }

    void setCreatedAtTime(String createdAtTime)
    {
        this.createdAtTime = createdAtTime;
    }

    void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }

    void setRoomDescription(String roomDescription)
    {
        this.roomDescription = roomDescription;
    }
}
