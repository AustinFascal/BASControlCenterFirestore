package com.wingscorp.basreport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginPass;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Objects.requireNonNull(getSupportActionBar()).hide();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Masuk");
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPass = findViewById(R.id.editText_login_pass);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();

        ImageView imageViewShowHidePass = findViewById(R.id.imageView_show_hide_pass);
        imageViewShowHidePass.setImageResource(R.drawable.ic_pass_hide);
        imageViewShowHidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextLoginPass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextLoginPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePass.setImageResource(R.drawable.ic_pass_hide);
                } else {
                    editTextLoginPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePass.setImageResource(R.drawable.ic_pass_show);
                }
            }
        });

        Button btnLogin = findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtEmail = editTextLoginEmail.getText().toString();
                String txtPass = editTextLoginPass.getText().toString();

                if (TextUtils.isEmpty(txtEmail)){
                    editTextLoginEmail.setError("Alamat email tidak boleh kosong");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
                    editTextLoginEmail.setError("Masukkan alamat email yang benar");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(txtPass)){
                    editTextLoginPass.setError("Kata sandi tidak boleh kosong");
                    editTextLoginPass.requestFocus();
                }  else if (txtPass.length() < 6) {
                    editTextLoginPass.setError("Kata sandi tidak boleh kurang dari 6 karakter");
                    editTextLoginPass.requestFocus();
                } else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(txtEmail, txtPass);
                }
            }
        });
    }

    private void loginUser(String email, String pass) {
        authProfile.signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    if (firebaseUser.isEmailVerified()){
                        startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                        finish();
                        Toast.makeText(LoginActivity.this, "Berhasil masuk", Toast.LENGTH_SHORT).show();
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertEmailVerification();
                    }
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("Detail pengguna tidak ditemukan");
                        editTextLoginEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextLoginPass.setError("Kredensial tidak benar.");
                        editTextLoginPass.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(LoginActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertEmailVerification() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Belum Diverifikasi");
        builder.setMessage("Mohon verifikasi email Anda sekarang. Anda tidak dapat masuk apabila email belum diverifikasi");

        builder.setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser()!=null){
            Toast.makeText(this, "Anda sudah masuk", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
            finish();
        } /*else {
            Toast.makeText(this, "Anda bisa masuk sekarang", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}