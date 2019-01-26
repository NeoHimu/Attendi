package com.helloworld.www.helloworld;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Calendar myCalendar;
    //EditText etBirthday;
    //DatePickerDialog.OnDateSetListener date;
    Button bt1;
    EditText etFName, etLName, etRollNo, etMailId;
    TextView tvNames, tvRoll, tvMail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar is added
        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        etFName = (EditText)findViewById(R.id.idFirstName);
        etLName = (EditText)findViewById(R.id.idSurname);
        etRollNo = (EditText)findViewById(R.id.idRollNo);
        etMailId = (EditText)findViewById(R.id.idMailId);
        bt1 = (Button)findViewById(R.id.idBtNext);
        tvNames = (TextView)findViewById(R.id.idWarningNames);
        tvRoll = (TextView)findViewById(R.id.idWarningRollNo);
        tvMail = (TextView)findViewById(R.id.idWarningMailId);

        etFName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvNames.setText("");
            }
        });

        etLName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvNames.setText("");
            }
        });


        etRollNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvRoll.setText("");
            }
        });

        etMailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvMail.setText("");
            }
        });

/*
        myCalendar= Calendar.getInstance();
        etBirthday= (EditText) findViewById(R.id.idEtBirthday);

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        etBirthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

*/
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName= etFName.getText().toString();
                String lName = etLName.getText().toString();
                String rollNo = etRollNo.getText().toString();
                String emailId = etMailId.getText().toString();

                if(TextUtils.isEmpty(firstName)) {
                    tvNames.setText("Please fill the first name!");
                }
                else if(TextUtils.isEmpty(lName)) {
                    tvNames.setText("Please fill the surname!");
                }
                else if(TextUtils.isEmpty(rollNo))
                {
                    tvRoll.setText("Please enter your roll no!");
                }
                else if(TextUtils.isEmpty(emailId))
                {
                    tvMail.setText("Please enter your mail id!");
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(emailId).matches())
                {
                    tvMail.setText("Wrong mail id!");
                }
                else
                {
                    saveData();
                    Intent i = new Intent(MainActivity.this, Signup2.class);
                    startActivity(i);
                    finish();
                }

            }
        });


    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SharedPreferencesVariables.FIRSTNAME, etFName.getText().toString());
        editor.putString(SharedPreferencesVariables.LASTNAME, etLName.getText().toString());
        editor.putString(SharedPreferencesVariables.ROLLNO, etRollNo.getText().toString());
        editor.putString(SharedPreferencesVariables.MAILID, etMailId.getText().toString());
        editor.apply();
    }

    void loadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
        String fName = sharedPreferences.getString(SharedPreferencesVariables.FIRSTNAME, "");
        String lName = sharedPreferences.getString(SharedPreferencesVariables.LASTNAME, "");
        String rollNo = sharedPreferences.getString(SharedPreferencesVariables.ROLLNO, "");
        String mailId = sharedPreferences.getString(SharedPreferencesVariables.MAILID, "");
    }

    public void updateView()
    {
        // update ui widget with the stored value after loading it.
    }


/*
    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etBirthday.setText(sdf.format(myCalendar.getTime()));
    }
    */
}
