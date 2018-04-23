package com.androilk.bifs.fragment;


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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SingleSpinner;
import com.androidbuts.multispinnerfilter.SingleSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.androilk.bifs.Class.Post;
import com.androilk.bifs.Class.PostRecipe;
import com.androilk.bifs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ilkim on 18.02.2018.
 */

public class Recipe extends Fragment {

    private static final String TAG = "RecipeAdd";
    private static final String REQUIRED = "Required";
    DatabaseReference mDatabase;
    List<String> products_id;
    EditText etRecipe;
    EditText etDescription;
    Button btnRecipeAdd;
    ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Tarif Ekle");
        View v =  inflater.inflate(R.layout.activity_recipe, container, false);
        return v;

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etRecipe = (EditText) getView().findViewById(R.id.etRecipe);
        etDescription = (EditText) getView().findViewById(R.id.etDescription);
        btnRecipeAdd = (Button) getView().findViewById(R.id.btnRecipeAdd);
        final List<String> querys = new ArrayList<>();
        final List<String> queryids = new ArrayList<>();
        products_id = new ArrayList<>();
        final List<KeyPairBoolData> listArray0 = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final MultiSpinnerSearch searchMultiSpinnerUnlimited = (MultiSpinnerSearch)getView().findViewById(R.id.searchMultiSpinnerUnlimited);
        btnRecipeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        final HashMap<String, Object> result = new HashMap<>();
        DatabaseReference stock = mDatabase.child("products");
        stock.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    querys.add(snapshot.child("title").getValue().toString());
                    queryids.add(snapshot.child("id").getValue().toString());
                    Log.w("query", String.valueOf(querys.size()));
                }
                for (int i = 0; i < queryids.size(); i++) {
                    KeyPairBoolData h = new KeyPairBoolData();
                    h.setId(Integer.parseInt(queryids.get(i)));
                    h.setName(querys.get(i));
                    h.setSelected(false);
                    listArray0.add(h);
                }
            } @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        searchMultiSpinnerUnlimited.setItems(listArray0, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getId() + " : " + items.get(i).getName());
                        products_id.add(String.valueOf(items.get(i).getId()));
                    }
                }
            }
        });
    }

    private void submitPost() {
        final String title = etRecipe.getText().toString();
        final String body = etDescription.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            etRecipe.setError(REQUIRED);
            return;
        }
        // Body is required
        if (TextUtils.isEmpty(body)) {
            etDescription.setError(REQUIRED);
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

                        }else {
                            // Write new post
                            writeNewPost(userId, user.getEmail(), title, body, (List<String>) products_id);
                            Toast.makeText(getContext(), "Tarif Eklendi.", Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        hideProgressDialog();
                        RecipeList recipeList = new RecipeList();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, recipeList);
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
        etRecipe.setEnabled(enabled);
        etDescription.setEnabled(enabled);
        if (enabled) {
            btnRecipeAdd.setVisibility(View.VISIBLE);
        } else {
            btnRecipeAdd.setVisibility(View.GONE);
        }
    }


    private void writeNewPost(String userId, String username, String title, String body,List<String> products_id) {

        String key = mDatabase.child("posts").push().getKey();
        PostRecipe postRecipe = new PostRecipe(userId, username, title, body,products_id);
        Map<String, Object> postValues = postRecipe.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user-recipe-list/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Tarif Listesine Ekleniyor...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}


