package com.helloworld.www.helloworld;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.w3c.dom.Text;

/**
 * Created by himanshu on 26/7/18.
 */

public class Room extends AppCompatActivity {
    TextView tvRoomName, tvRoomDescription, tvCreatedAt, tvCreatedBy, tvAttandanceCount, tvScanTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        tvRoomName = (TextView)findViewById(R.id.idTvNameRoom);
        tvRoomDescription = (TextView)findViewById(R.id.idTvDescriptionRoom);
        tvCreatedAt = (TextView)findViewById(R.id.itTvCreatedOn);
        tvCreatedBy = (TextView)findViewById(R.id.idTvCreator);
        tvAttandanceCount = (TextView)findViewById(R.id.idTvAttendance);
        tvScanTime = (TextView)findViewById(R.id.idScanTime);

        String roomName = Room.this.getIntent().getExtras().getString("roomName");
        tvRoomName.setText(roomName);

        String roomDescription = Room.this.getIntent().getExtras().getString("roomDescription");
        tvRoomDescription.setText(roomDescription);

        String roomCreatedBy = Room.this.getIntent().getExtras().getString("roomCreatedBy");
        tvCreatedBy.setText(roomCreatedBy);

        String roomCreatedAtTime = Room.this.getIntent().getExtras().getString("roomCreatedAtTime");
        tvCreatedAt.setText(roomCreatedAtTime);

        String attendanceCount = Room.this.getIntent().getExtras().getString("attendanceCount");
        String temp1 = tvAttandanceCount.getText().toString();
        tvAttandanceCount.setText(temp1 + attendanceCount);

        String scanTime = Room.this.getIntent().getExtras().getString("scanTime");
        String temp2 = tvScanTime.getText().toString();
        tvScanTime.setText(temp2 + scanTime);

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
                Intent profile_intent = new Intent(Room.this, AppUsageStatisticsActivity.class);
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
//
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(Room.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(Room.this);
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
                Intent i = new Intent(Room.this, Contact_QR_Code.class);
                startActivity(i);
                break;

            default:
                Toast.makeText(Room.this, "Oops... Something went wrong :(", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}
