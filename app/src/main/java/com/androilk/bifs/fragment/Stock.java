package com.androilk.bifs.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bs on 3.02.2018.
 */

public class Stock extends Fragment {

    String count="0" ;
    private static final String TAG = "Stock";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText etStockName;
    private Button btnStockAdd;
    private ProgressDialog mProgressDialog;
    TextView textView2,sicaklikTXT;
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    ImageView imgphoto;
    FirebaseUser user;
    DatabaseReference productRef,tempRef;
    DatabaseReference photo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Stoğum");
        return inflater.inflate(R.layout.stock, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sicaklikTXT = getView().findViewById(R.id.sicaklikTXT);
        imgphoto = getView().findViewById(R.id.imgphoto);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        String email = user.getEmail();
        final String mail = email.replace(".",",");
        tempRef = db.getReference("temp").child(mail);

        tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("temp").getValue()!=null){
                    sicaklikTXT.setText(dataSnapshot.child("temp").getValue().toString()+"° C ");

                }
                else
                    sicaklikTXT.setText("Sıcaklık Verisi Yok");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final String eposta = email.replace(".", ",");
        photo = db.getReference("photo").child(mail);
        photo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("photo").getValue() != null) {
                    Log.w("photo",dataSnapshot.child("photo").getValue().toString());
                    byte[] decodedString = Base64.decode(dataSnapshot.child("photo").getValue().toString(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);
                    imgphoto.setImageBitmap(decodedByte);
                } else
                {
                    Toast.makeText(getContext(), "else", Toast.LENGTH_SHORT).show();
                    imgphoto.setImageBitmap(null);
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




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


}
