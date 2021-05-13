package com.example.finalapp.ClassesForTorchMode;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.MainActivity;
import com.example.finalapp.R;

public class TorchMode extends Activity {

    private static final int MY_PERMISSIONS = 111;
    Switch torchModeSwitch;
    TextView txtOnOff;
    Toast toast;
    Boolean switchStatus=false;
    SharedPreferences spForCount;
    SharedPreferences.Editor spEditorForCount;

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_torch_mode);

        //TODO:For finding ID's
        inItComponents();
        //TODO: GPS LOCATION MODE FEATURE
       // gpsLocationModeFeature();

        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:CHARGER REMOVED MODE SWITCH
        SharedPreferences sp8 = getSharedPreferences("Swch8 State", Context.MODE_PRIVATE);
        torchModeSwitch.setChecked(sp8.getBoolean("Swch8 State", false));
        switchStatus=sp8.getBoolean("Swch8 State", false);

        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }
        //TODO: Code for TORCH MODE on Switch
        torchModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    SharedPreferences.Editor editor3 =getSharedPreferences("Swch8 State", Context.MODE_PRIVATE).edit();
                    editor3.putBoolean("Swch8 State", true);
                    if(MainActivity.count<=8)
                    {
                        spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                        spEditorForCount.putInt("countValue",++MainActivity.count);
                        spEditorForCount.commit();
                    }
                    editor3.commit();
                    txtOnOff.setText("ON");
                    switchStatus=true;
                    editor3.commit();
                    checkPermission();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        startForegroundService(new Intent(TorchMode.this, BackgroundServiceForTorch.class));
                    } else {
                        startService(new Intent(TorchMode.this, BackgroundServiceForTorch.class));
                    }
                }
                else{
                    SharedPreferences.Editor editor3 = TorchMode.this.getSharedPreferences("Swch8 State",Context.MODE_PRIVATE).edit();
                    editor3.putBoolean("Swch8 State", false);
                    if(MainActivity.count>0)
                    {
                        spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                        spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
                        spEditorForCount.commit();
                    }
                    editor3.commit();
                    txtOnOff.setText("OFF");
                    switchStatus=false;
                    editor3.commit();
                    stopService(new Intent(TorchMode.this, BackgroundServiceForTorch.class));

                }
            }
        });

    }

    private void inItComponents() {
        torchModeSwitch=(Switch)findViewById(R.id.swh_option8);
        txtOnOff=(TextView)findViewById(R.id.txt_on_off);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onPause();
    }
    public void backImageClick(View view)
    {
        //Log.d("CDA", "onBackPressed Called");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("TORCH_MODE_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
    public boolean checkPermission(){
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(TorchMode.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(TorchMode.this, Manifest.permission.READ_SMS)) {

                } else {
                    ActivityCompat.requestPermissions(TorchMode.this, new String[]{Manifest.permission.READ_SMS}, MY_PERMISSIONS);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    @Override
    public void onBackPressed() {
        //Log.d("CDA", "onBackPressed Called");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("TORCH_MODE_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:CHARGER REMOVED MODE SWITCH
        SharedPreferences sp8 = getSharedPreferences("Swch8 State", Context.MODE_PRIVATE);
        torchModeSwitch.setChecked(sp8.getBoolean("Swch8 State", false));
        switchStatus=sp8.getBoolean("Swch8 State", false);
        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }
    }
    public void displayToast(String message) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
