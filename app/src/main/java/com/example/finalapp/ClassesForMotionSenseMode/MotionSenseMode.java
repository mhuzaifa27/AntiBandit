package com.example.finalapp.ClassesForMotionSenseMode;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.AdminActivity;
import com.example.finalapp.MainActivity;
import com.example.finalapp.MyAdmin;
import com.example.finalapp.R;

public class MotionSenseMode extends Activity {

    public static final int RESULT_ENABLE_FOR_ACCEL = 10;
    private DevicePolicyManager devicePolicyManagerForAccel;
    private ActivityManager activityManageForAccel;
    private ComponentName compName;
    AlertDialog alertDialogForAccel;
    CountDownTimer cdtForAccel;
    int accelSwitch=0;
    private static final String myTagForPowerManager = "myApp:WakeLock";
    private PowerManager.WakeLock mWakeLock;
    SharedPreferences.Editor editor2;



    Toast toast;
    Boolean switchStatus=false;
    TextView txtOnOff;
    Switch motionSenseSwitch;
    SharedPreferences spForCount;
    SharedPreferences.Editor spEditorForCount;

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_motion_sense);

        //TODO:For finding ID's
        inItComponents();
        //TODO:Main Function
        motionSenseModeFeature();


    }

    private void motionSenseModeFeature() {
       /* spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);
*/
        //
        SharedPreferences sp4 = getSharedPreferences("Swch4 State", Context.MODE_PRIVATE);
        motionSenseSwitch.setChecked(sp4.getBoolean("Swch4 State", false));
        switchStatus=sp4.getBoolean("Swch4 State", false);
        if(switchStatus){
            txtOnOff.setText("ON");
           // motionSenseSwitch.setChecked(true);
        }
        else{
            //motionSenseSwitch.setChecked(false);
            txtOnOff.setText("OFF");
        }

        //TODO: Code for MOTION SENSE MODE on Switch
        motionSenseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //for device admin
                devicePolicyManagerForAccel = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
                activityManageForAccel = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                compName = new ComponentName(MotionSenseMode.this, MyAdmin.class);
                boolean active = devicePolicyManagerForAccel.isAdminActive(compName);
                //
                if (isChecked) {
                    if (active) {
                        goToLockDeviceForAccel();
                    }
                    else {
                        Intent goToDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        goToDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                        goToDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You need to allow AntiBandit as an Device administrator for using this Feature");
                        startActivityForResult(goToDeviceAdmin, RESULT_ENABLE_FOR_ACCEL);
                    }
                }
                else
                {
                    SharedPreferences.Editor editor2 = getSharedPreferences("Swch4 State", Context.MODE_PRIVATE).edit();
                        editor2.putBoolean("Swch4 State", false);
                        /*if(MainActivity.count>0)
                        {
                            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                            spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
                            spEditorForCount.commit();
                        }*/
                        editor2.commit();
                        txtOnOff.setText("OFF");
                        switchStatus=false;
                        Intent goToMotionSensorService=new Intent(getApplicationContext(),ServiceForMotionSensor.class);
                        stopService(goToMotionSensorService);
                        //Toast.makeText(getApplication(), "Motion Sense Mode Off", Toast.LENGTH_SHORT).show();
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

    private void inItComponents() {
        motionSenseSwitch=(Switch)findViewById(R.id.swh_option4);
        txtOnOff=(TextView)findViewById(R.id.txt_on_off);
    }
    private void goToLockDeviceForAccel() {
        alertDialogForAccel = new AlertDialog.Builder(this).create();
        alertDialogForAccel.setTitle("Motion Sensor will be Activated In 5 Seconds");
        alertDialogForAccel.setMessage("00:10");
        SharedPreferences.Editor editor2 = getSharedPreferences("Swch4 State",Context.MODE_PRIVATE).edit();
        editor2.putBoolean("Swch4 State", true);
       /* if(MainActivity.count<=8)
        {
            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
            spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count+1);
            spEditorForCount.commit();
        }*/
        editor2.commit();
        txtOnOff.setText("ON");
        switchStatus=true;

        cdtForAccel = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                alertDialogForAccel.setMessage("00:" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                //info.setVisibility(View.GONE);
                accelSwitch = 1;
                alertDialogForAccel.hide();
                Intent goToMotionSensorService=new Intent(MotionSenseMode.this,ServiceForMotionSensor.class);
                goToMotionSensorService.putExtra("accelSwitch",accelSwitch);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(goToMotionSensorService);
                }
                else{
                    startService(goToMotionSensorService);
                }
                devicePolicyManagerForAccel.lockNow();
                finish();


                // Toast.makeText(MotionDetector.this, "Motion Detection Mode Activated", Toast.LENGTH_SHORT).show();
            }
        }.start();
        alertDialogForAccel.show();
        alertDialogForAccel.setCancelable(false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, myTagForPowerManager);
        if(mWakeLock.isHeld())mWakeLock.release();
        //if(accelSwitch==1) disableFeature(this);
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:MOTION SENSE MODE SWITCH
        SharedPreferences sp4 = getSharedPreferences("Swch4 State", Context.MODE_PRIVATE);
        motionSenseSwitch.setChecked(sp4.getBoolean("Swch4 State", false));
        switchStatus=sp4.getBoolean("Swch4 State", false);
        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }
    }
    @Override
    public void onBackPressed() {
       /* Intent returnIntent = new Intent();
        returnIntent.putExtra("MOTION_SENSE_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);*/
        finish();
    }
    public void backImageClick(View view)
    {
        /*Intent returnIntent = new Intent();
        returnIntent.putExtra("MOTION_SENSE_SWITCH_STATUS",switchStatus);
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
            case RESULT_ENABLE_FOR_ACCEL:
                if (resultCode == Activity.RESULT_OK) {
                    SharedPreferences.Editor editor2 = getSharedPreferences("adminSwitch",Context.MODE_PRIVATE).edit();
                    editor2.putBoolean("adminSwitch", true);
                    editor2.commit();
                    goToLockDeviceForAccel();
                    //Toast.makeText(getActivity(), "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void disableFeature(Context context){
                onResume();
                /*LayoutInflater factory = LayoutInflater.from(this);
                        final View deleteDialogView = factory.inflate(R.layout.motion_sense_dialog, null);

                        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
                        deleteDialog.setView(deleteDialogView);
                        deleteDialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                deleteDialog.dismiss();
                                SharedPreferences.Editor editor10 = getSharedPreferences("Swch4 State", Context.MODE_PRIVATE).edit();
                                editor10.putBoolean("Swch4 State", false);
                                if(MainActivity.count>0)
                                {
                                    spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                                    spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
                                    spEditorForCount.commit();
                                }
                                editor10.commit();
                                motionSenseSwitch.setChecked(false);
                                Intent goToMotionSensorService=new Intent(MotionSenseMode.this,ServiceForMotionSensor.class);
                                stopService(goToMotionSensorService);
                                Toast.makeText(MotionSenseMode.this, "Motion Sense Mode Off", Toast.LENGTH_SHORT).show();
                                txtOnOff.setText("OFF");
                                switchStatus=false;
                            }
                        });
                        deleteDialog.show();*/


    }
}
