package com.helloworld.www.helloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.helloworld.www.helloworld.Model.AttendanceTimes;
import com.helloworld.www.helloworld.Model.AttendedTabInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by himanshu on 25/7/18.
 */

public class Attended extends Fragment {
    private static final String TAG = "Attended";
    DateFormat mDateFormat;
    TextView tvTextView;
    public static IntentResult result;
    private Button scanBtn, btnSubmitAttendance;
    private TextView tvScanFormat, tvScanContent;
    private LinearLayout llSearch;
    public static ListView listView;
    public static ArrayList<String> list;
    public static ArrayList<String> listRoomDescription;
    public static ArrayList<String> listCreatedBy;
    public static ArrayList<String> listCreatedAt;
    public static ArrayList<String> listScanTime;
    public static ArrayList<String> listAttendanceCount;
    public static ArrayList<String> appsWithCameraPermissionButAreDefault;
    public static ArrayList<String> app_list;
    public static ArrayList<String> app_usage_time;
    String temp_string = "";
    private String m_Text = "";
    public static String scanTime = "";
    public static String leftMostTime = "";
    public static String righMostTime = "";
    public static String leftAppName = "";
    public static String rightAppName="";
    public static String uids[] = new String [3];

    public static ArrayAdapter<String> arrayAdapter;
    DatabaseReference dbref;
    String roomName="";
    String roomDescription="";
    String roomCreatedBy="";
    String roomCreatedAtTime="";
    public static String logged_in_user;
    public static boolean isQRCodeValid=false;
    SharedPreferences sharedPreferences;
    UsageStatsManager mUsageStatsManager;

    public Attended()
    {
        // publis constructor is required in fragment
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attended, container,false);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, Context.MODE_PRIVATE);
        logged_in_user = sharedPreferences.getString(SharedPreferencesVariables.LOGGED_IN_USER,"");
        dbref = FirebaseDatabase.getInstance().getReference();
        scanBtn = (Button) view.findViewById(R.id.idScanQRCode);
//        tvScanFormat = (TextView) view.findViewById(R.id.tvScanFormat);
//        tvScanContent = (TextView) view.findViewById(R.id.tvScanContent);
        tvTextView = (TextView)view.findViewById(R.id.idTextOnAttendedPage);
        btnSubmitAttendance = (Button)view.findViewById(R.id.idSubmitAttendance);
        llSearch = (LinearLayout) view.findViewById(R.id.llSearch);
        listView = (ListView)view.findViewById(R.id.idListViewAttended);
        list = new ArrayList<String>();
        listRoomDescription = new ArrayList<String>();
        listCreatedBy = new ArrayList<String>();
        listCreatedAt = new ArrayList<String>();
        listScanTime = new ArrayList<String>();
        listAttendanceCount = new ArrayList<String>();
         arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_expandable_list_item_1,
                android.R.id.text1,
                list
        ){
             @Override
             public View getView(int position, View convertView, ViewGroup parent) {
                 TextView textView = (TextView) super.getView(position, convertView, parent);
                 textView.setTextColor(getContext().getResources().getColor(R.color.colorText));
                 return textView;
             }
         };
        listView.setAdapter(arrayAdapter);

        dbref = FirebaseDatabase.getInstance().getReference();

        btnSubmitAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scanTime.equalsIgnoreCase(""))
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    builder.setTitle("Not now!")
                            .setMessage("Press it after teacher ends the attendance!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else
                { // make sure attendance is taken and not contact is exchanged so that uids are filled properly
                    dbref.child(FirebaseVariables.ATT).child(uids[0]).child(uids[1]).child(uids[2]).child(uids[0]).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // set the current time as rightmost time
                            //righMostTime = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                            // obtain start and end time from dataSnapshot

                            // find the app with camera permission which is used most recently
                            for(int k=0;k<app_list.size();k++)
                            {
                                if(!appsWithCameraPermissionButAreDefault.contains(app_list.get(k)))
                                {
                                    temp_string = app_list.get(k) + " " + app_usage_time.get(k);
                                    righMostTime = app_usage_time.get(k);
                                    rightAppName = app_list.get(k);
                                    break;
                                }
                            }
                            //tvTextView.setText(temp_string);

                            String startTime = dataSnapshot.child("startTime").getValue().toString();
                            String endTime = dataSnapshot.child("endTime").getValue().toString();

                            if(endTime.equalsIgnoreCase("NA"))
                            {
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                                } else {
                                    builder = new AlertDialog.Builder(getActivity());
                                }
                                builder.setTitle("Not now!")
                                        .setMessage("Press it after teacher ends the attendance!")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                            else
                            {
                                if(!leftMostTime.equalsIgnoreCase(righMostTime))
                                {
                                    AlertDialog.Builder builder;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                                    } else {
                                        builder = new AlertDialog.Builder(getActivity());
                                    }
                                    builder.setTitle("This will be reported!")
                                            .setMessage("You opened "+ rightAppName+" at "+righMostTime)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                                AttendanceTimes att = new AttendanceTimes(scanTime,startTime,endTime,leftMostTime,righMostTime);
                                dbref.child(FirebaseVariables.ATT).child(uids[0]).child(uids[1]).child(uids[2]).child(logged_in_user).setValue(att);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        dbref.child(FirebaseVariables.ATT_EACH_USER).child(logged_in_user).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String,Object> attendance = (Map<String,Object>) dataSnapshot.getValue();
                        if(attendance==null) // when no room has been created
                            return;
                        //iterate through each user, ignoring their UID
                        for (Map.Entry<String, Object> entry : attendance.entrySet()){

                            //Get user map
                            Map singleAttendance = (Map) entry.getValue();

                            list.add(0, singleAttendance.get("roomName").toString());
                            listRoomDescription.add(0,singleAttendance.get("roomDescription").toString());
                            listCreatedBy.add(0,singleAttendance.get("createdBy").toString());
                            listCreatedAt.add(0,singleAttendance.get("createdAtTime").toString());
                            listAttendanceCount.add(0,singleAttendance.get("attendanceCount").toString());
                            listScanTime.add(0,singleAttendance.get("scanTime").toString());

                            arrayAdapter.notifyDataSetChanged(); // IMPORTANT to add this line
                            //Toast.makeText(getContext(), list.size()+"", Toast.LENGTH_SHORT).show();
                            //Get phone field and append to list
                            //list.add((Long) singleUser.get("phone"));
                            //Log.d("tmz",""+singleAttendance.get("market_name"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                llSearch.setVisibility(View.GONE);

                IntentIntegrator integrator = new IntentIntegrator(getActivity());

                integrator.setPrompt("Scan a QRcode");

                integrator.setOrientationLocked(false);

                integrator.forSupportFragment(Attended.this).initiateScan();

                //        Use this for more customization
//        IntentIntegrator integrator = new IntentIntegrator(this);
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
//        integrator.setPrompt("Scan a barcode");
//        integrator.setCameraId(0);  // Use a specific camera of the device
//        integrator.setBeepEnabled(false);
//        integrator.setBarcodeImageEnabled(true);
//        integrator.initiateScan();
            }
        });

        // go to new_room activity on clicking an item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getContext(), Room.class);
                i.putExtra("roomName", list.get(position));
                i.putExtra("roomDescription", listRoomDescription.get(position));
                i.putExtra("roomCreatedBy", listCreatedBy.get(position));
                i.putExtra("roomCreatedAtTime", listCreatedAt.get(position));
                i.putExtra("attendanceCount", listAttendanceCount.get(position));
                i.putExtra("scanTime", listScanTime.get(position));
                startActivity(i);
            }
        });


        // remove an item on long press
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                // TODO Auto-generated method stub
                final int position = pos;
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getActivity());
                }

                builder.setTitle("Delete")
                        .setMessage("Do you want to remove it from here?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // remove from the UI
                            list.remove(position);
                            arrayAdapter.notifyDataSetChanged();
                                // remove from the database
                                dbref.child(FirebaseVariables.ATT_EACH_USER).child(logged_in_user).child(listScanTime.get(position)).removeValue();
                                //remove from all other lists
                            listRoomDescription.remove(position);
                            listCreatedBy.remove(position);
                            listCreatedAt.remove(position);
                            listScanTime.remove(position);
                            listAttendanceCount.remove(position);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // This code is for apps usage purpose
        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService("usagestats"); //Context.USAGE_STATS_SERVICE

        List<UsageStats> usageStatsList =
                getUsageStatistics(UsageStatsManager.INTERVAL_DAILY);
        Collections.sort(usageStatsList, new Attended.LastTimeLaunchedComparatorDesc());
        List<CustomUsageStats> tempList = updateAppsList(usageStatsList);

        mDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //long lastTimeUsed = tempList.get(0).usageStats.getLastTimeUsed();
        //tvTextView.setText(mDateFormat.format(new Date(lastTimeUsed)));
        //tempList.get(0).usageStats.getPackageName();

        // getting list of apps with  camera permission
        PackageManager pm = getActivity().getPackageManager();
        List packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        app_list = new ArrayList<String>();
        app_usage_time = new ArrayList<String>();
        appsWithCameraPermissionButAreDefault = new ArrayList<String>();

        for(int idx=0;idx<tempList.size()&&idx<10;idx++)
        {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(tempList.get(idx).usageStats.getPackageName(), PackageManager.GET_PERMISSIONS);
                //Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;
                if(requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        Log.d("test", requestedPermissions[i]);

                        //////////////////////////////////////
                        //////////////////////////////////////
                        // Look for the desired permission here
                        //////////////////////////////////////
                        //////////////////////////////////////
                        if(requestedPermissions[i].equals("android.permission.CAMERA"))
                        {
                            // here add() is used instead of add(0, ...) to make sure the ascending order of time is maintained
                            app_list.add(tempList.get(idx).usageStats.getPackageName());
                            app_usage_time.add(mDateFormat.format(new Date(tempList.get(idx).usageStats.getLastTimeUsed())));
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> temp_app_list = new ArrayList<String>();
        dbref.child(FirebaseVariables.ALLOWED_APPS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //iterate through each user, ignoring their UID
                for (DataSnapshot entry : dataSnapshot.getChildren()){
                    appsWithCameraPermissionButAreDefault.add(0,entry.getValue().toString());
                }

                // find the app with camera permission which is used most recently
                for(int k=0;k<app_list.size();k++)
                {
                    if(!appsWithCameraPermissionButAreDefault.contains(app_list.get(k)))
                    {
                        temp_string = app_list.get(k) + " " + app_usage_time.get(k);
                        leftMostTime = app_usage_time.get(k);
                        leftAppName = app_list.get(k);
                        break;
                    }
                }
                //tvTextView.setText(temp_string);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        if (queryUsageStats.size() == 0) {
            Log.i(TAG, "The user may not allow the access to apps usage. ");
            Toast.makeText(getActivity(),
                    getString(R.string.explanation_access_to_appusage_is_not_enabled),
                    Toast.LENGTH_LONG).show();
//            mOpenUsageSettingButton.setVisibility(View.VISIBLE);
//            mOpenUsageSettingButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//                }
//            });
        }
        return queryUsageStats;
    }

    //VisibleForTesting
    public List<CustomUsageStats> updateAppsList(List<UsageStats> usageStatsList) {
        List<CustomUsageStats> customUsageStatsList = new ArrayList<>();
        for (int i = 0; i < usageStatsList.size(); i++) {
            CustomUsageStats customUsageStats = new CustomUsageStats();
            customUsageStats.usageStats = usageStatsList.get(i);
            try {
                Drawable appIcon = getActivity().getPackageManager()
                        .getApplicationIcon(customUsageStats.usageStats.getPackageName());
                customUsageStats.appIcon = appIcon;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, String.format("App Icon is not found for %s",
                        customUsageStats.usageStats.getPackageName()));
                customUsageStats.appIcon = getActivity()
                        .getDrawable(R.drawable.ic_default_app_launcher);
            }
            customUsageStatsList.add(customUsageStats);
        }
        return customUsageStatsList;
    }

    /**
     * The {@link Comparator} to sort a collection of {@link UsageStats} sorted by the timestamp
     * last time the app was used in the descendant order.
     */
    private static class LastTimeLaunchedComparatorDesc implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
        }
    }
    //VisibleForTesting
    static enum StatsUsageInterval {
        DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
        WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
        MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY),
        YEARLY("Yearly", UsageStatsManager.INTERVAL_YEARLY);

        private int mInterval;
        private String mStringRepresentation;

        StatsUsageInterval(String stringRepresentation, int interval) {
            mStringRepresentation = stringRepresentation;
            mInterval = interval;
        }

        static Attended.StatsUsageInterval getValue(String stringRepresentation) {
            for (Attended.StatsUsageInterval statsUsageInterval : values()) {
                if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                    return statsUsageInterval;
                }
            }
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getContext(), "Scanned!", Toast.LENGTH_SHORT).show();

        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "Added Contact", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Toast.makeText(getActivity(), "Cancelled Added Contact", Toast.LENGTH_SHORT).show();
            }
        }

        if (result != null) {

            if (result.getContents() == null) {

                llSearch.setVisibility(View.GONE);
                llSearch.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();

            } else {

                llSearch.setVisibility(View.VISIBLE);

                uids = result.getContents().split(":");
                if(uids.length==3)
                {
//                    //Toast.makeText(getActivity(), "This is a 12 digit number", Toast.LENGTH_SHORT).show();
//                    AlertDialog.Builder builder;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
//                    } else {
//                        builder = new AlertDialog.Builder(getActivity());
//                    }
//
//                    builder.setTitle("Name for "+uids[1]);
//
//// Set up the input
//                    final EditText input = new EditText(getActivity());
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                    builder.setView(input);
//
//// Set up the buttons
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            m_Text = input.getText().toString();
//                        }
//                    });
//                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//
//                    builder.show();

                    // add new contact
                    Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                    contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                    contactIntent
                            .putExtra(ContactsContract.Intents.Insert.NAME, "")
                            .putExtra(ContactsContract.Intents.Insert.PHONE, uids[1]);

                    startActivityForResult(contactIntent, 1);

                    return;
                }

                uids = result.getContents().split("/");
                //Toast.makeText(getContext(), uids[2], Toast.LENGTH_SHORT).show();
                if (uids.length < 3)
                {
                    Toast.makeText(getActivity(), "Not a valid QR code!", Toast.LENGTH_LONG).show();
                    return;
                }

                final String scan_time = new SimpleDateFormat("ddMMyyyyHHmmss").format(Calendar.getInstance().getTime());
                scanTime =  new SimpleDateFormat("dd:MM:yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(FirebaseVariables.ATT).exists()
                                && dataSnapshot.child(FirebaseVariables.ATT).child(uids[0]).exists()
                                && dataSnapshot.child(FirebaseVariables.ATT).child(uids[0]).child(uids[1]).exists()
                                && dataSnapshot.child(FirebaseVariables.ATT).child(uids[0]).child(uids[1]).child(uids[2]).exists())
                        {

                            Toast.makeText(getActivity(), "Valid QR Code!", Toast.LENGTH_SHORT).show();

                        // again do a database search for room details
                            dbref.child(FirebaseVariables.ROOMS).child(uids[0]).child(uids[1]).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Object objName = dataSnapshot.child("roomName").getValue();
                                    if(objName!=null)
                                        roomName = objName.toString();

                                    Object objDescription = dataSnapshot.child("roomDescription").getValue();
                                    if(objDescription!=null)
                                        roomDescription = objDescription.toString();

                                    Object objCreatedBy = dataSnapshot.child("createdBy").getValue();
                                    if(objCreatedBy!=null)
                                        roomCreatedBy = objCreatedBy.toString();

                                    Object objCreatedAtTime = dataSnapshot.child("createdAtTime").getValue();
                                    if(objCreatedAtTime!=null)
                                        roomCreatedAtTime = objCreatedAtTime.toString();

                                    list.add(0,roomName);
                                    // updation in all these list is mandatory to avoid runtime crash as these list are getting used in realtime but they get data only after reload. OutOfboundException
                                    listRoomDescription.add(0, roomDescription);
                                    listCreatedBy.add(0, roomCreatedBy);
                                    listCreatedAt.add(0, roomCreatedAtTime);
                                    listScanTime.add(0, scan_time);
                                    listAttendanceCount.add(0, 0+"");
                                    arrayAdapter.notifyDataSetChanged(); // so that this added element is reflected in the view
                                    // add this attendance in new table i.e. ATTENDED_ROOMS
                                    // writing into database
                                    AttendedTabInfo temp = new AttendedTabInfo(roomName, roomDescription, roomCreatedBy, roomCreatedAtTime, 0+"", scan_time);
                                    dbref.child(FirebaseVariables.ATT_EACH_USER).child(logged_in_user).child(scan_time).setValue(temp);

                                    // write in database : ATTENDANCE -> creator_uid -> qr_code_generation_time -> uid of scanning user
                                    AttendanceTimes attendanceTimes = new AttendanceTimes("","","","","");
                                    dbref.child(FirebaseVariables.ATT).child(uids[0]).child(uids[1]).child(uids[2]).child(logged_in_user).setValue(attendanceTimes);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(getContext(), "Invalid QR Code!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                 //tvScanContent.setText(result.getContents());

                //tvScanFormat.setText(result.getFormatName());

            }

        } else {

            super.onActivityResult(requestCode, resultCode, data);

        }


    }

}
