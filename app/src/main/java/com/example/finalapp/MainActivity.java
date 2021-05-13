package com.example.finalapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.finalapp.ClassesForBatteryStatusMode.BatteryStatusMode;
import com.example.finalapp.ClassesForChargingSenseMode.ChargerSenseMode;
import com.example.finalapp.ClassesForGpsLocaitonMode.GpsLocationMode;
import com.example.finalapp.ClassesForHeadphoneSenseMode.HeadphoneSenseMode;
import com.example.finalapp.ClassesForMotionSenseMode.MotionSenseMode;
import com.example.finalapp.ClassesForMotionSenseMode.ServiceForMotionSensor;
import com.example.finalapp.ClassesForPocketSenseMode.PocketSenseMode;
import com.example.finalapp.ClassesForRingAlarmMode.RingAlarmMode;
import com.example.finalapp.ClassesForTorchMode.TorchMode;


public class MainActivity extends Activity{

    private static final int CHARGING_SENSE_CODE=10;
    private static final int HEADPHONE_SENSE_CODE=20;
    private static final int POCKET_SENSE_CODE=30;
    private static final int MOTION_SENSE_CODE=40;
    private static final int RING_ALARM_CODE=50;
    private static final int GPS_LOCATION_CODE=60;
    private static final int BATTERY_STATUS_MODE=70;
    private static final int TORCH_MODE_CODE=80;
    public static int count=0;

    LinearLayout layoutOfChargingMode,layoutOfHeadphoneMode,layoutOfPocketSense,
                 layoutOfMotionSense,layoutOfRingAlarm,layoutOfGpsLocation,
                 layoutOfBatteryStatus,layoutOfTorchMode;
    ImageView glow10,glow20,glow30,glow40,glow50,glow60,glow70,glow80,glow90,glow100;
    ImageView secure0,secure10,secure20,secure30,secure40,secure50,secure60,secure70,
              secure80,secure100;
    ImageView chargingOn,chargingOff,headphoneOn,headphoneOff,pocketSenseOn,pocketSenseOff,motionSenseOn,motionSenseOff,
              ringAlarmOn,ringAlarmOff,gpsLocationOn,gpsLocationOff,batteryStatusOn,batteryStatusOff,torchOn,torchOff;

    Boolean chargingSenseSwitchStatus,headphoneSenseSwitchStatus,motionSenseSwitchStatus,
            pocketSenseSwitchStatus,batteryStatusSwitchStatus,gpsLocationSwitchStatus,
            torchModeSwitchStatus,ringAlarmSwitchStatus;

    ImageView imgChargingMode,imgHeadphoneMode,imgPocketSense,imgMotionSense,imgRingAlarm,
              imgGpsLocation,imgBatteryStatus,imgTorchMode;
    SharedPreferences spForCount;
    BottomNavigationView bNevView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bNevView=(BottomNavigationView)findViewById(R.id.bottom_nev);
        bNevView.setSelectedItemId(R.id.menu_home);
        inItComponents();
        gettingPreferences();

        ////

        bNevView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_admin:
                        startActivity(new Intent(MainActivity.this,AdminActivity.class));
                        break;
                    case R.id.menu_home:
                        //startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        break;
                    case R.id.menu_help:
                        startActivity(new Intent(MainActivity.this,Help.class));
                        break;
                }
                return true;
            }
        });
    }

    private void gettingPreferences() {
        spForCount = getSharedPreferences("countValue", Context.MODE_PRIVATE);
        count=spForCount.getInt("countValue",0);

        //TODO:CHARGER REMOVED MODE SWITCH
        SharedPreferences sp1 = getSharedPreferences("Swch1 State", Context.MODE_PRIVATE);
        chargingSenseSwitchStatus =sp1.getBoolean("Swch1 State", false);

        //TODO:CHARGER REMOVED MODE SWITCH
        if(chargingSenseSwitchStatus){
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgChargingMode.getDrawable()),
                    ContextCompat.getColor(this, R.color.green)
            );
                countCasesFunction(count);
        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgChargingMode.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }

        //TODO:HEADPHONE REMOVED MODE SWITCH
        SharedPreferences sp2 = getSharedPreferences("Swch2 State", Context.MODE_PRIVATE);
        headphoneSenseSwitchStatus =sp2.getBoolean("Swch2 State", false);
        //TODO:HEADPHONE REMOVED MODE SWITCH
        if(headphoneSenseSwitchStatus){
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgHeadphoneMode.getDrawable()),
                    ContextCompat.getColor(this, R.color.green)
            );
                countCasesFunction(count);
        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgHeadphoneMode.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }

        //TODO:POCKET SENSE MODE SWITCH
        SharedPreferences sp3 = getSharedPreferences("Swch3 State", Context.MODE_PRIVATE);
        pocketSenseSwitchStatus =sp3.getBoolean("Swch3 State", false);
        //TODO:POCKET REMOVED MODE SWITCH
        /*if(pocketSenseSwitchStatus){
            if(pocketSenseOff.getVisibility()==View.VISIBLE){
                pocketSenseOn.setVisibility(View.VISIBLE);
                pocketSenseOff.setVisibility(View.GONE);
                countCasesFunction(count);
            }
        }
        else{*/
           /* pocketSenseOn.setVisibility(View.GONE);
            pocketSenseOff.setVisibility(View.VISIBLE);*/
            //countCasesFunction(count);
        //}

        /*//TODO:MOTION SENSE MODE SWITCH
        SharedPreferences sp4 = getSharedPreferences("Swch4 State", Context.MODE_PRIVATE);
        motionSenseSwitchStatus =sp4.getBoolean("Swch4 State", false);
        //TODO:MOTION REMOVED MODE SWITCH
        if(motionSenseSwitchStatus){
            if(motionSenseOff.getVisibility()==View.VISIBLE){
                motionSenseOn.setVisibility(View.VISIBLE);
                motionSenseOff.setVisibility(View.GONE);
                countCasesFunction(count);
            }
        }
        else{

        */

           /* motionSenseOn.setVisibility(View.GONE);
            motionSenseOff.setVisibility(View.VISIBLE);*/
              //countCasesFunction(count);
        //}
        //TODO:RING ALARM MODE SWITCH
        SharedPreferences sp5 = getSharedPreferences("Swch5 State", Context.MODE_PRIVATE);
        ringAlarmSwitchStatus =sp5.getBoolean("Swch5 State", false);
        //TODO:RING ALARM MODE SWITCH
        if(ringAlarmSwitchStatus){
                DrawableCompat.setTint(
                        DrawableCompat.wrap(imgRingAlarm.getDrawable()),
                        ContextCompat.getColor(this, R.color.green)
                );
                countCasesFunction(count);
        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgRingAlarm.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }

        //TODO:GPS LOCATION MODE SWITCH
        SharedPreferences sp6 = getSharedPreferences("Swch6 State", Context.MODE_PRIVATE);
        gpsLocationSwitchStatus =sp6.getBoolean("Swch6 State", false);
        //TODO:GPS LOCATION MODE SWITCH
        if(gpsLocationSwitchStatus){
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgGpsLocation.getDrawable()),
                    ContextCompat.getColor(this, R.color.green)
            );
                countCasesFunction(count);
        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgGpsLocation.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }

        //TODO:BATTERY STATUS MODE SWITCH
        SharedPreferences sp7 = getSharedPreferences("Swch7 State", Context.MODE_PRIVATE);
        batteryStatusSwitchStatus =sp7.getBoolean("Swch7 State", false);
        //TODO:BATTERY STATUS MODE SWITCH
        if(batteryStatusSwitchStatus){
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgBatteryStatus.getDrawable()),
                    ContextCompat.getColor(this, R.color.green)
            );
                countCasesFunction(count);
        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgBatteryStatus.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }
        //TODO:TORCH MODE MODE SWITCH
        SharedPreferences sp8 = getSharedPreferences("Swch8 State", Context.MODE_PRIVATE);
        torchModeSwitchStatus =sp8.getBoolean("Swch8 State", false);
        //TODO:TORCH MODE MODE SWITCH
        if(torchModeSwitchStatus){
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgTorchMode.getDrawable()),
                    ContextCompat.getColor(this, R.color.green)
            );
                countCasesFunction(count);
        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgBatteryStatus.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        bNevView=(BottomNavigationView)findViewById(R.id.bottom_nev);
        bNevView.setSelectedItemId(R.id.menu_home);
        spForCount = getSharedPreferences("countValue", Context.MODE_PRIVATE);
        count=spForCount.getInt("countValue",0);

        //TODO:CHARGER REMOVED MODE SWITCH
        SharedPreferences sp1 = getSharedPreferences("Swch1 State", Context.MODE_PRIVATE);
        chargingSenseSwitchStatus =sp1.getBoolean("Swch1 State", false);
        if(chargingSenseSwitchStatus){
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgChargingMode.getDrawable()),
                    ContextCompat.getColor(this, R.color.green)
            );
                countCasesFunction(count);
        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgChargingMode.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }

        //TODO:HEADPHONE REMOVED MODE SWITCH
        SharedPreferences sp2 = getSharedPreferences("Swch2 State", Context.MODE_PRIVATE);
        headphoneSenseSwitchStatus =sp2.getBoolean("Swch2 State", false);
        //TODO:HEADPHONE REMOVED MODE SWITCH
        if(headphoneSenseSwitchStatus){
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgHeadphoneMode.getDrawable()),
                    ContextCompat.getColor(this, R.color.green)
            );
                countCasesFunction(count);

        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgHeadphoneMode.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }

        //TODO:POCKET SENSE MODE SWITCH
        SharedPreferences sp3 = getSharedPreferences("Swch3 State", Context.MODE_PRIVATE);
        pocketSenseSwitchStatus =sp3.getBoolean("Swch3 State", false);
        /*//TODO:POCKET REMOVED MODE SWITCH
        if(pocketSenseSwitchStatus){
            if(pocketSenseOff.getVisibility()==View.VISIBLE){
                pocketSenseOn.setVisibility(View.VISIBLE);
                pocketSenseOff.setVisibility(View.GONE);
                countCasesFunction(count);
            }
        }
        else{*/
           /* pocketSenseOn.setVisibility(View.GONE);
            pocketSenseOff.setVisibility(View.VISIBLE);*/
        /*    countCasesFunction(count);
        }*/

        //TODO:MOTION SENSE MODE SWITCH
        SharedPreferences sp4 = getSharedPreferences("Swch4 State", Context.MODE_PRIVATE);
        motionSenseSwitchStatus =sp4.getBoolean("Swch4 State", false);
        /*//TODO:HEADPHONE REMOVED MODE SWITCH
        if(motionSenseSwitchStatus){
            if(motionSenseOff.getVisibility()==View.VISIBLE){
                motionSenseOn.setVisibility(View.VISIBLE);
                motionSenseOff.setVisibility(View.GONE);
                countCasesFunction(count);
            }
        }
        else{*/

           /* motionSenseOn.setVisibility(View.GONE);
            motionSenseOff.setVisibility(View.VISIBLE);*/
            /*countCasesFunction(count);
        }*/
        //TODO:RING ALARM MODE SWITCH
        SharedPreferences sp5 = getSharedPreferences("Swch5 State", Context.MODE_PRIVATE);
        ringAlarmSwitchStatus =sp5.getBoolean("Swch5 State", false);
        //TODO:RING ALARM MODE SWITCH
        if(ringAlarmSwitchStatus){

                DrawableCompat.setTint(
                        DrawableCompat.wrap(imgRingAlarm.getDrawable()),
                        ContextCompat.getColor(this, R.color.green)
                );
                countCasesFunction(count);
        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgRingAlarm.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }
        //TODO:GPS LOCATION MODE SWITCH
        SharedPreferences sp6 = getSharedPreferences("Swch6 State", Context.MODE_PRIVATE);
        gpsLocationSwitchStatus =sp6.getBoolean("Swch6 State", false);
        //TODO:GPS LOCATION MODE SWITCH
        if(gpsLocationSwitchStatus){
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgGpsLocation.getDrawable()),
                    ContextCompat.getColor(this, R.color.green)
            );
                countCasesFunction(count);
        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgGpsLocation.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }

        //TODO:BATTERY STATUS MODE SWITCH
        SharedPreferences sp7 = getSharedPreferences("Swch7 State", Context.MODE_PRIVATE);
        batteryStatusSwitchStatus =sp7.getBoolean("Swch7 State", false);
        //TODO:BATTERY STATUS MODE SWITCH
        if(batteryStatusSwitchStatus){
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgBatteryStatus.getDrawable()),
                    ContextCompat.getColor(this, R.color.green)
            );
                countCasesFunction(count);
        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgBatteryStatus.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }
        //TODO:TORCH MODE MODE SWITCH
        SharedPreferences sp8 = getSharedPreferences("Swch8 State", Context.MODE_PRIVATE);
        torchModeSwitchStatus =sp8.getBoolean("Swch8 State", false);
        //TODO:TORCH MODE MODE SWITCH
        if(torchModeSwitchStatus){
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgTorchMode.getDrawable()),
                    ContextCompat.getColor(this, R.color.green)
            );
                countCasesFunction(count);
        }
        else{
            DrawableCompat.setTint(
                    DrawableCompat.wrap(imgTorchMode.getDrawable()),
                    ContextCompat.getColor(this, R.color.red)
            );
            countCasesFunction(count);
        }

    }


    public void onChargingModeClick(View view) {

        Intent intent=new Intent(this, ChargerSenseMode.class);
        startActivityForResult(intent,CHARGING_SENSE_CODE);
    }
    public void onHeadphoneClick(View view) {

        Intent intent=new Intent(this, HeadphoneSenseMode.class);
        startActivityForResult(intent,HEADPHONE_SENSE_CODE);
    }
    public void onPocketSenseClick(View view) {

        Intent intent=new Intent(this, PocketSenseMode.class);
        startActivityForResult(intent,POCKET_SENSE_CODE);

    }
    public void onMotionSenseClick(View view) {

        Intent intent=new Intent(this, MotionSenseMode.class);
        startActivityForResult(intent,MOTION_SENSE_CODE);

    }
    public void onRingMobileClick(View view) {

        Intent intent=new Intent(this, RingAlarmMode.class);
        startActivityForResult(intent,RING_ALARM_CODE);
    }
    public void onGpsLocationClick(View view) {

        Intent intent=new Intent(this, GpsLocationMode.class);
        startActivityForResult(intent,GPS_LOCATION_CODE);
    }
    public void onBatteryStatusClick(View view) {

        Intent intent=new Intent(this, BatteryStatusMode.class);
        startActivityForResult(intent,BATTERY_STATUS_MODE);

    }
    public void onTorchModeClick(View view) {

        Intent intent=new Intent(this, TorchMode.class);
        startActivityForResult(intent,TORCH_MODE_CODE);
    }



    private void inItComponents() {


        //TODO:Images of Features
        imgChargingMode=findViewById(R.id.img_charging_mode);
        imgHeadphoneMode=findViewById(R.id.img_headphone_mode);
        imgPocketSense=findViewById(R.id.img_pocket_sense);
        imgMotionSense=findViewById(R.id.img_motion_sense);
        imgRingAlarm=findViewById(R.id.img_ring_phone);
        imgGpsLocation=findViewById(R.id.img_gps_location);
        imgBatteryStatus=findViewById(R.id.img_battery_status);
        imgTorchMode=findViewById(R.id.img_torch);

        //TODO: Layouts Ids
        layoutOfChargingMode=findViewById(R.id.linear_charging_mode);
        layoutOfHeadphoneMode=findViewById(R.id.linear_headphone_mode);
        layoutOfPocketSense=findViewById(R.id.linear_pocket_sense);
        layoutOfMotionSense=findViewById(R.id.linear_motion_sense);
        layoutOfRingAlarm=findViewById(R.id.linear_ring_phone);
        layoutOfGpsLocation=findViewById(R.id.linear_gps_location);
        layoutOfBatteryStatus=findViewById(R.id.linear_battery_status);
        layoutOfTorchMode=findViewById(R.id.linear_torch);

        //TODO: Glow Ids
       /* glow10=findViewById(R.id.glow10);
        glow20=findViewById(R.id.glow20);
        glow30=findViewById(R.id.glow30);
        glow40=findViewById(R.id.glow40);
        glow50=findViewById(R.id.glow50);
        glow60=findViewById(R.id.glow60);
        glow70=findViewById(R.id.glow70);
        glow80=findViewById(R.id.glow80);
        glow90=findViewById(R.id.glow90);
        glow100=findViewById(R.id.glow100);*/

        //TODO: Security % Ids
        secure0=findViewById(R.id.secure0);
        secure20=findViewById(R.id.secure20);
        secure30=findViewById(R.id.secure30);
        secure40=findViewById(R.id.secure40);
        secure50=findViewById(R.id.secure50);
        secure60=findViewById(R.id.secure60);
        secure70=findViewById(R.id.secure70);
        secure80=findViewById(R.id.secure80);
        secure100=findViewById(R.id.secure100);
        secure10=findViewById(R.id.secure10);

      /*  //TODO: ON/OFF Signals
        chargingOn=findViewById(R.id.charging_on);
        chargingOff=findViewById(R.id.charging_off);

        headphoneOn=findViewById(R.id.headphone_on);
        headphoneOff=findViewById(R.id.headphone_off);

        pocketSenseOn=findViewById(R.id.pocket_sense_on);
        pocketSenseOff=findViewById(R.id.pocket_sense_off);

        motionSenseOn=findViewById(R.id.motion_sense_on);
        motionSenseOff=findViewById(R.id.motion_sense_off);

        ringAlarmOn=findViewById(R.id.ring_alarm_on);
        ringAlarmOff=findViewById(R.id.ring_alarm_off);

        gpsLocationOn=findViewById(R.id.gps_location_on);
        gpsLocationOff=findViewById(R.id.gps_location_off);

        batteryStatusOn=findViewById(R.id.battery_status_on);
        batteryStatusOff=findViewById(R.id.battery_status_off);

        torchOn=findViewById(R.id.torch_on);
        torchOff=findViewById(R.id.torch_off);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHARGING_SENSE_CODE:
                if(resultCode == Activity.RESULT_OK){
                    Boolean switchStatus=data.getBooleanExtra("CHARGING_SENSE_SWITCH_STATUS",false);

                    //TODO:CHARGER REMOVED MODE SWITCH
                     if(switchStatus){
                         DrawableCompat.setTint(
                                 DrawableCompat.wrap(imgChargingMode.getDrawable()),
                                 ContextCompat.getColor(this, R.color.green)
                         );
                     }
                     else{
                         DrawableCompat.setTint(
                                 DrawableCompat.wrap(imgChargingMode.getDrawable()),
                                 ContextCompat.getColor(this, R.color.red)
                         );
                }
                }
                break;
            case HEADPHONE_SENSE_CODE:
                if(resultCode == Activity.RESULT_OK){
                    Boolean switchStatus=data.getBooleanExtra("HEADPHONE_SENSE_SWITCH_STATUS",false);

                    //TODO:HEADPHONE REMOVED MODE SWITCH
                    if(switchStatus){
                        if(switchStatus){
                            DrawableCompat.setTint(
                                    DrawableCompat.wrap(imgHeadphoneMode.getDrawable()),
                                    ContextCompat.getColor(this, R.color.green)
                            );
                        }
                        else{
                            DrawableCompat.setTint(
                                    DrawableCompat.wrap(imgHeadphoneMode.getDrawable()),
                                    ContextCompat.getColor(this, R.color.red)
                            );
                    // String result=data.getStringExtra("result");
                }
                    }
                }
                break;

            case POCKET_SENSE_CODE:
                if(resultCode == Activity.RESULT_OK){
                    Boolean switchStatus=data.getBooleanExtra("POCKET_SENSE_SWITCH_STATUS",false);

                    //TODO:POCKET SENSE MODE SWITCH
                    if(switchStatus){
                        if(switchStatus){
                            DrawableCompat.setTint(
                                    DrawableCompat.wrap(imgPocketSense.getDrawable()),
                                    ContextCompat.getColor(this, R.color.green)
                            );
                        }
                        else{
                            DrawableCompat.setTint(
                                    DrawableCompat.wrap(imgPocketSense.getDrawable()),
                                    ContextCompat.getColor(this, R.color.red)
                            );
                            // String result=data.getStringExtra("result");
                        }
                    }
                }
                break;

            case MOTION_SENSE_CODE:
                if(resultCode == Activity.RESULT_OK){
                    Boolean switchStatus=data.getBooleanExtra("MOTION_SENSE_SWITCH_STATUS",false);

                    //TODO:MOTION SENSE MODE SWITCH
                    if(switchStatus){
                        if(switchStatus){
                            DrawableCompat.setTint(
                                    DrawableCompat.wrap(imgMotionSense.getDrawable()),
                                    ContextCompat.getColor(this, R.color.green)
                            );
                        }
                        else{
                            DrawableCompat.setTint(
                                    DrawableCompat.wrap(imgMotionSense.getDrawable()),
                                    ContextCompat.getColor(this, R.color.red)
                            );
                            // String result=data.getStringExtra("result");
                        }
                    }
                }
                break;

            case RING_ALARM_CODE:
                if(resultCode == Activity.RESULT_OK){
                    Boolean switchStatus=data.getBooleanExtra("RING_ALARM_SWITCH_STATUS",false);

                    //TODO:RING ALARM MODE SWITCH
                    if(switchStatus){
                        if(switchStatus){
                            if(switchStatus){
                                DrawableCompat.setTint(
                                        DrawableCompat.wrap(imgRingAlarm.getDrawable()),
                                        ContextCompat.getColor(this, R.color.green)
                                );
                            }
                            else{
                                DrawableCompat.setTint(
                                        DrawableCompat.wrap(imgRingAlarm.getDrawable()),
                                        ContextCompat.getColor(this, R.color.red)
                                );
                                // String result=data.getStringExtra("result");
                            }
                        }
                    }
                }
                break;

            case GPS_LOCATION_CODE:
                if(resultCode == Activity.RESULT_OK){
                    Boolean switchStatus=data.getBooleanExtra("GPS_LOCATION_SWITCH_STATUS",false);

                    //TODO:GPS LOCATION MODE SWITCH
                    if(switchStatus){
                        if(switchStatus){
                            DrawableCompat.setTint(
                                    DrawableCompat.wrap(imgGpsLocation.getDrawable()),
                                    ContextCompat.getColor(this, R.color.green)
                            );
                        }
                        else{
                            DrawableCompat.setTint(
                                    DrawableCompat.wrap(imgGpsLocation.getDrawable()),
                                    ContextCompat.getColor(this, R.color.red)
                            );
                            // String result=data.getStringExtra("result");
                        }
                    }
                }
                break;

            case BATTERY_STATUS_MODE:
                if(resultCode == Activity.RESULT_OK){
                    Boolean switchStatus=data.getBooleanExtra("BATTERY_STATUS_SWITCH_STATUS",false);

                    //TODO:BATTERY STATUS MODE SWITCH
                    if(switchStatus){
                        if(switchStatus){
                            if(switchStatus){
                                DrawableCompat.setTint(
                                        DrawableCompat.wrap(imgBatteryStatus.getDrawable()),
                                        ContextCompat.getColor(this, R.color.green)
                                );
                            }
                            else{
                                DrawableCompat.setTint(
                                        DrawableCompat.wrap(imgBatteryStatus.getDrawable()),
                                        ContextCompat.getColor(this, R.color.red)
                                );
                                // String result=data.getStringExtra("result");
                            }
                        }
                    }
                }
                break;

            case TORCH_MODE_CODE:
                if(resultCode == Activity.RESULT_OK){

                    Boolean switchStatus=data.getBooleanExtra("TORCH_MODE_SWITCH_STATUS",false);

                    //TODO:TORCH MODE MODE SWITCH
                    if(switchStatus){
                        if(switchStatus){
                            if(switchStatus){
                                DrawableCompat.setTint(
                                        DrawableCompat.wrap(imgTorchMode.getDrawable()),
                                        ContextCompat.getColor(this, R.color.green)
                                );
                            }
                            else{
                                DrawableCompat.setTint(
                                        DrawableCompat.wrap(imgTorchMode.getDrawable()),
                                        ContextCompat.getColor(this, R.color.red)
                                );
                                // String result=data.getStringExtra("result");
                            }
                        }
                    }
                }
                break;

        }
        }
    //TODO: 0% SECURITY GLOW AND TEXT
    public void zeroPercentSecure(){

        secure0.setVisibility(View.VISIBLE);

        secure10.setVisibility(View.GONE);
        secure20.setVisibility(View.GONE);
        secure30.setVisibility(View.GONE);
        secure40.setVisibility(View.GONE);
        secure50.setVisibility(View.GONE);
        secure60.setVisibility(View.GONE);
        secure70.setVisibility(View.GONE);
        secure80.setVisibility(View.GONE);
        secure100.setVisibility(View.GONE);

       /* glow10.setVisibility(View.GONE);
        glow20.setVisibility(View.GONE);
        glow30.setVisibility(View.GONE);
        glow40.setVisibility(View.GONE);
        glow50.setVisibility(View.GONE);
        glow60.setVisibility(View.GONE);
        glow70.setVisibility(View.GONE);
        glow80.setVisibility(View.GONE);
        glow90.setVisibility(View.GONE);
        glow100.setVisibility(View.GONE);
*/
    }
        //TODO: 10% SECURITY GLOW AND TEXT
        public void tenPercentSecure(){

                secure10.setVisibility(View.VISIBLE);

                secure0.setVisibility(View.GONE);
                secure20.setVisibility(View.GONE);
                secure30.setVisibility(View.GONE);
                secure40.setVisibility(View.GONE);
                secure50.setVisibility(View.GONE);
                secure60.setVisibility(View.GONE);
                secure70.setVisibility(View.GONE);
                secure80.setVisibility(View.GONE);
                secure100.setVisibility(View.GONE);

               /* glow20.setVisibility(View.GONE);
                glow30.setVisibility(View.GONE);
                glow40.setVisibility(View.GONE);
                glow50.setVisibility(View.GONE);
                glow60.setVisibility(View.GONE);
                glow70.setVisibility(View.GONE);
                glow80.setVisibility(View.GONE);
                glow90.setVisibility(View.GONE);
                glow100.setVisibility(View.GONE);*/

        }
    //TODO: 20% SECURITY GLOW AND TEXT
    public void twentyPercentSecure(){

        //glow20.setVisibility(View.VISIBLE);
        secure20.setVisibility(View.VISIBLE);

        secure0.setVisibility(View.GONE);
        secure10.setVisibility(View.GONE);
        secure30.setVisibility(View.GONE);
        secure40.setVisibility(View.GONE);
        secure50.setVisibility(View.GONE);
        secure60.setVisibility(View.GONE);
        secure70.setVisibility(View.GONE);
        secure80.setVisibility(View.GONE);
        secure100.setVisibility(View.GONE);

       /* glow10.setVisibility(View.GONE);
        glow30.setVisibility(View.GONE);
        glow40.setVisibility(View.GONE);
        glow50.setVisibility(View.GONE);
        glow60.setVisibility(View.GONE);
        glow70.setVisibility(View.GONE);
        glow80.setVisibility(View.GONE);
        glow90.setVisibility(View.GONE);
        glow100.setVisibility(View.GONE);*/

    }
    //TODO: 30% SECURITY GLOW AND TEXT
    public void thirtyPercentSecure(){

//        glow30.setVisibility(View.VISIBLE);
        secure30.setVisibility(View.VISIBLE);

        secure0.setVisibility(View.GONE);
        secure20.setVisibility(View.GONE);
        secure10.setVisibility(View.GONE);
        secure40.setVisibility(View.GONE);
        secure50.setVisibility(View.GONE);
        secure60.setVisibility(View.GONE);
        secure70.setVisibility(View.GONE);
        secure80.setVisibility(View.GONE);
        secure100.setVisibility(View.GONE);

        /*glow20.setVisibility(View.GONE);
        glow10.setVisibility(View.GONE);
        glow40.setVisibility(View.GONE);
        glow50.setVisibility(View.GONE);
        glow60.setVisibility(View.GONE);
        glow70.setVisibility(View.GONE);
        glow80.setVisibility(View.GONE);
        glow90.setVisibility(View.GONE);
        glow100.setVisibility(View.GONE);*/

    }
    //TODO: 40% SECURITY GLOW AND TEXT
    public void fourtyPercentSecure(){

        //glow40.setVisibility(View.VISIBLE);
        secure40.setVisibility(View.VISIBLE);

        secure0.setVisibility(View.GONE);
        secure20.setVisibility(View.GONE);
        secure30.setVisibility(View.GONE);
        secure10.setVisibility(View.GONE);
        secure50.setVisibility(View.GONE);
        secure60.setVisibility(View.GONE);
        secure70.setVisibility(View.GONE);
        secure80.setVisibility(View.GONE);
        //secure90.setVisibility(View.GONE);
        secure100.setVisibility(View.GONE);

       /* glow20.setVisibility(View.GONE);
        glow30.setVisibility(View.GONE);
        glow10.setVisibility(View.GONE);
        glow50.setVisibility(View.GONE);
        glow60.setVisibility(View.GONE);
        glow70.setVisibility(View.GONE);
        glow80.setVisibility(View.GONE);
        glow90.setVisibility(View.GONE);
        glow100.setVisibility(View.GONE);*/

    }
    //TODO: 50% SECURITY GLOW AND TEXT
    public void fiftyPercentSecure(){

        //glow50.setVisibility(View.VISIBLE);
        secure50.setVisibility(View.VISIBLE);

        secure0.setVisibility(View.GONE);
        secure20.setVisibility(View.GONE);
        secure30.setVisibility(View.GONE);
        secure40.setVisibility(View.GONE);
        secure10.setVisibility(View.GONE);
        secure60.setVisibility(View.GONE);
        secure70.setVisibility(View.GONE);
        secure80.setVisibility(View.GONE);
        //secure90.setVisibility(View.GONE);
        secure100.setVisibility(View.GONE);

        /*glow20.setVisibility(View.GONE);
        glow30.setVisibility(View.GONE);
        glow40.setVisibility(View.GONE);
        glow10.setVisibility(View.GONE);
        glow60.setVisibility(View.GONE);
        glow70.setVisibility(View.GONE);
        glow80.setVisibility(View.GONE);
        glow90.setVisibility(View.GONE);
        glow100.setVisibility(View.GONE);*/

    }
    //TODO: 60% SECURITY GLOW AND TEXT
    public void sixtyPercentSecure(){

       // glow60.setVisibility(View.VISIBLE);
        secure60.setVisibility(View.VISIBLE);

        secure0.setVisibility(View.GONE);
        secure20.setVisibility(View.GONE);
        secure30.setVisibility(View.GONE);
        secure40.setVisibility(View.GONE);
        secure50.setVisibility(View.GONE);
        secure10.setVisibility(View.GONE);
        secure70.setVisibility(View.GONE);
        secure80.setVisibility(View.GONE);
       // secure90.setVisibility(View.GONE);
        secure100.setVisibility(View.GONE);

       /* glow20.setVisibility(View.GONE);
        glow30.setVisibility(View.GONE);
        glow40.setVisibility(View.GONE);
        glow50.setVisibility(View.GONE);
        glow10.setVisibility(View.GONE);
        glow70.setVisibility(View.GONE);
        glow80.setVisibility(View.GONE);
        glow90.setVisibility(View.GONE);
        glow100.setVisibility(View.GONE);*/

    }
    //TODO: 70% SECURITY GLOW AND TEXT
    public void seventyPercentSecure(){

        //glow70.setVisibility(View.VISIBLE);
        secure70.setVisibility(View.VISIBLE);

        secure0.setVisibility(View.GONE);
        secure20.setVisibility(View.GONE);
        secure30.setVisibility(View.GONE);
        secure40.setVisibility(View.GONE);
        secure50.setVisibility(View.GONE);
        secure60.setVisibility(View.GONE);
        secure10.setVisibility(View.GONE);
        secure80.setVisibility(View.GONE);
        //secure90.setVisibility(View.GONE);
        secure100.setVisibility(View.GONE);

        /*glow20.setVisibility(View.GONE);
        glow30.setVisibility(View.GONE);
        glow40.setVisibility(View.GONE);
        glow50.setVisibility(View.GONE);
        glow60.setVisibility(View.GONE);
        glow10.setVisibility(View.GONE);
        glow80.setVisibility(View.GONE);
        glow90.setVisibility(View.GONE);
        glow100.setVisibility(View.GONE);*/

    }
    //TODO: 80% SECURITY GLOW AND TEXT
    public void eightyPercentSecure(){

       // glow80.setVisibility(View.VISIBLE);
        secure80.setVisibility(View.VISIBLE);

        secure0.setVisibility(View.GONE);
        secure20.setVisibility(View.GONE);
        secure30.setVisibility(View.GONE);
        secure40.setVisibility(View.GONE);
        secure50.setVisibility(View.GONE);
        secure60.setVisibility(View.GONE);
        secure70.setVisibility(View.GONE);
        secure10.setVisibility(View.GONE);
        //secure90.setVisibility(View.GONE);
        secure100.setVisibility(View.GONE);

        /*glow20.setVisibility(View.GONE);
        glow30.setVisibility(View.GONE);
        glow40.setVisibility(View.GONE);
        glow50.setVisibility(View.GONE);
        glow60.setVisibility(View.GONE);
        glow70.setVisibility(View.GONE);
        glow10.setVisibility(View.GONE);
        glow90.setVisibility(View.GONE);
        glow100.setVisibility(View.GONE);*/

    }
    //TODO: 90% SECURITY GLOW AND TEXT
    public void nightyPercentSecure(){

       /* glow90.setVisibility(View.VISIBLE);
        secure90.setVisibility(View.VISIBLE);

        secure0.setVisibility(View.GONE);
        secure20.setVisibility(View.GONE);
        secure30.setVisibility(View.GONE);
        secure40.setVisibility(View.GONE);
        secure50.setVisibility(View.GONE);
        secure60.setVisibility(View.GONE);
        secure70.setVisibility(View.GONE);
        secure80.setVisibility(View.GONE);
        secure10.setVisibility(View.GONE);
        secure100.setVisibility(View.GONE);

        glow20.setVisibility(View.GONE);
        glow30.setVisibility(View.GONE);
        glow40.setVisibility(View.GONE);
        glow50.setVisibility(View.GONE);
        glow60.setVisibility(View.GONE);
        glow70.setVisibility(View.GONE);
        glow80.setVisibility(View.GONE);
        glow10.setVisibility(View.GONE);
        glow100.setVisibility(View.GONE);
*/
    }
    //TODO: 100% SECURITY GLOW AND TEXT
    public void hundredPercentSecure(){

        glow100.setVisibility(View.VISIBLE);
        secure100.setVisibility(View.VISIBLE);

        secure0.setVisibility(View.GONE);
        secure20.setVisibility(View.GONE);
        secure30.setVisibility(View.GONE);
        secure40.setVisibility(View.GONE);
        secure50.setVisibility(View.GONE);
        secure60.setVisibility(View.GONE);
        secure70.setVisibility(View.GONE);
        secure80.setVisibility(View.GONE);
        //secure90.setVisibility(View.GONE);
        secure10.setVisibility(View.GONE);

        /*glow20.setVisibility(View.GONE);
        glow30.setVisibility(View.GONE);
        glow40.setVisibility(View.GONE);
        glow50.setVisibility(View.GONE);
        glow60.setVisibility(View.GONE);
        glow70.setVisibility(View.GONE);
        glow80.setVisibility(View.GONE);
        glow90.setVisibility(View.GONE);
        glow10.setVisibility(View.GONE);*/

    }
    public void countCasesFunction(int x){
        switch(x){
            case 0:
                zeroPercentSecure();
                break;
            case 1:
                tenPercentSecure();
                break;
            case 2:
                twentyPercentSecure();
                break;
            case 3:
                thirtyPercentSecure();
                break;
            case 4:
                fiftyPercentSecure();
                break;
            case 5:
                sixtyPercentSecure();
                break;
            case 6:
                eightyPercentSecure();
                break;
            case 7:
                nightyPercentSecure();
                break;
            case 8:
                hundredPercentSecure();
                break;
        }
    }
}
