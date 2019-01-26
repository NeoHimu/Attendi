package com.helloworld.www.helloworld;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helloworld.www.helloworld.Model.AttendanceTimes;

/**
 * Created by himanshu on 27/7/18.
 */

public class AttendanceDetails extends AppCompatActivity {
    TextView tvStartTime, tvEndTime, tvScanTime,
            tvLastUsageBeforeStartTime, tvFirstUsageBeforeEndTime, tvPresentOrAbsent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        tvStartTime = (TextView)findViewById(R.id.tvAttendanceStartTime);
        tvLastUsageBeforeStartTime = (TextView)findViewById(R.id.tvLastUsageBeforeStartTime);
        tvFirstUsageBeforeEndTime = (TextView)findViewById(R.id.tvFirstUsageBeforeEndTime);
        tvPresentOrAbsent = (TextView)findViewById(R.id.tvPresentOrAbsent);
        tvScanTime = (TextView)findViewById(R.id.tvScanTime);
        tvEndTime = (TextView)findViewById(R.id.tvEndTime);

        String uid = AttendanceDetails.this.getIntent().getExtras().getString("uid");
        String roomId = AttendanceDetails.this.getIntent().getExtras().getString("roomId");
        String attendanceId = AttendanceDetails.this.getIntent().getExtras().getString("attendanceId");
        String studentId = AttendanceDetails.this.getIntent().getExtras().getString("studentId");

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        //leftMostTime
        dbref.child(FirebaseVariables.ATT).child(uid).child(roomId).child(attendanceId).child(studentId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String temp1 = dataSnapshot.child("leftMostTime").getValue().toString();
                        String prev1 = tvLastUsageBeforeStartTime.getText().toString();
                        tvLastUsageBeforeStartTime.setText(prev1 + " "+ temp1);

                        String temp2 = dataSnapshot.child("startTime").getValue().toString();
                        String prev2 = tvStartTime.getText().toString();
                        tvStartTime.setText(prev2 + " "+ temp2);

                        String temp3 = dataSnapshot.child("scanTimeByStudent").getValue().toString();
                        String prev3 = tvScanTime.getText().toString();
                        tvScanTime.setText(prev3 + " "+ temp3);
//
                        String temp4 = dataSnapshot.child("endTime").getValue().toString();
                        String prev4 = tvEndTime.getText().toString();
                        tvEndTime.setText(prev4 + " "+ temp4);
//
                        String temp = dataSnapshot.child("rightmostTime").getValue().toString();
                        String prev = tvFirstUsageBeforeEndTime.getText().toString();
                        tvFirstUsageBeforeEndTime.setText(prev + " "+ temp);

                        if(prev1.equalsIgnoreCase(prev))
                        {
                            tvPresentOrAbsent.setText("Present");
                        }
                        else
                        {
                            tvPresentOrAbsent.setText("Absent");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.idAppsUsage:
                //Toast.makeText(Home.this, "Profile is clicked", Toast.LENGTH_SHORT).show();
                Intent profile_intent = new Intent(AttendanceDetails.this, AppUsageStatisticsActivity.class);
                startActivity(profile_intent);
                break;

//            case R.id.idNewRoom:
//                Toast.makeText(RoomModel.this, "New RoomModel is clicked", Toast.LENGTH_SHORT).show();
//                break;
//
//            case R.id.idSetting:
//                //o be added : this is done to get a back button from the setting activity
//                //Toast.makeText(MainActivity.this, "Settings is clicked", Toast.LENGTH_SHORT).show();
//                Intent setting_intent = new Intent(RoomModel.this, SettingsActivity.class);
//                startActivity(setting_intent);
//                break;

            case R.id.idLogout:
//                //Toast.makeText(Home.this, "Logout is clicked", Toast.LENGTH_SHORT).show();
//                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(SharedPreferencesVariables.LOGGED_IN_USER, "No one");
//                editor.putBoolean(SharedPreferencesVariables.IS_LOGGEDIN, false);
//                editor.apply();
//                Intent logout_intent = new Intent(AttendanceDetails.this, Login.class);
//                logout_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(logout_intent);
//                finish();
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(AttendanceDetails.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(AttendanceDetails.this);
                }
                builder.setTitle("No Logout!")
                        .setMessage("This is intentional. It's done to avoid proxy.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(PrepareQRCode.this, position+"", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;

            case R.id.idShareContact:
                Intent i = new Intent(AttendanceDetails.this, Contact_QR_Code.class);
                startActivity(i);
                break;

            default:
                Toast.makeText(AttendanceDetails.this, "Oops... Something went wrong :(", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
