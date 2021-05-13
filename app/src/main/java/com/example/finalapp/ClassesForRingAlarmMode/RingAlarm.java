package com.example.finalapp.ClassesForRingAlarmMode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.MainActivity;
import com.example.finalapp.R;

import static android.content.ContentValues.TAG;

public class RingAlarm extends Activity {

    final static String PHONE_KEY = "phone";
    final static int RC_OVERLAY = 02;

    String phone;
    TextView tePassphrase;
    StorageForRingAlarm storageForRingAlarm;
    EditText editText;
    Toast toast;
    SharedPreferences.Editor spEditorForCount;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ring_alarm);
        storageForRingAlarm = StorageForRingAlarm.getInstance(this);
        editText=(EditText)findViewById(R.id.te_passphrase_for_ring_alarm);

        storePhoneFromIntent();
        initPassphraseEdit();
        initButtons();
    }
    private void initButtons(){

        Button clickButton = findViewById(R.id.save_button_for_ring_alarm);
        final Activity activity = this;
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!editText.getText().toString().trim().equalsIgnoreCase("")){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("CODE_SAVED",true);
                    setResult(Activity.RESULT_OK,returnIntent);
                    displayToast("Ring Alarm Mode is Enabled");
                    activity.finish();
                }
                else{
                    editText.setError("Keyword is Must");
                }
            }
        });
    }
    /*
     Trick for Mi and Huaway devices
    */


        private void storePhoneFromIntent(){
        Intent intent = getIntent();
        phone = intent.getStringExtra(PHONE_KEY);
    }

    private void initPassphraseEdit(){
        tePassphrase = findViewById(R.id.te_passphrase_for_ring_alarm);
        tePassphrase.setText(storageForRingAlarm.getCurrentPassphrase());
    }
    @Override
    public void onResume(){
        super.onResume();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            openOverlaySettings();/////////////////////////////////////////

        }*/
    }

    @Override
    public void onPause(){
        storageForRingAlarm.savePassphrase(getPassphraseFromInput());
        super.onPause();
        }

    private String getPassphraseFromInput(){
        String userInput = tePassphrase.getText().toString().toLowerCase().trim();
        if (userInput.isEmpty()){
            return getString(R.string.default_passphrase_for_ring_alarm);
        }
        return userInput;
    }
    public void displayToast(String message) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void openOverlaySettings() {
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        try {
            startActivityForResult(intent,RC_OVERLAY);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_OVERLAY:
                final boolean overlayEnabled = Settings.canDrawOverlays(getApplicationContext());
               if(overlayEnabled) {
                   finish();
               }
                break;
        }
    }
    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor4 = getSharedPreferences("Swch5 State", Context.MODE_PRIVATE).edit();
        editor4.putBoolean("Swch5 State", false);
        if(MainActivity.count>0)
        {
            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
            spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
            spEditorForCount.commit();
        }
        editor4.commit();
        finish();
    }

}
