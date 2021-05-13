package com.example.finalapp.ClassesForHeadphoneSenseMode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.finalapp.RingSirenService;

public class HeadphoneSenseMode extends Activity {

    int state;
    Boolean switchStatus=false;
    Switch headphoneSenseSwitch;
    Toast toast;
    TextView txtOnOff;
    SharedPreferences spForCount;
    SharedPreferences.Editor spEditorForCount;


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_headphone_sense);

        //TODO:For finding ID's
        inItComponents();
        //TODO:Main Function
        headphoneModeFeature();

    }

    private void headphoneModeFeature() {
        //
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //
        SharedPreferences sp2 = getSharedPreferences("Swch2 State", Context.MODE_PRIVATE);
        headphoneSenseSwitch.setChecked(sp2.getBoolean("Swch2 State", false));
        switchStatus=sp2.getBoolean("Swch2 State", false);
        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }

        //TODO: Code for HEADPHONE SENSE MODE on Switch
        headphoneSenseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent=registerReceiver(null,new IntentFilter(Intent.ACTION_HEADSET_PLUG));
                state=intent.getIntExtra("state",-1);
                if(state!=0 || isMyServiceRunning(RingSirenService.class)){
                    if(isChecked) {
                        SharedPreferences.Editor editor =getSharedPreferences("Swch2 State", Context.MODE_PRIVATE).edit();
                        editor.putBoolean("Swch2 State", true);
                        if(MainActivity.count<=8)
                        {
                            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                            spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count+1);
                            spEditorForCount.commit();
                        }
                        editor.commit();
                        txtOnOff.setText("ON");
                        switchStatus=true;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService( new Intent(HeadphoneSenseMode.this, HeadphoneBroadCastRunningService.class));
                        }
                        else{
                            startService( new Intent(HeadphoneSenseMode.this, HeadphoneBroadCastRunningService.class));

                        }
                        displayToast("Headphone Sense mode is Enabled");

                        //Toast.makeText(getActivity(),"Headphone Sense mode is Enabled",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(isMyServiceRunning(RingSirenService.class)){
                            headphoneSenseSwitch.setChecked(true);
                            displayToast("Please Connect Back the Headphones!");

                            //Toast.makeText(getActivity(),"Please Connect Back the Headphones!",Toast.LENGTH_SHORT).show();

                        }
                        else{
                            //getContext().unregisterReceiver(mHeadphoneBroadCastReceiver);
                            stopService(new Intent(HeadphoneSenseMode.this, HeadphoneBroadCastRunningService.class));
                            displayToast("Headphone Sense mode is Disabled");

                            // Toast.makeText(getActivity(),"Headphone Sense mode is Disabled",Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = getSharedPreferences("Swch2 State", Context.MODE_PRIVATE).edit();
                            editor.putBoolean("Swch2 State", false);
                            if(MainActivity.count>0)
                            {
                                spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                                spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
                                spEditorForCount.commit();
                            }
                            editor.commit();
                            txtOnOff.setText("OFF");
                            switchStatus=false;
                            state=0;
                        }
                    }
                }
                else{
                    displayToast("Connect Your Headphone First!");
                    //Toast.makeText(getActivity(),"Connect Your Headphone First!",Toast.LENGTH_SHORT).show();
                    headphoneSenseSwitch.setChecked(false);


                }
            }
        });

    }

    private void inItComponents() {
        headphoneSenseSwitch=(Switch)findViewById(R.id.swh_option2);
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
        Intent returnIntent = new Intent();
        returnIntent.putExtra("HEADPHONE_SENSE_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }
    public void displayToast(String message) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
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
    @Override
    protected void onResume() {
        super.onResume();
        spForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE);
        MainActivity.count=spForCount.getInt("countValue",0);

        //TODO:CHARGER REMOVED MODE SWITCH
        SharedPreferences sp1 = getSharedPreferences("Swch2 State", Context.MODE_PRIVATE);
        headphoneSenseSwitch.setChecked(sp1.getBoolean("Swch2 State", false));
        switchStatus=sp1.getBoolean("Swch2 State", false);
        if(switchStatus){
            txtOnOff.setText("ON");
        }
        else{
            txtOnOff.setText("OFF");
        }
    }
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("HEADPHONE_SENSE_SWITCH_STATUS",switchStatus);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
