package com.ptbas.controlcenter.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.ptbas.controlcenter.DashboardActivity;
import com.ptbas.controlcenter.userprofile.ForgotPasswordActivity;
import com.ptbas.controlcenter.R;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginPass;
    private TextView textViewForgotPass, textViewRegister;
    private FirebaseAuth authProfile;
    LottieAnimationView lavWelcomeAnim;
    private static final String TAG = "LoginActivity";

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_login);

        pd = new ProgressDialog(this);
        pd.setMessage("Mohon tunggu ...");
        pd.setCancelable(false);

        //Objects.requireNonNull(getSupportActionBar()).hide();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //actionBar.setTitle("Masuk");
        // showing the back button in action bar
        //actionBar.setDisplayHomeAsUpEnabled(true);

        lavWelcomeAnim = findViewById(R.id.lavWelcomeAnim);
        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPass = findViewById(R.id.editText_login_pass);
        textViewForgotPass = findViewById(R.id.textView_forgot_password_link);
        textViewRegister = findViewById(R.id.textView_register_link);

        authProfile = FirebaseAuth.getInstance();

        lavWelcomeAnim.playAnimation();

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
                    pd.show();
                    loginUser(txtEmail, txtPass);
                }
            }
        });

        textViewForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
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
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
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
                pd.dismiss();
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
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        if (authProfile.getCurrentUser()!=null){
            if (firebaseUser.isEmailVerified()){
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
                Toast.makeText(LoginActivity.this, "Berhasil masuk", Toast.LENGTH_SHORT).show();
            } else {
                firebaseUser.sendEmailVerification();
                authProfile.signOut();
                showAlertEmailVerification();
            }
        }
    }
}