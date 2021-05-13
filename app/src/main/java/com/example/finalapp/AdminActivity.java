package com.example.finalapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.ClassesForMotionSenseMode.MotionSenseMode;

public class AdminActivity extends AppCompatActivity {

    Switch deviceAdminSwitch;
    TextView txtOnOff;
    public static final int RESULT_ENABLE_FOR_ACCEL = 10;
    private DevicePolicyManager devicePolicyManagerForAccel;
    private ActivityManager activityManageForAccel;
    private ComponentName compName;
    boolean switchStatus=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        //TODO:For finding ID's
        inItComponents();

        devicePolicyManagerForAccel = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        activityManageForAccel = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        compName = new ComponentName(AdminActivity.this, MyAdmin.class);

        SharedPreferences sp4 = getSharedPreferences("adminSwitch", Context.MODE_PRIVATE);
        deviceAdminSwitch.setChecked(sp4.getBoolean("adminSwitch", false));
        switchStatus=sp4.getBoolean("adminSwitch", false);
        if(switchStatus){
            txtOnOff.setText("ON");
            //deviceAdminSwitch.setChecked(true);
        }
        else{
            txtOnOff.setText("OFF");
            //deviceAdminSwitch.setChecked(false);
        }

        //TODO: Code for GPS LOCATION MODE on Switch
        deviceAdminSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (isAdminActive()) {
                        SharedPreferences.Editor editor2 = getSharedPreferences("adminSwitch",Context.MODE_PRIVATE).edit();
                        editor2.putBoolean("adminSwitch", true);
                        editor2.commit();
                        deviceAdminSwitch.setChecked(true);
                        txtOnOff.setText("ON");
                    }
                    else {
                        Intent goToDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        goToDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                        goToDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You need to allow AntiBandit as an Device administrator for using this Feature");
                        startActivityForResult(goToDeviceAdmin, RESULT_ENABLE_FOR_ACCEL);
                    }
                }
                else {
                    if (devicePolicyManagerForAccel != null || devicePolicyManagerForAccel.isAdminActive(compName)) {
                        devicePolicyManagerForAccel.removeActiveAdmin(compName);
                        SharedPreferences.Editor editor2 = getSharedPreferences("adminSwitch",Context.MODE_PRIVATE).edit();
                        editor2.putBoolean("adminSwitch", false);
                        editor2.commit();
                        deviceAdminSwitch.setChecked(false);
                        txtOnOff.setText("OFF");
                        switchStatus=false;
                    }
                }
            }
        });

        /*SharedPreferences sp4 = getSharedPreferences("adminSwitch", Context.MODE_PRIVATE);
        deviceAdminSwitch.setChecked(sp4.getBoolean("adminSwitch", false));
        switchStatus=sp4.getBoolean("adminSwitch", false);
        if(switchStatus){
            txtOnOff.setText("ON");
            deviceAdminSwitch.setChecked(true);
        }
        else{
            txtOnOff.setText("OFF");
            deviceAdminSwitch.setChecked(false);
        }*/
    }
    public void backImageClick(View view) {
        /*Intent returnIntent = new Intent(AdminActivity.this,MainActivity.class);
        startActivity(returnIntent);
        finish();*/
        onBackPressed();
    }
    private void inItComponents() {
        deviceAdminSwitch=(Switch)findViewById(R.id.swh_device_admin);
        txtOnOff=(TextView)findViewById(R.id.txt_on_off);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE_FOR_ACCEL:
                if (resultCode == Activity.RESULT_OK) {
                    SharedPreferences.Editor editor2 = getSharedPreferences("adminSwitch",Context.MODE_PRIVATE).edit();
                    editor2.putBoolean("adminSwitch", true);
                    editor2.commit();
                    deviceAdminSwitch.setChecked(true);
                    txtOnOff.setText("ON");
                    switchStatus=true;
                    //Toast.makeText(getActivity(), "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor2 = getSharedPreferences("adminSwitch",Context.MODE_PRIVATE).edit();
                    editor2.putBoolean("adminSwitch", false);
                    editor2.commit();
                    deviceAdminSwitch.setChecked(false);
                    txtOnOff.setText("OFF");
                    switchStatus=false;
                    Toast.makeText(this, "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public boolean isAdminActive(){
        //for device admin
        boolean active = devicePolicyManagerForAccel.isAdminActive(compName);
        return active;
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp4 = getSharedPreferences("adminSwitch", Context.MODE_PRIVATE);
        deviceAdminSwitch.setChecked(sp4.getBoolean("adminSwitch", false));
        switchStatus=sp4.getBoolean("adminSwitch", false);
        if(switchStatus){
            txtOnOff.setText("ON");
            deviceAdminSwitch.setChecked(true);
        }
        else{
            deviceAdminSwitch.setChecked(false);
            txtOnOff.setText("OFF");
        }
    }*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
