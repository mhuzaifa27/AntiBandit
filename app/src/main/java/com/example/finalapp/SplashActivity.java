package com.example.finalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity{


    int value,popUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        SharedPreferences sp7 = getSharedPreferences("first", Context.MODE_PRIVATE);
        value=sp7.getInt("first", 0);

        setContentView(R.layout.activity_splash);
        SharedPreferences sp10 = getSharedPreferences("popUp", Context.MODE_PRIVATE);
        popUp=sp10.getInt("popUp", 0);

        ImageView splashImage = findViewById(R.id.splashImage);
        Animation.startZoomEffect(splashImage);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(value==1 && popUp==0){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }
                else if(popUp==1){
                    startActivity(new Intent(SplashActivity.this,PopupActivity.class));
                    finish();
                }
                else{
                    SharedPreferences.Editor editor4 = getSharedPreferences("first", Context.MODE_PRIVATE).edit();
                    editor4.putInt("first", 1);
                    editor4.commit();
                    startActivity(new Intent(SplashActivity.this,Features.class));
                    finish();
                }

            }
        },3000);
    }
}
