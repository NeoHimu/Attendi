package com.helloworld.www.helloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.helloworld.www.helloworld.Model.RoomModel;
import com.helloworld.www.helloworld.Model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by himanshu on 25/7/18.
 */

public class Hosted extends Fragment {
    private Button btnNewRoom;
    public static ListView listView;
    public static ArrayList<String> list;
    public static ArrayList<String> listUid;
    public static ArrayList<String> listTimestamp;
    public static ArrayAdapter<String> arrayAdapter;
    TextView tvNoRoomsYet;
    public static DatabaseReference dbref;


    public Hosted()
    {
        //public constructor is required in fragment
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hosted, container,false);
        listView = (ListView)view.findViewById(R.id.idListViewHosted);
        tvNoRoomsYet = (TextView)view.findViewById(R.id.idRoomsYouCreated);
        // add all rooms from shared preferences
//        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, Context.MODE_PRIVATE);
//        String roomName = sharedPreferences.getString(SharedPreferencesVariables.NEW_ROOM, "default");
//        TinyDB tinyDB = new TinyDB(getActivity());
//        tinyDB.putObject(SharedPreferencesVariables.NEW_ROOM, new RoomModel(new User(), "", "", ""));
//        newRoom = tinyDB.getObject(SharedPreferencesVariables.NEW_ROOM, RoomModel.class);
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, Context.MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString(SharedPreferencesVariables.LOGGED_IN_USER, "");

        list = new ArrayList<String>();
//        if(list.size()==0)
//        {
//            tvNoRoomsYet.setText("No room. Create one.");
//        }
//        else
//        {
            tvNoRoomsYet.setText("Rooms you created : ");
        //}
        listUid = new ArrayList<String>();
        listTimestamp = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
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

        dbref = FirebaseDatabase.getInstance().getReference();

        dbref.child(FirebaseVariables.ROOMS).child(loggedInUser).addListenerForSingleValueEvent(
                new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String,Object> rooms = (Map<String,Object>) dataSnapshot.getValue();
                    if(rooms==null) // when no room has been created
                        return;
                    //iterate through each user, ignoring their UID
                    for (Map.Entry<String, Object> entry : rooms.entrySet()){

                        //Get user map
                        Map singleRoom = (Map) entry.getValue();
                        listUid.add(0,singleRoom.get("createdBy").toString());
                        listTimestamp.add(0,singleRoom.get("createdAtTime").toString());
                        list.add(0, singleRoom.get("roomName").toString());
                        arrayAdapter.notifyDataSetChanged(); // IMPORTANT to add this line
                        //Toast.makeText(getContext(), list.size()+"", Toast.LENGTH_SHORT).show();
                        //Get phone field and append to list
                        //list.add((Long) singleUser.get("phone"));
                        //Log.d("tmz",""+singleRoom.get("market_name"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
        });


        btnNewRoom = (Button)view.findViewById(R.id.idBtnNewRoomHosted);

        btnNewRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NewRoom.class);
                startActivityForResult(i, 123);
            }
        });

        // go to room activity to start taking attendance on clicking an item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                list.get(position);
                Intent i = new Intent(getContext(), PrepareQRCode.class);
                i.putExtra("uidHosted2PrepareQRCode", listUid.get(position)); // creator
                i.putExtra("timestampHosted2PrepareQRCode", listTimestamp.get(position)); // room_id
                startActivity(i);
            }
        });


        // remove an item on long press
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                // TODO Auto-generated method stub
                final int position = pos;
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getActivity());
                }

                builder.setTitle("Delete")
                        .setMessage("Do you want to remove it from here?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // removal in this order is recommended otherwise it'll cause inconsistency among lists
                                // remove from the UI
                                list.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                                // remove from the database
                                dbref.child(FirebaseVariables.ROOMS).child(listUid.get(position)).child(listTimestamp.get(position)).removeValue();
                                //remove from other lists
                                listUid.remove(position);
                                listTimestamp.remove(position);
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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==123 && resultCode == Activity.RESULT_OK && data!=null) {
            // TODO Extract the data returned from the child Activity.
            String roomName = data.getStringExtra("some_key");
            String uid = data.getStringExtra("uid_key");
            String timestamp = data.getStringExtra("timestamp_key");
            tvNoRoomsYet.setText("Rooms you created : ");
            list.add(0,roomName);
            listUid.add(0, uid);
            listTimestamp.add(0, timestamp);
            arrayAdapter.notifyDataSetChanged();
        }

    }


    public  void onResume()
    {
        super.onResume();

//        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SharedPreferencesVariables.SHARED_PREFS, Context.MODE_PRIVATE);
//        String roomName = sharedPreferences.getString(SharedPreferencesVariables.ROOM, "default");
//        list.add(0,roomName);
//        arrayAdapter.notifyDataSetChanged();
    }

}