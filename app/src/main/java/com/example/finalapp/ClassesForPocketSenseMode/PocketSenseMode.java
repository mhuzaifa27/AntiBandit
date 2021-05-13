package com.example.finalapp.ClassesForPocketSenseMode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.ClassesForMotionSenseMode.MotionSenseMode;
import com.example.finalapp.ClassesForMotionSenseMode.ServiceForMotionSensor;
import com.example.finalapp.MainActivity;
import com.example.finalapp.MyAdmin;
import com.example.finalapp.R;

public class PocketSenseMode extends Activity {


    public static final int RESULT_ENABLE_FOR_PROXIMITY = 10;
    private DevicePolicyManager devicePolicyManagerForProximity;
    private ActivityManager activityManageForProximity;
    private ComponentName compName;
    AlertDialog alertDialogForProximity;
    CountDownTimer cdtForProximity;
    int proximitySwitch=0;


    Toast toast;
    Boolean switchStatus=false;
    TextView txtOnOff;
    Switch pocketSenseSwitch;
    SharedPreferences spForCount;
    SharedPreferences.Editor spEditorForCount;
    private static final String myTagForPowerManager = "myApp:WakeLock";
    private PowerManager.WakeLock mWakeLock;

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_pocket_sense);


        //TODO:For finding ID's
        inItComponents();
        //TODO:Main Function
        pocketSenseModeFeature();


    }

    private void pocketSenseModeFeature() {

        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //
        SharedPreferences sp3 = getSharedPreferences("Swch3 State", Context.MODE_PRIVATE);
        pocketSenseSwitch.setChecked(sp3.getBoolean("Swch3 State", false));
        switchStatus=sp3.getBoolean("Swch3 State", false);
        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }

        //TODO: Code for POCKET SENSE MODE on Switch
        pocketSenseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //for device admin
                devicePolicyManagerForProximity = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
                activityManageForProximity = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                compName = new ComponentName(PocketSenseMode.this, MyAdmin.class);
                boolean active = devicePolicyManagerForProximity.isAdminActive(compName);
                //
                if (isChecked) {
                    if (active) {
                        goToLockDeviceForProximity();
                    }
                    else {
                        Intent goToDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        goToDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                        goToDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You need to allow AntiBandit as an Device administrator for using this Feature");
                        startActivityForResult(goToDeviceAdmin, RESULT_ENABLE_FOR_PROXIMITY);
                    }
                }
                else
                {
                        SharedPreferences.Editor editor4 = getSharedPreferences("Swch3 State", Context.MODE_PRIVATE).edit();
                        editor4.putBoolean("Swch3 State", false);
                    /*    if(MainActivity.count>0)
                        {
                            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                            spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
                            spEditorForCount.commit();
                        }
                    */    editor4.commit();
                        txtOnOff.setText("OFF");
                        switchStatus=false;
                        Intent goToMotionSensorService=new Intent(PocketSenseMode.this,ServiceForPocketSensor.class);
                        stopService(goToMotionSensorService);
                        Toast.makeText(PocketSenseMode.this, "Pocket Sense Mode Off", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onPause();
    }
    public void backImageClick(View view) {

       /* Intent returnIntent = new Intent();
        returnIntent.putExtra("POCKET_SENSE_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
       */ finish();
    }
    private void inItComponents() {
        pocketSenseSwitch=(Switch)findViewById(R.id.swh_option3);
        txtOnOff=(TextView)findViewById(R.id.txt_on_off);
    }
    private void goToLockDeviceForProximity() {
        alertDialogForProximity = new AlertDialog.Builder(this).create();
        alertDialogForProximity.setTitle("Pocket Sense will be Activated In 5 Seconds");
        alertDialogForProximity.setMessage("00:10");
        SharedPreferences.Editor editor4 = getSharedPreferences("Swch3 State",Context.MODE_PRIVATE).edit();
        editor4.putBoolean("Swch3 State", true);
        /*if(MainActivity.count<=8)
        {
            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
            spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count+1);
            spEditorForCount.commit();
        }*/
        editor4.commit();
        txtOnOff.setText("ON");
        switchStatus=true;

        cdtForProximity = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                alertDialogForProximity.setMessage("00:" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                //info.setVisibility(View.GONE);
                proximitySwitch = 1;
                alertDialogForProximity.hide();
                Intent goToPocketSensorService=new Intent(PocketSenseMode.this,ServiceForPocketSensor.class);
                goToPocketSensorService.putExtra("proximitySwitch",proximitySwitch);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(goToPocketSensorService);
                }
                else{
                    startService(goToPocketSensorService);
                }
                devicePolicyManagerForProximity.lockNow();
                finish();

                // Toast.makeText(MotionDetector.this, "Motion Detection Mode Activated", Toast.LENGTH_SHORT).show();
            }
        }.start();
        alertDialogForProximity.show();
        alertDialogForProximity.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, myTagForPowerManager);
        if(mWakeLock.isHeld())mWakeLock.release();
        //TODO:MOTION SENSE MODE SWITCH
        SharedPreferences sp3 = getSharedPreferences("Swch3 State", Context.MODE_PRIVATE);
        pocketSenseSwitch.setChecked(sp3.getBoolean("Swch3 State", false));
        switchStatus=sp3.getBoolean("Swch3 State", false);
        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }
    }
    @Override
    public void onBackPressed() {
        /*Intent returnIntent = new Intent();
        returnIntent.putExtra("POCKET_SENSE_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);*/
        finish();
    }
    public void displayToast(String message) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE_FOR_PROXIMITY:
                if (resultCode == Activity.RESULT_OK) {
                    SharedPreferences.Editor editor2 = getSharedPreferences("adminSwitch",Context.MODE_PRIVATE).edit();
                    editor2.putBoolean("adminSwitch", true);
                    editor2.commit();
                    goToLockDeviceForProximity();
                    //Toast.makeText(getActivity(), "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
