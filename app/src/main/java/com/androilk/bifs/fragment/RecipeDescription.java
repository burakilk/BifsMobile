package com.androilk.bifs.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.List;

/**
 * Created by ilkim celik on 27.02.2018.
 */

public class RecipeDescription extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;
    List<String> arrayList;
    List<String> ids;
    ListView listView;
    private TextView etRecipe;
    private TextView etDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recipe_description, container, false);


        return v;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etRecipe = (TextView) getView().findViewById(R.id.etRecipeTitle);
        etDescription = (TextView) getView().findViewById(R.id.etDescriptionContent);
        Bundle bundle = getArguments();
        user = mAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userpost = mDatabase.child("user-recipe-list");
        DatabaseReference post = userpost.child(user.getUid());
        DatabaseReference value = post.child(bundle.getString("id"));
        value.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                etRecipe.setText(dataSnapshot.child("title").getValue().toString());
                etDescription.setText(dataSnapshot.child("body").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }}