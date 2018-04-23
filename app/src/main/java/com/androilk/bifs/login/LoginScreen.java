package com.androilk.bifs.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androilk.bifs.MainActivity;
import com.androilk.bifs.R;
import com.facebook.CallbackManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by FBI on 3.02.2018.
 */

public class LoginScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvRestore;
    private Button btnFacebook;
    private SignInButton btnGoogle;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = LoginScreen.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;
    private ProgressDialog mProgressDialog;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvRestore= (TextView) findViewById(R.id.tvRestore);
        tvRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginScreen.this, RestorePassword.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, RegisterScreen.class));
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                    hideProgressDialog();
                } else {
                    updateUI(null);
                }
            }
        };

        /**
         * Button normally login.
         */
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                startSignIn();
                print();

            }
        });
    }


    private void startSignIn() {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginScreen.this, "Email Alanı Boş Geçilemez.", Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        } else if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(this, "Şifre En Az 6 Karakter Olmalı.", Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginScreen.this, "Hata Oluştu.Tekrar Deneyin.", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                        updateUI(null);
                    } else {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }
                }
            });
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(this, "Başarılı Şekilde Giriş Yaptınız.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginScreen.this, MainActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Giriş Başarısız.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    /**
     * Hash Key Genereta
     */
    private void print() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.androilk.bifs", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google service Error", Toast.LENGTH_SHORT).show();
    }
}
