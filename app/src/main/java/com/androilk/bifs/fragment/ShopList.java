package com.androilk.bifs.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.androilk.bifs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FBI on 4.02.2018.
 */

public class ShopList extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;
    List<String> arrayList;
    List<String> ids;
    ListView listView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Alışveriş Sepeti Listesi");
        View v = inflater.inflate(R.layout.shoplist, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrayList = new ArrayList<>();
        ids = new ArrayList<>();
        listView = (ListView) getView().findViewById(R.id.listShop);
        registerForContextMenu(listView);
        user = mAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String myUserId = user.getUid();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, this.arrayList);
        DatabaseReference userpost = mDatabase.child("bifs-shopping-list");
        DatabaseReference post = userpost.child(user.getUid());
        post.orderByKey().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    arrayAdapter.add(snapshot.child("title").getValue().toString() + " " + snapshot.child("body").getValue().toString());
                    ids.add(snapshot.getKey());
                }
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
        if (v.getId() == R.id.listShop) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, this.arrayList);
                AdapterView.AdapterContextMenuInfo information = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int index = information.position;
                DatabaseReference userpost = mDatabase.child("bifs-shopping-list");
                DatabaseReference post = userpost.child(user.getUid());
                Log.i("ids", ids.get(index));
                arrayAdapter.remove(arrayList.get(index));
                post.child(ids.get(index)).removeValue();
                FragmentManager fragmentManager;
                fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                ShopList shopList = new ShopList();
                fragmentTransaction2.replace(R.id.container, shopList);
                fragmentTransaction2.addToBackStack(null);
                fragmentTransaction2.commit();
                Toast.makeText(getContext(), "Başarıyla Silindi.", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
