package com.androilk.bifs.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bs on 3.02.2018.
 */

public class StockAdd extends Fragment {

    String count="0" ;
    private static final String TAG = "Stock";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText etStockName;
    private Button btnStockAdd;
    private ProgressDialog mProgressDialog;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Ürün Ekle");
        return inflater.inflate(R.layout.stockadd, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();



        // [END initialize_database_ref]
        etStockName = (EditText) getView().findViewById(R.id.etStockName);
        btnStockAdd = (Button) getView().findViewById(R.id.btnStockAdd);
        getStockCount();
        btnStockAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getStockCount();
                DatabaseReference stockMainRef = mDatabase.child("Stock");
                Integer newCount = Integer.parseInt(count);
                Toast.makeText(getContext(), stockMainRef.getKey(), Toast.LENGTH_SHORT).show();
                newCount++;
                Toast.makeText(getContext(), newCount.toString(), Toast.LENGTH_SHORT).show();
                DatabaseReference stockIDref = stockMainRef.child(newCount.toString());
                stockIDref.setValue(newCount.toString());
                DatabaseReference stockNameref = stockIDref.child("StockName");
                stockNameref.setValue(etStockName.getText().toString());
                DatabaseReference countRef = mDatabase.child("Stock").child("stock_count");
                countRef.setValue(newCount.toString());

            }
        });
    }

    private void submitPost() {
        final String title = etStockName.getText().toString();


        // Title is required
        if (TextUtils.isEmpty(title)) {
            etStockName.setError(REQUIRED);
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
                          //  writeNewPost(userId, user.getEmail(), title, body + " " + dropdown.getSelectedItem().toString());
                            Toast.makeText(getContext(), "Ürün Eklendi.", Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        hideProgressDialog();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
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
        etStockName.setEnabled(enabled);

        if (enabled) {
            btnStockAdd.setVisibility(View.VISIBLE);
        } else {
            btnStockAdd.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title,body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Stock/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]

    /**
     * this method showing progessdialog if doesnt stop return always
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Ürün Ekleniyor..");
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

    public void  getStockCount(){

        DatabaseReference countRef = mDatabase.child("Stock").child("stock_count");
        countRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                count=dataSnapshot.getValue().toString();
                Toast.makeText(getContext(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });



    }
}
