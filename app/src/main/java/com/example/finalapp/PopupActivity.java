package com.example.finalapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


public class PopupActivity  extends Activity {

    Button okBtn;
    EditText etPwd;
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavBar();
        SharedPreferences.Editor editor4 = getSharedPreferences("popUp", Context.MODE_PRIVATE).edit();
        editor4.putInt("popUp", 1);
        editor4.commit();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.motion_sense_dialog);
        okBtn=(Button)findViewById(R.id.ok);
        etPwd=(EditText) findViewById(R.id.et_password);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etPwd.getText().toString().trim().equalsIgnoreCase("1234")){
                    stopService(new Intent(PopupActivity.this, RingSirenService.class));
                    SharedPreferences.Editor editor4 = getSharedPreferences("popUp", Context.MODE_PRIVATE).edit();
                    editor4.putInt("popUp", 0);
                    editor4.commit();
                    startActivity(new Intent(PopupActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    etPwd.getText().clear();
                    etPwd.setError("Wrong Password");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
    @Override
    public void onAttachedToWindow()
    {

        //super.onAttachedToWindow();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {

            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    private void hideNavBar() {
        if (Build.VERSION.SDK_INT >= 19) {
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
