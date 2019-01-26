package com.helloworld.www.helloworld;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by himanshu on 10/8/18.
 */

public class Contact_QR_Code extends AppCompatActivity {
    ImageView imgView;
    public static Bitmap bitmap;
    public static int white = 0xFFFFFFFF;
    public static int black = 0xFF000000;
    public final static int WIDTH=500;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_qr_code);

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        imgView = (ImageView)findViewById(R.id.idContactQRImage);


        SharedPreferences sharedPreferences1 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);

        try {
            bitmap =  encodeAsBitmap("hello:"+sharedPreferences1.getString(SharedPreferencesVariables.LOGGED_IN_USER, "")+":hello");
        } catch (WriterException e) {
            e.printStackTrace();
        }
        imgView.setImageBitmap(bitmap);



    }
    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        Bitmap bitmap=null;
        try
        {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);

            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? black:white;
                }
            }
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        } catch (Exception iae) {
            iae.printStackTrace();
            return null;
        }
        return bitmap;
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
                Intent profile_intent = new Intent(Contact_QR_Code.this, AppUsageStatisticsActivity.class);
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
                    builder = new AlertDialog.Builder(Contact_QR_Code.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(Contact_QR_Code.this);
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

            default:
                Toast.makeText(Contact_QR_Code.this, "Oops... Something went wrong :(", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}
