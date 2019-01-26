package com.helloworld.www.helloworld.Model;

/**
 * Created by himanshu on 29/7/18.
 */

public class AttendanceTimes {
    String scanTimeByStudent;
    String startTime;
    String endTime;
    String leftMostTime;
    String rightmostTime;

    public AttendanceTimes() {
    }

    public AttendanceTimes(String scanTimeByStudent, String startTime, String endTime, String leftMostTime, String rightMostTime) {
        this.scanTimeByStudent = scanTimeByStudent;
        this.startTime = startTime;
        this.endTime = endTime;
        this.leftMostTime = leftMostTime;
        this.rightmostTime = rightMostTime;
    }


    public void setScanTimeByStudent(String scanTimeByStudent) {
        this.scanTimeByStudent = scanTimeByStudent;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setLeftMostTime(String leftMostTime) {
        this.leftMostTime = leftMostTime;
    }

    public void setRightmostTime(String rightMostTime) {
        this.rightmostTime = rightMostTime;
    }

    public String getScanTimeByStudent() {
        return scanTimeByStudent;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLeftMostTime() {
        return leftMostTime;
    }

    public String getRightmostTime() {
        return rightmostTime;
    }
}
