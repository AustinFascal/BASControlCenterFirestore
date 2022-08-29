package com.ptbas.controlcenter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ptbas.controlcenter.auth.LoginActivity;
import com.ptbas.controlcenter.auth.RegisterActivity;
import com.ptbas.controlcenter.helper.Helper;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Helper helper = new Helper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();
        Button btn_login = findViewById(R.id.button_login);
        btn_login.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        TextView textView_register_link = findViewById(R.id.textView_register_link);
        textView_register_link.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (helper.getUserId()!=null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }


}