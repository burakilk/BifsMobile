package com.androilk.bifs.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androilk.bifs.Class.Post;
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

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

/**
 * Created by FBI on 3.02.2018.
 */

public class ShopAdd extends Fragment {


    private static final String TAG = "ShoppAdd";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText etqty;
    private Button btnShopAdd,show;
    private ProgressDialog mProgressDialog;
    public Spinner dropdown;
    public List<String> datas;
    SpinnerDialog spinnerDialog;
    public TextView selectedItems;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Sepete Ekle");
        View v = inflater.inflate(R.layout.shopadd, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // [START initialize_database_ref]

        mDatabase = FirebaseDatabase.getInstance().getReference();
            dropdown = getView().findViewById(R.id.spinner1);
            datas =  new ArrayList<>();
            selectedItems =  getView().findViewById(R.id.txt);
            DatabaseReference stock = mDatabase.child("products");

        stock.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        datas.add(String.valueOf(snapshot.child("title").getValue()));
                    }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        spinnerDialog = new SpinnerDialog((Activity) getContext(), (ArrayList<String>) datas,
                "Ürün Seçiniz.");

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                selectedItems.setText(item);
                show.setText(item);
            }
        });

        getView().findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });
        String[] items = new String[]{"Kg","L","Adet"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);

        dropdown.setAdapter(adapter);
        // [END initialize_database_ref]
        //etShop = (EditText) getView().findViewById(R.id.etShop);
        etqty = (EditText) getView().findViewById(R.id.etqty);
        btnShopAdd = (Button) getView().findViewById(R.id.btnShopAdd);
        show = (Button) getView().findViewById(R.id.show);
        btnShopAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void submitPost() {
        final String title = selectedItems.getText().toString();
        final String body = etqty.getText().toString();

        if (TextUtils.isEmpty(title)) {
            show.setError(REQUIRED);
            return;
        }
        // Body is required
        if (TextUtils.isEmpty(body)) {
            etqty.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        showProgressDialog();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // [START single_value_read]
        final String userId = user.getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            hideProgressDialog();
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(getContext(),
                                    "Error: User Bulunamadı.",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // Write new post
                            Toast.makeText(getContext(), "UserId : "+ userId + "user email : "+ user.getEmail()+ "title :" + "body : "+ body + dropdown.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                            writeNewPost(userId, user.getEmail(), title, body + " " + dropdown.getSelectedItem().toString());
                            Toast.makeText(getContext(), "Sepete Eklendi.", Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        hideProgressDialog();
                        ShopList shopList = new ShopList();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, shopList);
                        transaction.addToBackStack(null);
                        transaction.commit();

                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]
    }



    private void setEditingEnabled(boolean enabled) {
        etqty.setEnabled(enabled);
        if (enabled) {
            btnShopAdd.setVisibility(View.VISIBLE);
        } else {
            btnShopAdd.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/bifs-shopping-list/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]

    /**
     * this method showing progessdialog if doesnt stop return always
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Sepete Ekleniyor..");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    /**
     * hide progessdialog
     */
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
