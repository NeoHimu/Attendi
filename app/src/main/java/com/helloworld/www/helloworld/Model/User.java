package com.helloworld.www.helloworld.Model;

/**
 * Created by himanshu on 20/7/18.
 */

public class User {
    private String fName;
    private String lName;
    private String rollNo;
    private String emailId;
    private String phoneNo;
    private String password;
    private boolean isRoomCreator;


    public User(String fName, String lName, String rollNo, String emailId, String phoneNo, String password, boolean isTeacher) {
        this.fName = fName;
        this.lName = lName;
        this.rollNo = rollNo;
        this.emailId = emailId;
        this.phoneNo = phoneNo;
        this.password = password;
        this.isRoomCreator = isTeacher;
    }

    public User() {
    }


    public void setTeacher(boolean teacher) {
        isRoomCreator = teacher;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public boolean isTeacher() {
        return isRoomCreator;
    }
}
