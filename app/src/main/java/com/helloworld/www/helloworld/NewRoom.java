package com.helloworld.www.helloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.helloworld.www.helloworld.Model.RoomModel;
import com.helloworld.www.helloworld.Model.User;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by himanshu on 26/7/18.
 */

public class NewRoom extends AppCompatActivity {
    private Button btnDone;
    private EditText etRoomName, etDescription;
    DatabaseReference dbref ;
    RoomModel newRoom;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_room);

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        etRoomName = (EditText) findViewById(R.id.idEtNameNewRoom);
        etDescription = (EditText) findViewById(R.id.idEtDescriptionNewRoom);

        btnDone = (Button)findViewById(R.id.idBtnDoneNewRoom);
        dbref = FirebaseDatabase.getInstance().getReference();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String createdAt = new SimpleDateFormat("ddMMyyyyHHmmss").format(Calendar.getInstance().getTime());

                if(TextUtils.isEmpty(etRoomName.getText().toString()))
                    Toast.makeText(NewRoom.this, "Room name can't be empty!", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etDescription.getText().toString()))
                {
                    Toast.makeText(NewRoom.this, "Room description can't be empty!", Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
                    String mob_no = sharedPreferences.getString(SharedPreferencesVariables.LOGGED_IN_USER, "");
                    String roomName = etRoomName.getText().toString();
                    String description = etDescription.getText().toString();
                    newRoom = new RoomModel(mob_no, createdAt, roomName, description);

                    dbref.child(FirebaseVariables.ROOMS).child(mob_no).child(createdAt).setValue(newRoom);

                    // to show in listview immediately
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("some_key", roomName);
                    resultIntent.putExtra("uid_key", mob_no);
                    resultIntent.putExtra("timestamp_key", createdAt);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
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
                Intent profile_intent = new Intent(NewRoom.this, AppUsageStatisticsActivity.class);
                startActivity(profile_intent);
                break;

            case R.id.idLogout:
//                //Toast.makeText(Home.this, "Logout is clicked", Toast.LENGTH_SHORT).show();
//                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(SharedPreferencesVariables.LOGGED_IN_USER, "No one");
//                editor.putBoolean(SharedPreferencesVariables.IS_LOGGEDIN, false);
//                editor.apply();
//                Intent logout_intent = new Intent(NewRoom.this, Login.class);
//                logout_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(logout_intent);
//                finish();
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(NewRoom.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(NewRoom.this);
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
                Intent i = new Intent(NewRoom.this, Contact_QR_Code.class);
                startActivity(i);
                break;

            default:
                Toast.makeText(NewRoom.this, "Oops... Something went wrong :(", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}
