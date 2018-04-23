package com.androilk.bifs.fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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


public class RecipeSearching extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser user;
    AutoCompleteTextView txtSensorContent;
    DatabaseReference mDatabase;
    FirebaseDatabase db;
    DatabaseReference productRef;
    List<String> arrayList;
    List<String> ids;
    List<String> urun_id;
    List<String> urun_adi;
    ArrayList products;
    String productsArray[];
    String urn = "";
    ListView listView;
    Map<String, String> map,map2;

    AutoCompleteTextView Liste;
    Button btn_Kaydet;
    TextView etTitle, etIntro, etBody, etNote, etUrun;
    private TextView txtSensorName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Ara");
        map = new HashMap<String, String>();
        urun_adi =  new ArrayList<>();
        return inflater.inflate(R.layout.recipe_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // [START initialize_database_ref]
        products = new ArrayList();
        txtSensorName = (TextView) getView().findViewById(R.id.txtSensorName);
        etTitle = (TextView) getView().findViewById(R.id.tvTitle);
        etIntro = (TextView) getView().findViewById(R.id.tvIntro);
        etBody = (TextView) getView().findViewById(R.id.tvBody);
        etNote = (TextView) getView().findViewById(R.id.tvNote);
        etUrun = (TextView) getView().findViewById(R.id.tvUrun);
        txtSensorContent = view.findViewById(R.id.txtSensorContent);
        db = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference productRef = mDatabase.child("recipes");
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
        txtSensorContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                urun_adi =  new ArrayList<>();
                mDatabase = FirebaseDatabase.getInstance().getReference();
                DatabaseReference post = mDatabase.child("recipes");
                final String deneme = String.valueOf(getProductKey(txtSensorContent.getText().toString()));

                if(!deneme.equals("none")){
                    DatabaseReference value = post.child(String.valueOf(getProductKey(txtSensorContent.getText().toString())));
                    DatabaseReference sen = mDatabase.child("recipe_products");
                    urun_adi =  new ArrayList<>();
                    sen.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(deneme.equals(String.valueOf(snapshot.child("recipe_id").getValue()))) {

                                    DatabaseReference posts = mDatabase.child("products");

                                    DatabaseReference value = posts.child(String.valueOf(snapshot.child("product_id").getValue()));

                                    value.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            Log.w("urunler", String.valueOf(dataSnapshot.child("title").getValue().toString()));

                                            urun_adi.add(dataSnapshot.child("title").getValue().toString());

                                            String den ="Ürünler: ";

                                                for(int o = 0; o<urun_adi.size(); o++ ){
                                                    den += urun_adi.get(o).toString()+", ";

                                                }
                                            //Log.w("urn", den);
                                            etUrun.setText(den);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });


                    value.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            etTitle.setText(dataSnapshot.child("title").getValue().toString());
                            etIntro.setText(dataSnapshot.child("intro").getValue().toString());
                            etBody.setText(dataSnapshot.child("body").getValue().toString());
                            etNote.setText(dataSnapshot.child("note").getValue().toString());




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


    }

    public Object getProductKey(Object product){



        for(Object o : map.keySet()){
            if(map.get(o).equals(product))
                return o;
        }

        return "none";
    }
}

