package com.example.pinteditor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //espera 2,5 seg y lanza mainActivity
        new Handler().postDelayed(() ->{
            Intent intent= new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        },4000);
    }
}
