package com.helloworld.www.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helloworld.www.helloworld.Model.User;

import android.widget.Toast;

/**
 * Created by himanshu on 17/7/18.
 */

public class Signup3 extends AppCompatActivity {
    Button btSetpassword;
    EditText etPassword1, etPassword2;
    TextView tvPassDoesNotMatch;

    FirebaseDatabase database;
    DatabaseReference dbref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup3);
        //toolbar is added
        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        // Firebase
        database = FirebaseDatabase.getInstance();
        dbref = database.getReference();

        btSetpassword = (Button)findViewById(R.id.idSetPassword);
        etPassword1 = (EditText)findViewById(R.id.idPassword1);
        etPassword2 = (EditText)findViewById(R.id.idPassword2);
        tvPassDoesNotMatch = (TextView)findViewById(R.id.tvPassDoesNotMatch);

        //validate while typing if password matches or not
        etPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // check if the current typed character from password matches the same position
                String str1 = etPassword1.getText().toString();
                String str2 = etPassword2.getText().toString();
                int lenOfPassword1 = str1.length();
                int lenOfPassword2 = str2.length();

//                if(lenOfPassword1<lenOfPassword2 || lenOfPassword2>0 && (str1.toCharArray()[lenOfPassword2-1]
//                        != str2.toCharArray()[lenOfPassword2-1]))
                if(!str1.regionMatches(0, str2, 0, lenOfPassword2))
                {
                    tvPassDoesNotMatch.setTextColor(Color.parseColor("#FF0000"));
                    tvPassDoesNotMatch.setText("Password does not match!");
                }
                else{
                    tvPassDoesNotMatch.setText("");
                }
            }
        });

        btSetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(Signup3.this, )
                // make sure that password and confirm password match
                final String str1 = etPassword1.getText().toString();
                String str2 = etPassword2.getText().toString();
                if(str2.length()<2)
                {
                    tvPassDoesNotMatch.setTextColor(Color.parseColor("#FF0000"));
                    tvPassDoesNotMatch.setText("Minimum 2 characters password!");
                }
                else if(str1.equals(str2))
                {
                    //Toast.makeText(Signup3.this, "Password Match!", Toast.LENGTH_SHORT).show();
                    // save the verified mobile number in the shared preference
                    final SharedPreferences sharedPreferences1 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences1.edit();

                    // save data from MainActivity, Signup2 and Signup3 in the Firebase database
                    String fName = sharedPreferences1.getString(SharedPreferencesVariables.FIRSTNAME, "");
                    String lName = sharedPreferences1.getString(SharedPreferencesVariables.LASTNAME, "");
                    String roll = sharedPreferences1.getString(SharedPreferencesVariables.ROLLNO, "");
                    String mailid = sharedPreferences1.getString(SharedPreferencesVariables.MAILID,"");
                    final String mobno = sharedPreferences1.getString(SharedPreferencesVariables.MOB_NO, "");

                    final User user = new User(fName, lName, roll, mailid, mobno, str1,false);

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // save new user in the database
                            boolean hasComeFromForgotPwd = sharedPreferences1.getBoolean(SharedPreferencesVariables.IS_FORGOT_PWD, false);
                            if(hasComeFromForgotPwd==false)
                            {
                                dbref.child(FirebaseVariables.USERS).child(user.getPhoneNo()).setValue(user);
                            }
                            else
                            {
                                // str1 is updated password here
                                // password is a class variable of User class in the model folder.
                                editor.putBoolean(SharedPreferencesVariables.IS_FORGOT_PWD, false);// so that user can afford more than one time password forgetting
                                dbref.child(FirebaseVariables.USERS).child(user.getPhoneNo()).child("password").setValue(str1);
                            }
                            // on successful write to the database, mark the user as logged in
                            editor.putString(SharedPreferencesVariables.LOGGED_IN_USER, user.getPhoneNo());
                            editor.putBoolean(SharedPreferencesVariables.IS_LOGGEDIN, true);
                            editor.apply();
                            //Toast.makeText(Signup3.this, "Database write occuring!", Toast.LENGTH_SHORT).show();
                            // only after successful write, go to the next activity

                            SharedPreferences sharedPreferences1 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
                            boolean isAppUsagePermGranted = sharedPreferences1.getBoolean(SharedPreferencesVariables.APP_USAGE_PERM_GRANTED, false);

                            if(isAppUsagePermGranted)
                            {
                                Intent i = new Intent(Signup3.this, HomeTabbedActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                            else
                            {
                                Intent i = new Intent(Signup3.this, Home.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //Toast.makeText(Signup3.this, "Database writing cancelled!", Toast.LENGTH_SHORT).show();
                            Toast.makeText(Signup3.this, "Database write error! Please try again later!", Toast.LENGTH_SHORT).show();
                            editor.putBoolean(SharedPreferencesVariables.IS_LOGGEDIN, false);
                            editor.apply();
                        }
                    });
                }
                else
                {
                    tvPassDoesNotMatch.setText("Passwords don't match!");
                    //Toast.makeText(Signup3.this, "Password don't match!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
