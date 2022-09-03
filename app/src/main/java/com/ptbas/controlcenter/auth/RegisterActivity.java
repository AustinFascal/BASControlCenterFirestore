package com.ptbas.controlcenter.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
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

    private TextInputEditText editTextRegistFullName, editTextRegistEmail, editTextRegistDoB, editTextRegistPhoneNumber,
            editTextRegistPass, editTextRegistConfirmPass, editTextRegistAccessCode;
    private RadioGroup radioGroupRegistGender;
    private RadioButton radioButtonRegistGenderSelected;
    private DatePickerDialog datePicker;
    private static final String TAG = "RegisterActivity";

    ImageView imageViewShowHidePass1, imageViewShowHidePass2;
    ProgressDialog pd;

    private CheckBox cbTermsCond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_register);

        pd = new ProgressDialog(this);
        pd.setMessage("Mohon tunggu ...");
        pd.setCancelable(false);

        //Objects.requireNonNull(getSupportActionBar()).hide();

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        cbTermsCond = findViewById(R.id.checkBox_terms_conditions);
        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        editTextRegistFullName = findViewById(R.id.editText_register_full_name);
        editTextRegistEmail = findViewById(R.id.editText_register_email);
        editTextRegistDoB = findViewById(R.id.editText_register_dob);
        editTextRegistPhoneNumber = findViewById(R.id.editText_register_mobile);
        editTextRegistPass = findViewById(R.id.editText_register_password);
        editTextRegistConfirmPass = findViewById(R.id.editText_register_password_confirm);
        editTextRegistAccessCode = findViewById(R.id.editText_access_code);

        radioGroupRegistGender = findViewById(R.id.radio_group_register_gender);
        //radioGroupRegistGender.clearCheck();

        imageViewShowHidePass1 = findViewById(R.id.imageView_show_hide_pwd1);
        imageViewShowHidePass2 = findViewById(R.id.imageView_show_hide_pwd2);

        imageViewShowHidePass1.setImageResource(R.drawable.ic_pass_hide);
        imageViewShowHidePass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextRegistPass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextRegistPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePass1.setImageResource(R.drawable.ic_pass_hide);
                } else {
                    editTextRegistPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePass1.setImageResource(R.drawable.ic_pass_show);
                }
            }
        });

        imageViewShowHidePass2.setImageResource(R.drawable.ic_pass_hide);
        imageViewShowHidePass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextRegistConfirmPass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextRegistConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePass2.setImageResource(R.drawable.ic_pass_hide);
                } else {
                    editTextRegistConfirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePass2.setImageResource(R.drawable.ic_pass_show);
                }
            }
        });

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
                        editTextRegistDoB.setError(null);
                    }
                }, year, month, day);
                datePicker.show();
            }
        });

        fabBack.setOnClickListener(view -> finish());

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
                } else if (!cbTermsCond.isChecked()){
                    Toast.makeText(RegisterActivity.this, "Mohon setujui Ketentuan Layanan dan Kebijakan Privasi terlebih dahulu.", Toast.LENGTH_SHORT).show();
                } else{
                    txtGender = radioButtonRegistGenderSelected.getText().toString();
                    //progressBar.setVisibility(View.VISIBLE);
                    pd.show();
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

                    UserModel userModel = new UserModel(txtFullName, txtDob, txtGender, txtMobile, txtAccessCode);

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
                            pd.dismiss();

                        }
                    });

                } else{
                    try{
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthWeakPasswordException e){
                        editTextRegistPass.setError("Kata sandi terlalu lemah. Mohon masukkan karakter campuran berupa huruf, angka dan karakter spesial");
                        editTextRegistPass.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextRegistEmail.setError("Alamat email yang Anda masukkan telah terdaftar.");
                        editTextRegistEmail.requestFocus();
                    } catch (FirebaseAuthEmailException e){
                        editTextRegistEmail.setError("Pengguna telah terdaftar.");
                        editTextRegistEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, "Terjadi kesalahan. Pastikan alamat email yang Anda masukkan belum terdaftar.", Toast.LENGTH_LONG).show();
                    }
                    pd.dismiss();
                }
            }
        });
    }

}