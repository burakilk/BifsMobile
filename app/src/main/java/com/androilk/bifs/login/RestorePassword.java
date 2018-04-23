package com.androilk.bifs.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androilk.bifs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * Created by FBI on 3.02.2018.
 */

public class RestorePassword extends AppCompatActivity {

    private EditText etEmail;
    private Button btnRestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);
        initUI();
    }

    private void initUI() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        btnRestore = (Button) findViewById(R.id.btnRestore);
        mAuth = FirebaseAuth.getInstance();
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restorePassword(etEmail.getText().toString());
            }
        });
    }

    private void restorePassword(String email) {


        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // we will start the login activity here;
                    // this activty if is return true
                    startActivity(new Intent(RestorePassword.this, LoginScreen.class));
                    Toast.makeText(RestorePassword.this, "Mail Gönderildi.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RestorePassword.this, "Mail Gönderilmedi.Daha Sonra Tekrar Deneyiniz.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
