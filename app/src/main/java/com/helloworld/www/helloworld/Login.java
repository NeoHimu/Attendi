package com.helloworld.www.helloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.UsageStatsManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helloworld.www.helloworld.Model.User;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by himanshu on 23/7/18.
 */

public class Login extends AppCompatActivity {
    EditText etMobNo, etPassword;
    TextView tvWarningMobNo, tvWarningPwd, tvForgotPassword, tvSignup;
    Button btLogin;
    boolean isAppUsagePermGranted;

    FirebaseDatabase database;
    DatabaseReference dbref;
    User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //toolbar is added
        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        // Firebase
        database = FirebaseDatabase.getInstance();
        dbref = database.getReference();

        //dbref.child(FirebaseVariables.ALLOWED_APPS).child("com:miui:home").setValue("com.miui.home");

        etMobNo = (EditText)findViewById(R.id.idMobNoLoginPage);
        etPassword = (EditText)findViewById(R.id.idPwdLoginPage);
        btLogin = (Button)findViewById(R.id.idBtLogin);
        tvWarningMobNo = (TextView)findViewById(R.id.idWarningMobNoLoginPage);
        tvWarningPwd = (TextView)findViewById(R.id.idWarningPwdLoginPage);
        tvForgotPassword = (TextView)findViewById(R.id.idForgotPasswordLoginPage);
        tvSignup = (TextView)findViewById(R.id.idSignUpLoginPage);

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, MainActivity.class);
                // Make the verification of mobile number flag as false i.e. mobile no verification is required now.
                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SharedPreferencesVariables.MOB_VERIFIED, false);
                editor.apply();
                startActivity(i);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Login.this, "Forgot pwd is clicked!", Toast.LENGTH_SHORT).show();
                // Make the verification of mobile number flag as false i.e. mobile no verification is required now.
                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SharedPreferencesVariables.MOB_VERIFIED, false);
                //set shared preference as true i.e. Signup2 is being called after onClick of forgot password
                editor.putBoolean(SharedPreferencesVariables.IS_FORGOT_PWD, true);
                editor.apply();

                // Signup3.java has code for password set/reset
                Intent i = new Intent(Login.this, Signup2.class);
                startActivity(i);

            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etMobNo.getText().toString()))
                {
                    tvWarningMobNo.setText("Please enter mobile no!");
                }
                else if(TextUtils.isEmpty(etPassword.getText().toString()))
                {
                    tvWarningPwd.setText("Please enter password!");
                }
                else
                {
                    // check if database has any registered user with this mobile number
                    // and password!
                       final String mobno = etMobNo.getText().toString();
                            final String pwd = etPassword.getText().toString();
                            //here mobile number is uid
                            DatabaseReference uid = dbref.child(FirebaseVariables.USERS).child(mobno);
                            user = new User();
                            uid.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // "phoneNo" and "password" are keys in the Firebase database, it's getting it's value
                                    Object phone_number = dataSnapshot.child("phoneNo").getValue();
                                    Object password = dataSnapshot.child("password").getValue();
                                    Object fName= dataSnapshot.child("fName").getValue();
                                    Object lName = dataSnapshot.child("lName").getValue();
                                    Object email = dataSnapshot.child("emailId").getValue();
                                    Object rollNo = dataSnapshot.child("rollNo").getValue();
                                    if(fName!=null)
                                        user.setfName(fName.toString());
                                    else
                                        user.setfName("");

                                    if(lName!=null)
                                        user.setlName(lName.toString());
                                    else
                                        user.setlName("");

                                    if(email!=null)
                                        user.setEmailId(email.toString());
                                    else
                                        user.setEmailId("");
                                    if(phone_number!=null)
                                        user.setPhoneNo(phone_number.toString());
                                    else
                                        user.setPhoneNo("");

                                    if(rollNo!=null)
                                        user.setRollNo(rollNo.toString());
                                    else
                                        user.setRollNo("");

                                    TinyDB tinyDB = new TinyDB(Login.this);
                                    tinyDB.putObject(SharedPreferencesVariables.LOGGEDIN_USER, user);

                                    if(phone_number==null || !phone_number.toString().equals(mobno))
                                    {
                                        tvWarningMobNo.setText("Wrong mobile number!");
                                    }
                                    else if(password==null || !password.toString().equals(pwd)) // wrong password
                                    {
                                        tvWarningPwd.setText("Wrong password!");
                                    }
                                    else if(phone_number.toString().equals(mobno) && password.toString().equals(pwd))
                                    {
                                        //save in shared preferencd the logged in user.
                                        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(SharedPreferencesVariables.LOGGED_IN_USER, mobno);
                                        editor.putBoolean(SharedPreferencesVariables.IS_LOGGEDIN, true);
                                        editor.apply();

                                        SharedPreferences sharedPreferences1 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
                                        isAppUsagePermGranted = sharedPreferences1.getBoolean(SharedPreferencesVariables.APP_USAGE_PERM_GRANTED, false);

                                          if(isAppUsagePermGranted)
                                          {
                                              Intent i = new Intent(Login.this, HomeTabbedActivity.class);
                                              i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                              startActivity(i);
                                          }
                                          else
                                          {
                                              Intent i = new Intent(Login.this, Home.class);
                                              i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                              startActivity(i);
                                          }

                                        finish();
                                    }
                                }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(Login.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(Login.this);
                            }

                            builder.setTitle("Database Read Error!")
                                    .setMessage("There is some problem with the database. Please try after some time.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
                }
            }
        });

        etMobNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvWarningMobNo.setText("");
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvWarningPwd.setText("");
            }
        });
    }

    boolean hasAppUsagePermission()
    {
        try {
            PackageManager packageManager = Login.this.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(Login.this.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) Login.this.getSystemService(Login.this.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}

