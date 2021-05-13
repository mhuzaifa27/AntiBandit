package com.example.finalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }
    public void backImageClick(View view) {
        Intent returnIntent = new Intent(Help.this,MainActivity.class);
        startActivity(returnIntent);
        finish();
    }
}
