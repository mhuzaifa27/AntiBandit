package com.example.finalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Features extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);
    }

    public void skipImageClick(View view) {
        startActivity(new Intent(Features.this,MainActivity.class));
        finish();
    }
}
