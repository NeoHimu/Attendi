package com.helloworld.www.helloworld;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by himanshu on 23/7/18.
 */

public class Home extends AppCompatActivity{

    Button btAppUsagePermission, btNext;
    boolean isAppUsagePermGranted=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        btAppUsagePermission = (Button)findViewById(R.id.idAppUsagePermission);
        btNext = (Button)findViewById(R.id.idNextFromHome);

        SharedPreferences sharedPreferences2 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
        isAppUsagePermGranted = sharedPreferences2.getBoolean(SharedPreferencesVariables.APP_USAGE_PERM_GRANTED, false);

        if(isAppUsagePermGranted)
        {
            btAppUsagePermission.setEnabled(false);
            btAppUsagePermission.setBackgroundColor(0xffffff);
        }
        else
        {
            btAppUsagePermission.setEnabled(true);
        }

        btAppUsagePermission.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isPremissionGranted = hasAppUsagePermission();
                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SharedPreferencesVariables.APP_USAGE_PERM_GRANTED, isPremissionGranted);
                editor.apply();

                if(isPremissionGranted)
                {
                    editor.putBoolean(SharedPreferencesVariables.IS_FIRST_RUN, false);
                    editor.apply();
                    Intent i = new Intent(Home.this, HomeTabbedActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(Home.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(Home.this);
                    }
                    builder.setTitle("App Usage Permission required!")
                            .setMessage("Please grant App Usage Permission for this app.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

            }
        });
    }


    boolean hasAppUsagePermission()
    {
        try {
            PackageManager packageManager = Home.this.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(Home.this.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) Home.this.getSystemService(Home.this.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
