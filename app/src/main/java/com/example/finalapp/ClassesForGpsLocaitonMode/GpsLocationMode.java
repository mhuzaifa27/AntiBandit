package com.example.finalapp.ClassesForGpsLocaitonMode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.ClassesForBatteryStatusMode.BatteryStatusMode;
import com.example.finalapp.ClassesForBatteryStatusMode.SmsReaderServiceForBatteryStatus;
import com.example.finalapp.MainActivity;
import com.example.finalapp.R;

public class GpsLocationMode extends Activity {

    private static final int  BTN_CLICKED_CODE=20;
    Switch gpsLocationSwitch;
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
        setContentView(R.layout.activity_gps_location);

        //TODO:For finding ID's
        inItComponents();
        //TODO: GPS LOCATION MODE FEATURE
        gpsLocationModeFeature();
    }

    private void gpsLocationModeFeature() {
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:CHARGER REMOVED MODE SWITCH
        SharedPreferences sp6 = getSharedPreferences("Swch6 State", Context.MODE_PRIVATE);
        gpsLocationSwitch.setChecked(sp6.getBoolean("Swch6 State", false));
        switchStatus=sp6.getBoolean("Swch6 State", false);

        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }
        //TODO: Code for GPS LOCATION MODE on Switch
        gpsLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true ) {
                    SharedPreferences.Editor editor2 = getSharedPreferences("Swch6 State", Context.MODE_PRIVATE).edit();
                    editor2.putBoolean("Swch6 State", true);
                    editor2.commit();
                    txtOnOff.setText("ON");
                    switchStatus=true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService( new Intent(GpsLocationMode.this, SmsReaderServiceForGps.class));
                    }
                    else{
                        startService( new Intent(GpsLocationMode.this, SmsReaderServiceForGps.class));
                    }
                    Intent goToGPSLocator=new Intent(GpsLocationMode.this,GPSLocator.class);
                    startActivityForResult(goToGPSLocator,BTN_CLICKED_CODE);
                }
                else{
                    stopService(new Intent(GpsLocationMode.this, SmsReaderServiceForGps.class));
                    //getContext().unregisterReceiver(mListener);
                    displayToast("GPS Location Mode is Disabled");
                    SharedPreferences.Editor editor2 = getSharedPreferences("Swch6 State",Context.MODE_PRIVATE).edit();
                    editor2.putBoolean("Swch6 State", false);
                    if(MainActivity.count>0)
                    {
                        spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                        spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
                        spEditorForCount.commit();
                    }
                    editor2.commit();
                    txtOnOff.setText("OFF");
                    switchStatus=false;
                }
            }
        });

    }

    private void inItComponents() {
        gpsLocationSwitch=(Switch)findViewById(R.id.swh_option6);
        txtOnOff=(TextView)findViewById(R.id.txt_on_off);

    }
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onPause();
    }
    public void backImageClick(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("GPS_LOCATION_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
    @Override
    public void onBackPressed() {
        //Log.d("CDA", "onBackPressed Called");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("GPS_LOCATION_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
    public void displayToast(String message) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case BTN_CLICKED_CODE:
                if(resultCode == Activity.RESULT_OK){
                    Boolean savedClick=data.getBooleanExtra("CODE_SAVED",false);
                    if(savedClick){
                        SharedPreferences.Editor editor4 =getSharedPreferences("Swch6 State", Context.MODE_PRIVATE).edit();
                        editor4.putBoolean("Swch6 State", true);
                        if(MainActivity.count<=8)
                        {
                            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                            spEditorForCount.putInt("countValue",++MainActivity.count);
                            spEditorForCount.commit();
                        }
                        editor4.commit();
                    }
                    else {
                        Boolean backClick=data.getBooleanExtra("CODE_NOT_SAVED",false);
                        stopService(new Intent(GpsLocationMode.this, SmsReaderServiceForGps.class));
                        //getContext().unregisterReceiver(mListener);
                        displayToast("GPS Location Mode is Disabled");
                        SharedPreferences.Editor editor2 = getSharedPreferences("Swch6 State",Context.MODE_PRIVATE).edit();
                        editor2.putBoolean("Swch6 State", false);
                        if(MainActivity.count>0)
                        {
                            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                            spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
                            spEditorForCount.commit();
                        }
                        editor2.commit();
                        txtOnOff.setText("OFF");
                        switchStatus=false;
                    }
                }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:GPS LOCATION MODE SWITCH
        SharedPreferences sp6 = getSharedPreferences("Swch6 State", Context.MODE_PRIVATE);
        gpsLocationSwitch.setChecked(sp6.getBoolean("Swch6 State", false));
        switchStatus=sp6.getBoolean("Swch6 State", false);
        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }
}
}
