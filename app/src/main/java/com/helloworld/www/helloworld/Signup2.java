package com.helloworld.www.helloworld;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helloworld.www.helloworld.Model.User;

import java.util.Random;

/**
 * Created by himanshu on 17/7/18.
 */

public class Signup2 extends AppCompatActivity {
    Button btVerify, btNext;
    EditText etPhone;
    TextView tvSent, tvReceived, tvVerified;
    public static boolean isVerified=false;
    public static boolean isPermissionGranted = false; // permission to read message and send message

    FirebaseDatabase database;
    DatabaseReference dbref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup2);
        //toolbar is added
        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        // Firebase
        database = FirebaseDatabase.getInstance();
        dbref = database.getReference();

        btVerify = (Button) findViewById(R.id.idBtMobileVerify);
        etPhone = (EditText) findViewById(R.id.idMobileNo);
        tvSent = (TextView) findViewById(R.id.idTvCodeSent);
        tvReceived = (TextView) findViewById(R.id.idTvCodeReceived);
        tvVerified = (TextView) findViewById(R.id.idTvCodeVerified);
        btNext = (Button) findViewById(R.id.idBtVerifyNext);

        final SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
        isVerified = sharedPreferences.getBoolean(SharedPreferencesVariables.MOB_VERIFIED, false);
        if (isVerified == false){
            btVerify.setEnabled(true);
        }
        else
        {
            btVerify.setEnabled(false);
            btVerify.setBackgroundColor(0xafcbc5); // hint color
            btVerify.setText("Verified!");
        }

        //Open a Dialog using the code below:
        ActivityCompat.requestPermissions(Signup2.this,
                new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS},
                1);


        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVerified==false)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(Signup2.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(Signup2.this);
                    }
                    builder.setTitle("Mobile number verification required!")
                            .setMessage("Please verify your mobile number.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else
                {
                    // show an alert that mobile number already exists if it is a signup process!
                    // if it is a password reset process then the mobile number should already be in the database
                   // SharedPreferences sharedPreferences1 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
                    //final boolean hasComeFromForgotPwd = sharedPreferences1.getBoolean(SharedPreferencesVariables.IS_FORGOT_PWD, false);

                    final String phone_no = etPhone.getText().toString();
                    final SharedPreferences sharedPreferences1 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences1.edit();

                    // save data from MainActivity, Signup2 and Signup3 in the Firebase database
                    String fName = sharedPreferences1.getString(SharedPreferencesVariables.FIRSTNAME, "");
                    String lName = sharedPreferences1.getString(SharedPreferencesVariables.LASTNAME, "");
                    String roll = sharedPreferences1.getString(SharedPreferencesVariables.ROLLNO, "");
                    String mailid = sharedPreferences1.getString(SharedPreferencesVariables.MAILID,"");
                    //final String mobno = sharedPreferences1.getString(SharedPreferencesVariables.MOB_NO, "");

                    final User user = new User(fName, lName, roll, mailid, phone_no, "",false);

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // save new user in the database
                            dbref.child(FirebaseVariables.USERS).child(user.getPhoneNo()).setValue(user);

                            // on successful write to the database, mark the user as logged in
                            editor.putString(SharedPreferencesVariables.LOGGED_IN_USER, user.getPhoneNo());
                            editor.putBoolean(SharedPreferencesVariables.IS_LOGGEDIN, true);
                            editor.apply();
                            //Toast.makeText(Signup3.this, "Database write occuring!", Toast.LENGTH_SHORT).show();
                            // only after successful write, go to the next activity
                            boolean isAppUsagePermGranted = sharedPreferences1.getBoolean(SharedPreferencesVariables.APP_USAGE_PERM_GRANTED, false);

                            if(isAppUsagePermGranted)
                            {
                                Intent i = new Intent(Signup2.this, HomeTabbedActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                            else
                            {
                                Intent i = new Intent(Signup2.this, Home.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //Toast.makeText(Signup3.this, "Database writing cancelled!", Toast.LENGTH_SHORT).show();
                            Toast.makeText(Signup2.this, "Database write error! Please try again later!", Toast.LENGTH_SHORT).show();
//                            editor.putBoolean(SharedPreferencesVariables.IS_LOGGEDIN, false);
//                            editor.apply();
                        }
                    });






//                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (!dataSnapshot.child(FirebaseVariables.USERS).hasChild("1234")) // this number does not exist
//                            {
//                                if (hasComeFromForgotPwd == true) {
//                                    AlertDialog.Builder builder;
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                        builder = new AlertDialog.Builder(Signup2.this, android.R.style.Theme_Material_Dialog_Alert);
//                                    } else {
//                                        builder = new AlertDialog.Builder(Signup2.this);
//                                    }
//
//                                    builder.setTitle("Mobile number check..")
//                                            .setMessage("This mobile number is not registered with us. Please register with it.")
//                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    // make isForgot = false in sharedpreference
//                                                    SharedPreferences sharedPreferences2 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor = sharedPreferences2.edit();
//                                                    editor.putBoolean(SharedPreferencesVariables.IS_FORGOT_PWD, false);
//                                                    editor.apply();
//                                                }
//                                            })
//                                            .setIcon(android.R.drawable.ic_dialog_alert)
//                                            .show();
//                                } else // new registration
//                                {
//                                    // move to the next activity
//                                    // save the verified mobile number in the shared preference
//                                    SharedPreferences sharedPreferences2 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = sharedPreferences2.edit();
//                                    editor.putString(SharedPreferencesVariables.MOB_NO, etPhone.getText().toString());
//                                    editor.apply();
//                                    // go to the next activity
//                                    Intent i = new Intent(Signup2.this, Signup3.class);
//                                    startActivity(i);
//                                }
//                            }
//                            else // this number exists
//                            {
//                                if(hasComeFromForgotPwd==true)
//                                {
//                                    // move to the next activity
//                                    // save the verified mobile number in the shared preference
//                                    SharedPreferences sharedPreferences2 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = sharedPreferences2.edit();
//                                    editor.putString(SharedPreferencesVariables.MOB_NO, etPhone.getText().toString());
//                                    editor.apply();
//                                    // go to the next activity
//                                    Intent i = new Intent(Signup2.this, Signup3.class);
//                                    startActivity(i);
//                                }
//                                else
//                                {
//                                    AlertDialog.Builder builder;
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                        builder = new AlertDialog.Builder(Signup2.this, android.R.style.Theme_Material_Dialog_Alert);
//                                    } else {
//                                        builder = new AlertDialog.Builder(Signup2.this);
//                                    }
//
//                                    builder.setTitle("Mobile number check..")
//                                            .setMessage("This mobile number is already registered with us. Forgot password? Try to reset the password.")
//                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    //
//                                                }
//                                            })
//                                            .setIcon(android.R.drawable.ic_dialog_alert)
//                                            .show();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });

                }
            }
        });



        // add check if this activity has come from reset password or from sign up
        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = etPhone.getText().toString();
                String val = "";
                Random r = new Random();
                int numbers = 100000 + (int)(r.nextFloat() * 899900);
                val += String.valueOf(numbers);
                String message = val; //"Verification message.";
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(number, null, message, null, null);
                //message is sent
                tvSent.setTextColor(Color.parseColor("#00FF00"));


                SmsReceiver receiver = new SmsReceiver();
                IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
                registerReceiver(receiver, filter);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    TelephonyManager tMgr = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
                    String mPhoneNumber = tMgr.getLine1Number(); // work needs to be done!
                    etPhone.setText(mPhoneNumber);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    // return to the previous activity
                    finish();
                    //Toast.makeText(Signup2.this, "Permission denied to manage your contacts and messages", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // inner class for receiver
    class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SmsMessage msg;
            //message is received
            tvReceived.setTextColor(Color.parseColor("#00FF00"));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                msg = msgs[0];
            } else {
                Object pdus[] = (Object[]) intent.getExtras().get("pdus");
                msg = SmsMessage.createFromPdu((byte[]) pdus[0]);
            }

            String number = msg.getOriginatingAddress();
            String message = msg.getMessageBody();

            if(PhoneNumberUtils.compare(number, etPhone.getText().toString()))
            {
                //Toast.makeText(Signup2.this, "SUCCESS!", Toast.LENGTH_SHORT).show();
                tvVerified.setTextColor(Color.parseColor("#00FF00"));
                // Save the data for verified
                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SharedPreferencesVariables.MOB_VERIFIED, true);
                editor.apply();
                btVerify.setEnabled(false);
                btVerify.setText("Verified!");
                btVerify.setBackgroundColor(0xafcbc5); // hint color
                isVerified = true; // this is mandatory

            }

            else
            {
                //Toast.makeText(Signup2.this, "FAILED!", Toast.LENGTH_SHORT).show();
                tvVerified.setText("Verification FAILED!");
                tvVerified.setTextColor(Color.parseColor("#FF0000"));
            }
        }
    }

}
