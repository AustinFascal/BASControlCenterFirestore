package com.ptbas.controlcenter.userprofile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ptbas.controlcenter.Helper;
import com.ptbas.controlcenter.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userPassCurr;
    private Button buttonChangePass, buttonReAuthenticate;
    private EditText editTextPassCur, editTextPassNew, editTextPassConfirmNew;

    Helper helper = new Helper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ActionBar actionBar = getSupportActionBar();
        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Perbarui Kata Sandi");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        editTextPassNew = findViewById(R.id.editText_change_pwd_new);
        editTextPassCur = findViewById(R.id.editText_change_pwd_current);
        editTextPassConfirmNew = findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        progressBar = findViewById(R.id.progressBar);
        buttonReAuthenticate = findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePass = findViewById(R.id.button_change_pwd);

        editTextPassNew.setEnabled(false);
        editTextPassConfirmNew.setEnabled(false);
        buttonChangePass.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")){
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else{
            reAuthenticateUser(firebaseUser);
        }
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPassCurr = editTextPassCur.getText().toString();

                if (TextUtils.isEmpty(userPassCurr)){
                    editTextPassCur.setError("Mohon masukkan kata sandi saat ini untuk autentikasi");
                    editTextPassCur.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPassCurr);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                editTextPassCur.setEnabled(false);
                                editTextPassNew.setEnabled(true);
                                editTextPassConfirmNew.setEnabled(true);

                                buttonReAuthenticate.setEnabled(false);
                                buttonChangePass.setEnabled(true);

                                textViewAuthenticated.setText("Anda telah terverifikasi. Anda dapat mengubah kata sandi sekarang.");

                                buttonChangePass.setBackgroundTintList(ContextCompat.getColorStateList(
                                        ChangePasswordActivity.this, R.color.dark_green));

                                buttonChangePass.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePass(firebaseUser);
                                    }
                                });
                            } else{
                                try {
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePass(FirebaseUser firebaseUser) {
        String userPassNew = editTextPassNew.getText().toString();
        String userPassNewConfirm = editTextPassConfirmNew.getText().toString();

        if (TextUtils.isEmpty(userPassNew)){
            editTextPassNew.setError("Mohon masukkan kata sandi dengan benar");
            editTextPassNew.requestFocus();
        } else if (TextUtils.isEmpty(userPassNewConfirm)){
            editTextPassConfirmNew.setError("Mohon masukkan ulang kata sandi baru Anda");
            editTextPassConfirmNew.requestFocus();
        } else if (!userPassNew.matches(userPassNewConfirm)){
            editTextPassConfirmNew.setError("Mohon masukkan kata sandi baru Anda");
            editTextPassConfirmNew.requestFocus();
        } else if (userPassCurr.matches(userPassNew)) {
            editTextPassNew.setError("Kata sandi baru diperlukan");
            editTextPassNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(userPassNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "Kata sandi telah diubah", Toast.LENGTH_SHORT).show();
                        /*Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);*/
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
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