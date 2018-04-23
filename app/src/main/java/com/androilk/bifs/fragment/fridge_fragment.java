package com.androilk.bifs.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.*;
import java.util.Date;
import java.util.TimeZone;

public class fridge_fragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;
    List<String> arrayList;
    List<String> ids;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Sensör Listesi");
        View v = inflater.inflate(R.layout.fridge_layout, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arrayList = new ArrayList<>();
        ids = new ArrayList<>();
        listView =  getView().findViewById(R.id.sensorList);
        registerForContextMenu(listView);
        user = mAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.fridge_layout, R.id.tvlist, this.arrayList);
        DatabaseReference sen = mDatabase.child("sensors");
        sen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(user.getEmail().equals(String.valueOf(snapshot.child("user_email").getValue()))) {
                        ids.add(snapshot.child("id").getValue().toString());
                        arrayAdapter.add(snapshot.child("name").getValue().toString());
                    }
                }
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                        // TODO Auto-generated method stub
                        Log.i("ids", ids.get(position));
                        Bundle bundle=new Bundle();
                        bundle.putString("id", ids.get(position));
                        SensorDescription sensorDescription = new SensorDescription();
                        sensorDescription.setArguments(bundle);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, sensorDescription);
                        transaction.commit();

                    }
                });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {

                        return false;
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Woops " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        listView.setAdapter(arrayAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.recipeList) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        return super.onContextItemSelected(item);}

}


