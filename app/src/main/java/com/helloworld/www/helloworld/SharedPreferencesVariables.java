package com.helloworld.www.helloworld;

/**
 * Created by himanshu on 24/7/18.
 */

public class SharedPreferencesVariables {
    public static String SHARED_PREFS = "sharedPrefs"; // key
    public static String FIRSTNAME = "fName";// key
    public static String LASTNAME = "lName"; // key
    public static String ROLLNO = "rollNo";// key
    public static String MAILID = "mailId";// key
    public static String IS_FORGOT_PWD = "isForgotPwd";
    public static String LOGGED_IN_USER = "loggedInUser"; // Use this variable instead of MOB_NO
    public static String MOB_VERIFIED = "mobileNoVerified";
    public static String MOB_NO = "mobileNo";
    public static String IS_LOGGEDIN = "isloggedin";
    public static String ROOMS = "setOfRooms";
    public static String IS_FIRST_RUN = "isFirstRun";

    // flag variable with four states : 1,2,3
    // 1 : Generate QR code
    // 2 : Start the attendance
    // 3 : End the attendance
    public static String CONTROL = "controlButtonsOnGenerateQRCodeActivity";


    // check if app usage permission is granted or not
    public static String APP_USAGE_PERM_GRANTED = "appUsagePermissionGranted";

    /// TinyDB
    public static String LOGGEDIN_USER = "loggedInUserDetails";
    public static String NEW_ROOM = "newlyCreatedRoomByTeacher";
    public static String ROOM_LIST_HOSTED = "roomListCreatedByHost";


}
