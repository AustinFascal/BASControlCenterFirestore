package com.ptbas.controlcenter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ptbas.controlcenter.auth.LoginActivity;
import com.ptbas.controlcenter.auth.RegisterActivity;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        authProfile = FirebaseAuth.getInstance();

        Objects.requireNonNull(getSupportActionBar()).hide();

        Button btn_login = findViewById(R.id.button_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        TextView textView_register_link = findViewById(R.id.textView_register_link);
        textView_register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } /*else {
            Toast.makeText(this, "Anda bisa masuk sekarang", Toast.LENGTH_SHORT).show();
        }*/
    }


}