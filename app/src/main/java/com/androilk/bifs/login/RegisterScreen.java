package com.androilk.bifs.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androilk.bifs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by FBI on 3.02.2018.
 */

public class RegisterScreen extends AppCompatActivity {

    private EditText etEmail;
    private EditText etName;
    private EditText etPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        initUI();
    }

    private void initUI() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email Alanı Boş Geçilemez.", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(this, "Şifre En Az 6 Karakter Olmalı.", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Kayıt Olunuyor...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //user is succesfull registering and logged in;
                    // we will start the profile activity here;
                    Toast.makeText(RegisterScreen.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(RegisterScreen.this, LoginScreen.class));
                } else {
                    Toast.makeText(RegisterScreen.this, "Kayıt Başarısız.Tekrar Deneyin.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

    }
}
