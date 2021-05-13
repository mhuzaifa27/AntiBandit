package com.example.finalapp.ClassesForChargingSenseMode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.MainActivity;
import com.example.finalapp.R;
import com.example.finalapp.RingSirenService;

public class ChargerSenseMode extends Activity {

    int chargePlug;
    Boolean switchStatus=false;
    Switch chargerSenseSwitch;
    Toast toast;
    TextView txtOnOff;
    SharedPreferences spForCount;
    SharedPreferences.Editor spEditorForCount;


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_charger_sense);

        //TODO:For finding ID's
        inItComponents();
        //TODO:Main Function
        chargingModeFeature();

    }

    private void chargingModeFeature() {

        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:CHARGER REMOVED MODE SWITCH
        SharedPreferences sp1 = getSharedPreferences("Swch1 State", Context.MODE_PRIVATE);
        chargerSenseSwitch.setChecked(sp1.getBoolean("Swch1 State", false));
        switchStatus=sp1.getBoolean("Swch1 State", false);

        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }

        //TODO: Code for CHARGER SENSE MODE on Switch
        chargerSenseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                Intent intent=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                if(chargePlug!=0|| isMyServiceRunning(RingSirenService.class)){
                    if(isChecked) {
                        SharedPreferences.Editor editor =getSharedPreferences("Swch1 State", Context.MODE_PRIVATE).edit();
                        editor.putBoolean("Swch1 State", true);
                        if(MainActivity.count<=8)
                        {
                            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                            spEditorForCount.putInt("countValue",++MainActivity.count);
                            spEditorForCount.commit();
                        }
                        editor.commit();
                        txtOnOff.setText("ON");
                        switchStatus=true;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService( new Intent(ChargerSenseMode.this, ChargerBroadCastRunningService.class));
                        }
                        else{
                            startService( new Intent(ChargerSenseMode.this, ChargerBroadCastRunningService.class));
                        }
                        displayToast("Charger Sense mode is Enabled");
                    }
                    else {
                        if(isMyServiceRunning(RingSirenService.class)){
                            chargerSenseSwitch.setChecked(true);
                            displayToast("Please Connect Back Charger!");

                            // Toast.makeText(getActivity(), "Please Connect Back Charger!", Toast.LENGTH_SHORT).show();

                        }

                        else{
                            //getContext().unregisterReceiver(mChargerBroadcastReceiver);
                            stopService(new Intent(ChargerSenseMode.this, ChargerBroadCastRunningService.class));
                            displayToast("Charger Sense mode is Disabled");
                            //Toast.makeText(getActivity(),"Charger Sense mode is Disabled",Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor =getSharedPreferences("Swch1 State", Context.MODE_PRIVATE).edit();
                            editor.putBoolean("Swch1 State", false);
                            if(MainActivity.count>0)
                            {
                                spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                                spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
                                spEditorForCount.commit();
                            }
                            editor.commit();
                            txtOnOff.setText("OFF");
                            switchStatus=false;
                            chargePlug=0;
                        }
                    }
                }
                else{
                    displayToast("Connect your Charger First");
                    // Toast.makeText(getActivity(),"Connect your Charger First",Toast.LENGTH_SHORT).show();
                    chargerSenseSwitch.setChecked(false);

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:CHARGER REMOVED MODE SWITCH
        SharedPreferences sp1 = getSharedPreferences("Swch1 State", Context.MODE_PRIVATE);
        chargerSenseSwitch.setChecked(sp1.getBoolean("Swch1 State", false));
        switchStatus=sp1.getBoolean("Swch1 State", false);
        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }
    }

    private void inItComponents() {
        chargerSenseSwitch=(Switch)findViewById(R.id.swh_option1);
        txtOnOff=(TextView)findViewById(R.id.txt_on_off);

    }
    public void displayToast(String message) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onPause();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void backImageClick(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("CHARGING_SENSE_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
    @Override
    public void onBackPressed() {
        //Log.d("CDA", "onBackPressed Called");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("CHARGING_SENSE_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}
