package com.helloworld.www.helloworld.Model;

/**
 * Created by himanshu on 29/7/18.
 */

public class AttendedTabInfo {
    String roomName;
    String roomDescription;
    String createdBy;
    String createdAtTime;
    String attendanceCount;
    String scanTime;

    public AttendedTabInfo() {
    }

    public AttendedTabInfo(String roomName, String roomDescription, String createdBy, String createdAtTime, String attendanceCount, String scanTime) {
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.createdBy = createdBy;
        this.createdAtTime = createdAtTime;
        this.attendanceCount = attendanceCount;
        this.scanTime = scanTime;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAtTime() {
        return createdAtTime;
    }

    public void setCreatedAtTime(String createdAtTime) {
        this.createdAtTime = createdAtTime;
    }

    public String getAttendanceCount() {
        return attendanceCount;
    }

    public void setAttendanceCount(String attendanceCount) {
        this.attendanceCount = attendanceCount;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }
}
