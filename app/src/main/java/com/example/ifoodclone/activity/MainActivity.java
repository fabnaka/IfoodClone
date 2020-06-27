package com.example.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.ifoodclone.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                irAutenticacao();
            }
        },4000 );
    }


    public void irAutenticacao(){
        Intent i = new Intent(MainActivity.this, AutenticacaoActivity.class);

        startActivity(i);
    }
}