package com.example.finalapp.ClassesForRingAlarmMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.ClassesForHeadphoneSenseMode.HeadphoneSenseBroadCastReceiver;
import com.example.finalapp.LockMonitor;
import com.example.finalapp.PopupActivity;
import com.example.finalapp.R;
import com.example.finalapp.RingSirenService;
import com.example.finalapp.StateReceiver;

public class SmsListenerForRingAlarm extends BroadcastReceiver {

    String phone = null;
    Context context = null;
    StorageForRingAlarm storageForRingAlarm = null ;
    boolean coarse = false;
    private static final int MY_PASSWORD_DIALOG_ID = 4;
    RingAlarmMode ringAlarmModeInstance;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        ringAlarmModeInstance=new RingAlarmMode();
        storageForRingAlarm = StorageForRingAlarm.getInstance(context);
        String action = intent.getAction();
        /*
            New SMS
         */
        //showDialog();
        if(action.equals("android.provider.Telephony.SMS_RECEIVED")){

            SmsMessage[] messages = extractSmsFromIntent(intent);
            for(SmsMessage m : messages){
                if (parseMessage(m)){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.getApplicationContext().startForegroundService(new Intent(context, RingSirenService.class));
                    } else {
                        context.getApplicationContext().startService(new Intent(context, RingSirenService.class));
                    }
                    try {
                        Bundle bundle = intent.getExtras();
                        String message = bundle.getString("alarm_message");

                        Intent newIntent = new Intent(context, PopupActivity.class);
                        newIntent.putExtra("alarm_message", message);
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(newIntent);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    //sendSMS("ringing ");
                   ////////////////////////
                    /* final Intent newIntent = new Intent(context, LockMonitor.class);
                    newIntent.setAction(LockMonitor.ACTION_CHECK_LOCK);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(newIntent);
                    } else {
                        context.startService(newIntent);
                    }*/

                    //Toast.makeText(context, "message received", Toast.LENGTH_SHORT).show();
                    //ringAlarmModeInstance.goToLockDeviceForRing();

                     //////////////////////////////////////////////
            //TODO: Dialogue for Entering Password


            /*        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context,R.style.MyDialogTheme);

                    dialogBuilder.setTitle("SYSTEM ALERT");
                    dialogBuilder.setMessage("Enter Password");
                    dialogBuilder.setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    );

                    final AlertDialog dialog = dialogBuilder.create();
                    final Window dialogWindow = dialog.getWindow();
                    final WindowManager.LayoutParams dialogWindowAttributes = dialogWindow.getAttributes();

// Set fixed width (280dp) and WRAP_CONTENT height
                    final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialogWindowAttributes);
                    lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280,context.getResources().getDisplayMetrics());
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialogWindow.setAttributes(lp);

// Set to TYPE_SYSTEM_ALERT so that the Service can display it
                    dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    //dialogWindowAttributes.windowAnimations = R.style.DialogAnimation;
                    dialog.show();
*/
            }
            }
        }

    }
    public void showDialog() {
        final WindowManager manager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.alpha = 1.0f;
        layoutParams.packageName = context.getPackageName();
        layoutParams.buttonBrightness = 1f;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;

        final View view = View.inflate(context.getApplicationContext(),R.layout.motion_sense_dialog, null);
        Button yesButton = (Button) view.findViewById(R.id.ok);
        //Button noButton = (Button) view.findViewById(R.id.noButton);
        yesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                manager.removeView(view);
            }
        });
        manager.addView(view, layoutParams);
    }
    public SmsMessage[] extractSmsFromIntent(Intent intent){
        SmsMessage[] msgs = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        } else{
            // For ver. prior 19
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                //---retrieve the SMS message received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for(int i=0; i<msgs.length; i++){
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }
            }
        }
        return msgs;
    }
    public void sendSMS(String text){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, text, null, null);
    }
    private boolean parseMessage(SmsMessage sms){
        String clearMessage = sms.getMessageBody().toLowerCase().trim();
        String[] parts = clearMessage.split(":");
        String passphrase = storageForRingAlarm.getCurrentPassphrase();
        if (parts.length == 0){
            return false;
        }

        String messagePassphrase = parts[0];
        if (! passphrase.toLowerCase().equals(messagePassphrase)){
            return false;
        }

        this.phone = sms.getOriginatingAddress();

        // Parse params

        for(int i=1;i<parts.length;i++){

            if (parts[i].toLowerCase().equals("reset")){
                storageForRingAlarm.removeRequest(phone);
                sendSMS("Request canceled");
                continue;
            }

            if (parts[i].toLowerCase().equals("coarse")){
                coarse = true;
                continue;
            }

            if (parts[i].toLowerCase().equals("requests")){
                sendListOfCurrentRequests();
                // Only send list of phones waiting for location
                return false;
            }
        }

        return true;
    }
    private void sendListOfCurrentRequests(){
        String text = "";
        if (storageForRingAlarm.getCurrentRequests().isEmpty()){
            text = context.getString(R.string.sms_no_requests);
        }else{
            for(String ph : storageForRingAlarm.getCurrentRequests()){
                text += ph + "\n";
            }
        }
        sendSMS("Current requests: \n" + text);
    }

}
