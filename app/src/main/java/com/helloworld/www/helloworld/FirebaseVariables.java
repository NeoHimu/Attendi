package com.helloworld.www.helloworld;

/**
 * Created by himanshu on 28/7/18.
 */

public class FirebaseVariables {
    public static String USERS = "USERS"; // Firebase parent node name for all users;
    public static String ROOMS = "ROOMS";// parent node for all new rooms created
    public static String ATT = "ATTENDANCE"; // this is for list of attendance on scan tab.
    public static String ATT_EACH_USER = "ATTENDANCE_USERWISE";// for each user, there is a table maintained for his attendance in different rooms.
    public static String ALLOWED_APPS = "appsWhichAreAllowedToBeOpened";// This is important because some system apps are already started without user intervention

}
