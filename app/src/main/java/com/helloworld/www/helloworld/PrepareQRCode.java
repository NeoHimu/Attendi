package com.helloworld.www.helloworld;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.helloworld.www.helloworld.Model.AttendanceTimes;
import com.helloworld.www.helloworld.Model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by himanshu on 26/7/18.
 */

public class PrepareQRCode extends AppCompatActivity {
    private static final String TAG = "Hosted";
    public static Bitmap bitmap;
    private Button btnGenerate, btnShare, btnStart, btnEnd;
    public static String uidForEachRoomCreated;
    String uidForEachTimeAttendanceIsTakenInRoom = "";
    public static String startTime = "";
    public static String endTime = "";
    public static int control=1;
    String logged_in_user="";
    SharedPreferences sharedPreferences;
    ImageView imgView;
    File myDir=null;
    File file=null;

    public static ListView listView;
    public static ArrayList<String> list;
    public static ArrayAdapter<String> arrayAdapter;

    public static int white = 0xFFFFFFFF;
    public static int black = 0xFF000000;
    public final static int WIDTH=500;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prepare_qr_code);

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        listView = (ListView)findViewById(R.id.idLvTimingsOfAttendance);
        View v = ((LayoutInflater)PrepareQRCode.this.getSystemService(PrepareQRCode.this.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_view_header_qr_code, null, false);
        listView.addHeaderView(v);
        listView.setSelectionAfterHeaderView();


        btnGenerate = (Button)findViewById(R.id.idGenerateQRCode);
        btnShare = (Button)findViewById(R.id.idShareQRCode);
        btnStart = (Button)findViewById(R.id.idBtnStartAttendance);
        btnEnd = (Button)findViewById(R.id.idBtnEndAttendance);
        imgView = (ImageView)findViewById(R.id.idQRImage);

        list = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(
                PrepareQRCode.this,
                android.R.layout.simple_expandable_list_item_1,
                android.R.id.text1,
                list
        ){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(getContext().getResources().getColor(R.color.colorText));
                return textView;
            }
        };

        listView.setAdapter(arrayAdapter);

        // This is for giving a sense of order in which buttons should be pressed
        sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);


        // set the image asking to generate a QR code in the imageview
        Drawable res = ResourcesCompat.getDrawable(getResources(), R.drawable.qrcode, null);
        imgView.setImageDrawable(res);


        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(control==2)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(PrepareQRCode.this);
                    }
                    builder.setTitle("Alert")
                            .setMessage("Please press \"START\" button")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else if(control==3)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(PrepareQRCode.this);
                    }
                    builder.setTitle("Alert")
                            .setMessage("Please press \"END\" button")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else if(control==1)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(PrepareQRCode.this);
                    }
                    builder.setTitle("Generating a new QR code")
                            .setMessage("Do you really want a new QR code?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try
                                    {
                                        SharedPreferences sharedPreferences1 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
//                                        SharedPreferences.Editor editor = sharedPreferences1.edit();
//                                        // so that next time START should be pressed
//                                        editor.putInt(SharedPreferencesVariables.CONTROL, 2);
//                                        editor.commit();
                                        control = 2;
                                        logged_in_user = sharedPreferences1.getString(SharedPreferencesVariables.LOGGED_IN_USER, "");


                                        uidForEachRoomCreated = PrepareQRCode.this.getIntent().getExtras().getString("timestampHosted2PrepareQRCode");
                                        uidForEachTimeAttendanceIsTakenInRoom = new SimpleDateFormat("ddMMyyyyHHmmss").format(Calendar.getInstance().getTime());

                                        String strToBeEncoded = logged_in_user + "/" + uidForEachRoomCreated + "/"+uidForEachTimeAttendanceIsTakenInRoom;
                                        bitmap =  encodeAsBitmap(strToBeEncoded);
                                        imgView.setImageBitmap(bitmap);

                                        // writing into database
                                        // teacher is also an attendee
                                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
                                        AttendanceTimes att = new AttendanceTimes("NA","NA","NA","NA","NA");
                                        dbref.child(FirebaseVariables.ATT).child(logged_in_user).child(uidForEachRoomCreated).child(uidForEachTimeAttendanceIsTakenInRoom).child(logged_in_user).setValue(att);

                                        // add this room's today's attendance now only
                                        list.add(0, uidForEachTimeAttendanceIsTakenInRoom);
                                        arrayAdapter.notifyDataSetChanged();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Toast toast = Toast.makeText(PrepareQRCode.this, "Stay on this page till you finish attendance!", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(control==1)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(PrepareQRCode.this);
                    }
                    builder.setTitle("Alert")
                            .setMessage("Please press \"GENERATE\" button")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else
                    requestPermission();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(control==1)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(PrepareQRCode.this);
                    }
                    builder.setTitle("Alert")
                            .setMessage("Please press \"GENERATE\" button")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else if(control==3)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(PrepareQRCode.this);
                    }
                    builder.setTitle("Alert")
                            .setMessage("Please press \"END\" button")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(PrepareQRCode.this);
                    }
                    builder.setTitle("Starting the attendance.")
                            .setMessage("Do you really want to start taking the attendance?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startTime = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                                    Toast.makeText(PrepareQRCode.this, startTime, Toast.LENGTH_SHORT).show();

//                                SharedPreferences sharedPreferences2 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
//                                SharedPreferences.Editor editor = sharedPreferences2.edit();
//                                editor.putInt(SharedPreferencesVariables.CONTROL, 3);
//                                editor.commit();
                                    control = 3;
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(control==2)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(PrepareQRCode.this);
                    }
                    builder.setTitle("Alert")
                            .setMessage("Please press \"START\" button")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else if(control==1)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(PrepareQRCode.this);
                    }
                    builder.setTitle("Alert")
                            .setMessage("Please press \"GENERATE\" button")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(PrepareQRCode.this);
                    }
                    builder.setTitle("Starting the attendance.")
                            .setMessage("Do you really want to finish taking the attendance?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    endTime = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                                    Toast.makeText(PrepareQRCode.this, endTime, Toast.LENGTH_SHORT).show();
//                                    SharedPreferences sharedPreferences3 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = sharedPreferences3.edit();
//                                    editor.putInt(SharedPreferencesVariables.CONTROL, 1);
//                                    editor.commit();
                                    control = 1;
                                    // write into Firebase Database
                                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
                                    AttendanceTimes att = new AttendanceTimes("NA",startTime,endTime,"NA","NA");
                                    dbref.child(FirebaseVariables.ATT).child(logged_in_user).child(uidForEachRoomCreated).child(uidForEachTimeAttendanceIsTakenInRoom).child(logged_in_user).setValue(att);

                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

            }
        });

        //list.add(0,"Day 1");

        SharedPreferences sharedPreferences1 = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
        final String logged_in_user = sharedPreferences1.getString(SharedPreferencesVariables.LOGGED_IN_USER, "");
        uidForEachRoomCreated = PrepareQRCode.this.getIntent().getExtras().getString("timestampHosted2PrepareQRCode");
        // all attendances for a room
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        dbref.child(FirebaseVariables.ATT).child(logged_in_user).child(uidForEachRoomCreated).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //iterate through each user, ignoring their UID
                        for (DataSnapshot entry : dataSnapshot.getChildren()){
                            // if inserting in multiple lists then remove from all these lists on long press
                            String singleRoom = entry.getKey();
                            list.add(0, singleRoom);
                            arrayAdapter.notifyDataSetChanged(); // IMPORTANT to add this line
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        // go to new_room activity on clicking an item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position-1;  // -1 is required because index is incremented by 1 when
                // header is added
                Log.i("item click : ",position+"");
                Intent i = new Intent(PrepareQRCode.this, StudentsList.class);
                i.putExtra("uid", logged_in_user);
                i.putExtra("roomId",uidForEachRoomCreated);
                i.putExtra("attendanceId", list.get(position));
                //
                startActivity(i);
            }
        });


        // remove an item on long press
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                final int position = pos-1; // This is important because index is incremented by 1 because of header added
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(PrepareQRCode.this);
                }

                builder.setTitle("Delete")
                        .setMessage("Do you want to remove it from here?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               // Toast.makeText(PrepareQRCode.this, position+"", Toast.LENGTH_SHORT).show();
                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
                                dbref.child(FirebaseVariables.ATT).child(logged_in_user).child(uidForEachRoomCreated).child(list.get(position)).removeValue();
                                Log.i("item long click : ",position+"");
                                list.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //share the image
            shareImage();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            //share the image
            shareImage();
        }
    }

    public void shareImage()
    {
        // Create the file directory
        myDir = new File(Environment.getExternalStorageDirectory() + "/req_images");
        myDir.mkdirs();
        String fname = "QRCodeImage.PNG"; // now this is dynamic

        // create the file in the directory
        file = new File(myDir, fname);
        if(file.exists())
        {
            file.delete();
            file = new File(myDir, fname);
        }

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "himanshus096@gmail.com");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "AttendanceMarker");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, " ");

   /*
    * The important part where you create the file and save it
    */
        emailIntent.setType("image/*"); // accept any image

        try {
            boolean fileCreated = file.createNewFile();
            if (fileCreated) {
                // write the bitmap to that file
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            }
        } catch (IOException ex) {
            Log.d("SAVE FAILED", "could not save file");
        }

        // then attach the file to the intent
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        startActivity(Intent.createChooser(emailIntent, "Share the QR code using:"));
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
                Intent profile_intent = new Intent(PrepareQRCode.this, AppUsageStatisticsActivity.class);
                startActivity(profile_intent);
                break;

            case R.id.idLogout:
                //Toast.makeText(Home.this, "Logout is clicked", Toast.LENGTH_SHORT).show();
//                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(SharedPreferencesVariables.LOGGED_IN_USER, "No one");
//                editor.putBoolean(SharedPreferencesVariables.IS_LOGGEDIN, false);
//                editor.apply();
//                Intent logout_intent = new Intent(PrepareQRCode.this, Login.class);
//                logout_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(logout_intent);
//                finish();
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(PrepareQRCode.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(PrepareQRCode.this);
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
            Intent i = new Intent(PrepareQRCode.this, Contact_QR_Code.class);
            startActivity(i);
            break;

            default:
                Toast.makeText(PrepareQRCode.this, "Oops... Something went wrong :(", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
