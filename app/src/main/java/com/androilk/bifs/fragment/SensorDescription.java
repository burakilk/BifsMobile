package com.androilk.bifs.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.androilk.bifs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ilkim celik on 27.02.2018.
 */

public class SensorDescription extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser user;
    AutoCompleteTextView txtSensorContent;
    DatabaseReference mDatabase;
    FirebaseDatabase db;
    DatabaseReference productRef, tempRef;
    List<String> arrayList;
    List<String> ids;
    ArrayList products;
    String productsArray[];
    ListView listView;
    Map<String, String> map;
    AutoCompleteTextView Liste;
    private TextView txtSensorName;
    //private TextView txtSensorContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        map = new HashMap<String, String>();
        View v = inflater.inflate(R.layout.sensor_decription, container, false);
        Liste = v.findViewById(R.id.txtSensorContent);

        return v;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtSensorName = (TextView) getView().findViewById(R.id.txtSensorName);
        // txtSensorContent = (TextView) getView().findViewById(R.id.txtSensorContent);
        txtSensorContent = view.findViewById(R.id.txtSensorContent);
        Bundle bundle = getArguments();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference sensors = mDatabase.child("sensors");
        DatabaseReference id = sensors.child(bundle.getString("id"));
        id.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtSensorName.setText(dataSnapshot.child("name").getValue().toString());
                txtSensorContent.setText(dataSnapshot.child("urun_adi").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        products = new ArrayList();
        db = FirebaseDatabase.getInstance();
        DatabaseReference productRef = mDatabase.child("products");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snaps : dataSnapshot.getChildren()) {

                    if (snaps.child("title").getValue() != null) {

                        map.put(snaps.getKey(), snaps.child("title").getValue().toString());
                        Log.w("querysd", String.valueOf(map.size()));
                        products.add(snaps.child("title").getValue().toString());


                    }
                }
                int size = products.size();
                productsArray = new String[size];
                for (int i = 0; i < size; i++) {
                    productsArray[i] = products.get(i).toString();
                }
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, productsArray);
                txtSensorContent.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }}