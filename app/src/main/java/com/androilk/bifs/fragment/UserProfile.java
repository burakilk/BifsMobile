package com.androilk.bifs.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androilk.bifs.MainActivity;
import com.androilk.bifs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by BusraSerbest on 16.02.2018.
 */

public class UserProfile extends android.support.v4.app.Fragment {

    EditText userMail,editTextPass;
    ImageView editBTN, userPic, saveBTN;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    List<String> ids;
    StorageReference ref;
    int control = 0;
    private ProgressDialog mProgressDialog;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private ArrayList<String> pathArray;
    private int array_position;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_profile, container, false);
        getActivity().setTitle("Profil");
        mAuth = FirebaseAuth.getInstance();
        init(v);
        return v;
    }

    public void init(View v) {
        final FirebaseUser user = mAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userMail = v.findViewById(R.id.userMailTXT);
        editBTN = v.findViewById(R.id.editBTN);
        saveBTN = v.findViewById(R.id.saveBTN);
        userPic = v.findViewById(R.id.userPic);
        ref = FirebaseStorage.getInstance().getReference(currentUser.getUid().toString());

        editDurumu(1);
        editBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDurumu(control);
                control++;
            }
        });
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadInfo();
                editDurumu(1);
                control++;

            }
        });

        doldur();
    }

    public void editDurumu(int control) {
        if (control % 2 == 1) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            userMail.setClickable(false);
            userMail.setCursorVisible(false);
            userMail.setFocusable(false);
            userMail.setFocusableInTouchMode(false);
            editBTN.setImageResource(R.drawable.if_edit_103539);
            userPic.setOnClickListener(null);
            saveBTN.setOnClickListener(null);
            saveBTN.setVisibility(View.GONE);
            editBTN.setVisibility(View.VISIBLE);

        } else if (control % 2 == 0) {
            userMail.setClickable(true);
            userMail.setCursorVisible(true);
            userMail.setFocusable(true);
            userMail.setFocusableInTouchMode(true);
            editBTN.setVisibility(View.GONE);
            saveBTN.setVisibility(View.VISIBLE);
            userPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseImage();

                }
            });
            saveBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadInfo();
                    editDurumu(1);
                }
            });
        }
    }

    public void uploadInfo() {

        //filePath= Uri.parse("android.resource://com.androilk.bifs/drawable/busra");;
        if (filePath != null) {
            Uri file = filePath;
            // Uri file1 = Uri.parse("android.resource://com.androilk.bifs/drawable/busra");
            StorageReference ref1 = ref.child("users_photo/");
            ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Başarılı", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());

                        }
                    });

        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("bifs-users");
        myRef.child(currentUser.getUid().toString()).child("user_name").setValue(userMail.getText().toString());
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            userPic.setImageURI(filePath);

        }
    }

    public void doldur() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://bifs-715da.appspot.com").child(currentUser.getUid().toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dRef = ref.child("bifs-users").child(currentUser.getUid().toString()).child("user_name");

        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userMail.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final long ONE_MEGABYTE = 1024 * 1024;
        //download file as a byte array
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                userPic.setImageBitmap(bitmap);

            }
        });
    }
    /**
     * this method showing progessdialog if doesnt stop return always
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Güncelleniyor..");
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
