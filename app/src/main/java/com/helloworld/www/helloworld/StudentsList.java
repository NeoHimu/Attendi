package com.helloworld.www.helloworld;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;

/**
 * Created by himanshu on 27/7/18.
 */

public class StudentsList extends AppCompatActivity {

    public static ListView listView;
    public static ArrayList<String> list;
    public static ArrayAdapter<String> arrayAdapter;
    ImageView imgView;
    public static int white = 0xFFFFFFFF;
    public static int black = 0xFF000000;
    public final static int WIDTH=500;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.students_list);

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        imgView = (ImageView)findViewById(R.id.imgViewQRCodeForEachRoom);

        listView = (ListView)findViewById(R.id.idLvStudentsAttendance);

        list = new ArrayList<String>();
        //list.add(0,"Student 1");

        final String uid = StudentsList.this.getIntent().getExtras().getString("uid");
        final String roomId = StudentsList.this.getIntent().getExtras().getString("roomId");
        final String attendanceId = StudentsList.this.getIntent().getExtras().getString("attendanceId");
        // generate a qr code and set it
        Bitmap b = null;
        try {
            b = encodeAsBitmap(uid+"/"+roomId+"/"+attendanceId);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        imgView.setImageBitmap(b);


        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        dbref.child(FirebaseVariables.ATT).child(uid).child(roomId).child(attendanceId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //iterate through each user, ignoring their UID
                        for (DataSnapshot entry : dataSnapshot.getChildren()){
                            //if(entry==null)
                            //    return;
                            //Get user map
                            String singleRoom = entry.getKey();
                            // if inserting in multiple lists then remove from all these lists on long press
                            list.add(0, singleRoom);
                            arrayAdapter.notifyDataSetChanged(); // IMPORTANT to add this line
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        arrayAdapter = new ArrayAdapter<String>(
                StudentsList.this,
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

        // go to new_room activity on clicking an item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(StudentsList.this, AttendanceDetails.class);
                i.putExtra("uid",uid);
                i.putExtra("roomId", roomId);
                i.putExtra("attendanceId", attendanceId);
                i.putExtra("studentId", list.get(position));

                startActivity(i);
            }
        });


        // remove an item on long press
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                final int position = pos;
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(StudentsList.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(StudentsList.this);
                }

                builder.setTitle("Delete")
                        .setMessage("Do you want to remove it from here?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
                                dbref.child(FirebaseVariables.ATT).child(uid).child(roomId).child(attendanceId).child(list.get(position)).removeValue();

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
                Intent profile_intent = new Intent(StudentsList.this, AppUsageStatisticsActivity.class);
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
                //Toast.makeText(Home.this, "Logout is clicked", Toast.LENGTH_SHORT).show();
//                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(SharedPreferencesVariables.LOGGED_IN_USER, "No one");
//                editor.putBoolean(SharedPreferencesVariables.IS_LOGGEDIN, false);
//                editor.apply();
//                Intent logout_intent = new Intent(StudentsList.this, Login.class);
//                logout_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(logout_intent);
//                finish();
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(StudentsList.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(StudentsList.this);
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
                Intent i = new Intent(StudentsList.this, Contact_QR_Code.class);
                startActivity(i);
                break;
            default:
                Toast.makeText(StudentsList.this, "Oops... Something went wrong :(", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
