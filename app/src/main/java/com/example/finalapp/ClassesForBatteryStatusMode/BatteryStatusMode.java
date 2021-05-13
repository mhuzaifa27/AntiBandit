package com.example.finalapp.ClassesForBatteryStatusMode;

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

public class BatteryStatusMode extends Activity {

    private static final int  BTN_CLICKED_CODE=10;
    Switch batteryStatusSwitch;
    Boolean switchStatus=false;
    TextView txtOnOff;
    Toast toast;
    SharedPreferences spForCount;
    SharedPreferences.Editor spEditorForCount;

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_battery_status);

        //TODO:For finding ID's
        inItComponents();
        //TODO:Main Function
        batteryStatusFeature();
    }

    private void batteryStatusFeature() {
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:CHARGER REMOVED MODE SWITCH
        SharedPreferences sp7 = getSharedPreferences("Swch7 State", Context.MODE_PRIVATE);
        batteryStatusSwitch.setChecked(sp7.getBoolean("Swch7 State", false));
        switchStatus=sp7.getBoolean("Swch7 State", false);

        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }

        //TODO: Code for BATTERY STATUS MODE on Switch
        batteryStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent goToBatteryStatus = new Intent(BatteryStatusMode.this, CodeSettingForBatteryStatus.class);
                    startActivityForResult(goToBatteryStatus,BTN_CLICKED_CODE);
                }
                else{

                    stopService(new Intent(BatteryStatusMode.this, SmsReaderServiceForBatteryStatus.class));
                    //getContext().unregisterReceiver(mListener);
                    displayToast("Battery Status Mode is Disabled");
                    SharedPreferences.Editor editor4 = getSharedPreferences("Swch7 State",Context.MODE_PRIVATE).edit();
                    editor4.putBoolean("Swch7 State", false);
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
        batteryStatusSwitch=(Switch)findViewById(R.id.swh_option7);
        txtOnOff=(TextView)findViewById(R.id.txt_on_off);

    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onPause();
    }
    public void displayToast(String message) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:BATTERY STATUS MODE SWITCH
        SharedPreferences sp7 = getSharedPreferences("Swch7 State", Context.MODE_PRIVATE);
        batteryStatusSwitch.setChecked(sp7.getBoolean("Swch7 State", false));
        switchStatus=sp7.getBoolean("Swch7 State", false);
        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }
    }
    public void backImageClick(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("BATTERY_STATUS_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
    @Override
    public void onBackPressed() {
        //Log.d("CDA", "onBackPressed Called");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("BATTERY_STATUS_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case BTN_CLICKED_CODE:
                if(resultCode == Activity.RESULT_OK){
                    Boolean savedClick=data.getBooleanExtra("CODE_SAVED",false);
                    if(savedClick){
                        SharedPreferences.Editor editor4 =getSharedPreferences("Swch7 State", Context.MODE_PRIVATE).edit();
                        editor4.putBoolean("Swch7 State", true);
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

                            startForegroundService(new Intent(BatteryStatusMode.this, SmsReaderServiceForBatteryStatus.class));
                        } else {
                            startService(new Intent(BatteryStatusMode.this, SmsReaderServiceForBatteryStatus.class));
                        }
                    }
                    else {
                        batteryStatusSwitch.setChecked(false);
                        SharedPreferences.Editor editor4 = getSharedPreferences("Swch7 State",Context.MODE_PRIVATE).edit();
                        editor4.putBoolean("Swch7 State", false);
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
}
