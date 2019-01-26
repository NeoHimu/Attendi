package com.helloworld.www.helloworld;

/**
 * Created by himanshu on 25/7/18.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class HomeTabbedActivity extends AppCompatActivity {

    private static final String TAG = "HomeTabbedActivty";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    boolean isFirstRun=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting.");
        setContentView(R.layout.home_tab_layout);
        //toolbar is added
        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
        isFirstRun = sharedPreferences.getBoolean(SharedPreferencesVariables.IS_FIRST_RUN, true);
        if(isFirstRun==true)
        {
            Intent i = new Intent(HomeTabbedActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }


        //mSectionsPageAdapter = new SectionsPageAdapter(this.getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new Attended(), "Attended");
        adapter.addFragment(new Hosted(), "Hosted");
        viewPager.setAdapter(adapter);
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
                Intent profile_intent = new Intent(HomeTabbedActivity.this, AppUsageStatisticsActivity.class);
                startActivity(profile_intent);
                break;

            case R.id.idLogout:
                //Toast.makeText(Home.this, "Logout is clicked", Toast.LENGTH_SHORT).show();
//                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(SharedPreferencesVariables.LOGGED_IN_USER, "No one");
//                editor.putBoolean(SharedPreferencesVariables.IS_LOGGEDIN, false);
//                editor.apply();
//                Intent logout_intent = new Intent(HomeTabbedActivity.this, Login.class);
//                logout_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(logout_intent);
//                finish();
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(HomeTabbedActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(HomeTabbedActivity.this);
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
                Intent i = new Intent(HomeTabbedActivity.this, Contact_QR_Code.class);
                startActivity(i);
                break;

            default:
                Toast.makeText(HomeTabbedActivity.this, "Oops... Something went wrong :(", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}
