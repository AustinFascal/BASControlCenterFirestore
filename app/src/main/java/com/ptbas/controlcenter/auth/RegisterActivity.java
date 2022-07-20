package com.ptbas.controlcenter.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.UserModel;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegistFullName, editTextRegistEmail, editTextRegistDoB, editTextRegistPhoneNumber,
            editTextRegistPass, editTextRegistConfirmPass, editTextRegistAccessCode;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegistGender;
    private RadioButton radioButtonRegistGenderSelected;
    private DatePickerDialog datePicker;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Objects.requireNonNull(getSupportActionBar()).hide();

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Daftar");
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        editTextRegistFullName = findViewById(R.id.editText_register_full_name);
        editTextRegistEmail = findViewById(R.id.editText_register_email);
        editTextRegistDoB = findViewById(R.id.editText_register_dob);
        editTextRegistPhoneNumber = findViewById(R.id.editText_register_mobile);
        editTextRegistPass = findViewById(R.id.editText_register_password);
        editTextRegistConfirmPass = findViewById(R.id.editText_register_password_confirm);
        editTextRegistAccessCode = findViewById(R.id.editText_access_code);

        radioGroupRegistGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegistGender.clearCheck();


        editTextRegistDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextRegistDoB.setText(dayOfMonth + "/" +(month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePicker.show();
            }
        });

        Button btnRegister = findViewById(R.id.button_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId = radioGroupRegistGender.getCheckedRadioButtonId();
                radioButtonRegistGenderSelected = findViewById(selectedGenderId);

                String txtFullName = editTextRegistFullName.getText().toString();
                String txtEmail = editTextRegistEmail.getText().toString();
                String txtDob = editTextRegistDoB.getText().toString();
                String txtMobile = editTextRegistPhoneNumber.getText().toString();
                String txtPass = editTextRegistPass.getText().toString();
                String txtConfirmPass = editTextRegistConfirmPass.getText().toString();
                String txtAccessCode = editTextRegistAccessCode.getText().toString();
                String txtGender;

                String phoneRegex = "[6-9][0-9]{9}";
                Matcher phoneMatcher;
                Pattern phonePattern = Pattern.compile(phoneRegex);
                phoneMatcher = phonePattern.matcher(txtMobile);

                if (TextUtils.isEmpty(txtFullName)){
                    editTextRegistFullName.setError("Nama lengkap tidak boleh kosong");
                    editTextRegistFullName.requestFocus();
                } else if (TextUtils.isEmpty(txtEmail)){
                    editTextRegistEmail.setError("Alamat email tidak boleh kosong");
                    editTextRegistEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
                    editTextRegistEmail.setError("Masukkan alamat email yang benar");
                    editTextRegistEmail.requestFocus();
                } else if (TextUtils.isEmpty(txtDob)){
                    editTextRegistDoB.setError("Tanggal lahir tidak boleh kosong");
                    editTextRegistDoB.requestFocus();
                } else if (TextUtils.isEmpty(txtMobile)){
                    editTextRegistPhoneNumber.setError("Nomor telepon tidak boleh kosong");
                    editTextRegistPhoneNumber.requestFocus();
                } else if (txtMobile.length() <= 10){
                    editTextRegistPhoneNumber.setError("Masukkan nomor telepon dengan benar");
                    editTextRegistPhoneNumber.requestFocus();
                } else if (!phoneMatcher.find()){
                    editTextRegistPhoneNumber.setError("Masukkan nomor telepon dengan benar");
                    editTextRegistPhoneNumber.requestFocus();
                } else if (TextUtils.isEmpty(txtPass)){
                    editTextRegistPass.setError("Nomor telepon tidak valid");
                    editTextRegistPass.requestFocus();
                }  else if (txtPass.length() < 6){
                    editTextRegistPass.setError("Kata sandi tidak boleh kurang dari 6 karakter");
                    editTextRegistPass.requestFocus();
                }  else if (TextUtils.isEmpty(txtConfirmPass)){
                    editTextRegistConfirmPass.setError("Konfirmasi kata sandi tidak boleh kosong");
                    editTextRegistConfirmPass.requestFocus();
                } else if (!txtConfirmPass.equals(txtPass)){
                    editTextRegistConfirmPass.setError("Konfirmasi kata sandi tidak sama");
                    editTextRegistConfirmPass.requestFocus();
                } else if (TextUtils.isEmpty(txtAccessCode)){
                    editTextRegistAccessCode.setError("Kode akses tidak boleh kosong");
                    editTextRegistAccessCode.requestFocus();
                } else if (radioGroupRegistGender.getCheckedRadioButtonId() == -1){
                    radioButtonRegistGenderSelected.setError("Jenis kelamin tidak boleh kosong");
                    radioButtonRegistGenderSelected.requestFocus();
                } else{
                    txtGender = radioButtonRegistGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);

                    registerUser(txtFullName, txtEmail, txtDob, txtGender, txtMobile, txtPass, txtAccessCode);
                }
            }
        });
    }

    private void registerUser(String txtFullName, String txtEmail, String txtDob, String txtGender, String txtMobile, String txtPass, String txtAccessCode) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(txtEmail, txtPass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(txtFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    UserModel userModel = new UserModel(txtDob, txtGender, txtMobile, txtAccessCode);

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
                    referenceProfile.child(Objects.requireNonNull(firebaseUser).getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Pendaftaran tidak berhasil", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);

                        }
                    });

                } else{
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        editTextRegistPass.setError("Kata sandi terlalu lemah. Mohon masukkan karakter campuran berupa huruf, angka dan karakter spesial");
                        editTextRegistPass.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextRegistPass.setError("Alamat email yang Anda masukkan telah terdaftar.");
                        editTextRegistPass.requestFocus();
                    } catch (FirebaseAuthInvalidUserException e){
                        editTextRegistPass.setError("Pengguna telah terdaftar.");
                        editTextRegistPass.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
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