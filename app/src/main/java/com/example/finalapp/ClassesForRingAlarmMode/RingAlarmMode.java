package com.example.finalapp.ClassesForRingAlarmMode;

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


import com.example.finalapp.MainActivity;
import com.example.finalapp.R;

public class RingAlarmMode extends Activity {

    Switch ringAlarmSwitch;
    private static final int REQUEST_ENABLE_FOR_RING = 200;
    Boolean switchStatus=false;
    TextView txtOnOff;
    Toast toast;
    SharedPreferences spForCount;
    SharedPreferences.Editor spEditorForCount;
    int ringSwitch=0;

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_ring_alarm);


        //TODO:For finding ID's
        inItComponents();
        //TODO:Main Function
        ringAlarmFeature();


    }

    private void ringAlarmFeature() {
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:CHARGER REMOVED MODE SWITCH
        SharedPreferences sp5 = getSharedPreferences("Swch5 State", Context.MODE_PRIVATE);
        ringAlarmSwitch.setChecked(sp5.getBoolean("Swch5 State", false));
        switchStatus=sp5.getBoolean("Swch5 State", false);

        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }

        //TODO: Code for BATTERY STATUS MODE on Switch
        ringAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent goToBatteryStatus = new Intent(RingAlarmMode.this, RingAlarm.class);
                    startActivityForResult(goToBatteryStatus,REQUEST_ENABLE_FOR_RING);
                }
                else{

                    stopService(new Intent(RingAlarmMode.this, SmsReaderServiceForRingAlarm.class));
                    //getContext().unregisterReceiver(mListener);
                    displayToast("Ring Alarm Mode is Disabled");
                    SharedPreferences.Editor editor4 = getSharedPreferences("Swch5 State",Context.MODE_PRIVATE).edit();
                    editor4.putBoolean("Swch5 State", false);
                    if(MainActivity.count>0)
                    {
                        spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                        spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
                        spEditorForCount.commit();
                    }
                    txtOnOff.setText("OFF");
                    switchStatus=false;
                    editor4.commit();
                }
            }
        });

    }


    private void inItComponents() {
        ringAlarmSwitch=(Switch)findViewById(R.id.swh_option5);
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
        returnIntent.putExtra("RING_ALARM_SWITCH_STATUS",switchStatus);
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
            case REQUEST_ENABLE_FOR_RING:
                if(resultCode == Activity.RESULT_OK){
                    Boolean savedClick=data.getBooleanExtra("CODE_SAVED",false);
                    if(savedClick){
                        SharedPreferences.Editor editor4 =getSharedPreferences("Swch5 State", Context.MODE_PRIVATE).edit();
                        editor4.putBoolean("Swch5 State", true);
                        if(MainActivity.count<=8)
                        {
                            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                            spEditorForCount.putInt("countValue",++MainActivity.count);
                            spEditorForCount.commit();
                        }
                        editor4.commit();
                        txtOnOff.setText("ON");
                        switchStatus=true;
                        editor4.commit();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            startForegroundService(new Intent(RingAlarmMode.this, SmsReaderServiceForRingAlarm.class));
                        } else {
                            startService(new Intent(RingAlarmMode.this, SmsReaderServiceForRingAlarm.class));
                        }
                    }
                    else {
                        ringAlarmSwitch.setChecked(false);
                        SharedPreferences.Editor editor4 = getSharedPreferences("Swch5 State",Context.MODE_PRIVATE).edit();
                        editor4.putBoolean("Swch5 State", false);
                        if(MainActivity.count>0)
                        {
                            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                            spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
                            spEditorForCount.commit();
                        }
                        editor4.commit();
                        txtOnOff.setText("OFF");
                        switchStatus=false;
                        editor4.commit();
                    }
                }
        }
    }

    /*private void enableRingService() {
            ringAlarmSwitch.setChecked(true);
            SharedPreferences.Editor editor1 = getSharedPreferences("Swch5 State", Context.MODE_PRIVATE).edit();
            editor1.putBoolean("Swch5 State", true);
            if(MainActivity.count<=8)
            {
                spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count+1);
                spEditorForCount.commit();
            }
            txtOnOff.setText("ON");
            switchStatus=true;
            editor1.commit();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(RingAlarmMode.this, SmsReaderServiceForRingAlarm.class));
                //Log.d("abc","yes service enabled");
            } else {
                //Log.d("abc","yes service enabled");
                startService(new Intent(RingAlarmMode.this, SmsReaderServiceForRingAlarm.class));
            }

        }*/
    @Override
    protected void onResume() {
        super.onResume();
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:BATTERY STATUS MODE SWITCH
        SharedPreferences sp5 = getSharedPreferences("Swch5 State", Context.MODE_PRIVATE);
        ringAlarmSwitch.setChecked(sp5.getBoolean("Swch5 State", false));
        switchStatus=sp5.getBoolean("Swch5 State", false);
        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }
    }
    @Override
    public void onBackPressed() {
        //Log.d("CDA", "onBackPressed Called");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("RING_ALARM_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
