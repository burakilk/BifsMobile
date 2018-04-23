package com.androilk.bifs.fragment;

import android.animation.Animator;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androilk.bifs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by fbura on 22.02.2018.
 */

public class Setting extends Fragment {
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private EditText etPassChange;
    private Button btnPassChange;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Ayarlar");
        return inflater.inflate(R.layout.settings, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        etPassChange=getView().findViewById(R.id.etPassChange);
        btnPassChange=getView().findViewById(R.id.btnPassChange);
        btnPassChange.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                updatePassword(currentUser);
            }
        });

    }
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updatePassword(FirebaseUser user) {
        showProgressDialog();
        String password = etPassChange.getText().toString();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(getContext(), "Şifre En Az 6 Karakter Olmalı.", Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        }
        user.updatePassword(etPassChange.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (task.isSuccessful()) {
                        Home home = new Home();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, home);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        Snackbar.make(getView(), "Şifre Değiştirme Başarıyla Gerçekleşti.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(getView(), "Şifre Değiştirme Başarısız.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    hideProgressDialog();
                }
            }
        });
    }
    /**
     * this method showing progessdialog if doesnt stop return always
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
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
