package com.example.finalapp.ClassesForBatteryStatusMode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.example.finalapp.R;

import java.util.Calendar;
import java.util.Date;

public class SmsListenerForBatteryStatus extends BroadcastReceiver {
    final static double minimalAccuracy = 75.0;

    String phone = null;
    Context context = null;
    StorageForBatteryStatus storageForBatteryStatus = null ;
    boolean coarse = false;
    int level;
    Date currentTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        storageForBatteryStatus = storageForBatteryStatus.getInstance(context);
        String action = intent.getAction();

        currentTime = Calendar.getInstance().getTime();
        if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
            SmsMessage[] messages = extractSmsFromIntent(intent);
            for(SmsMessage m : messages){
                if (parseMessage(m)){

                    sendSMS("Battery Status is: "+level+"% at "+currentTime);
                }
            }
        }
        if(action.equals(Intent.ACTION_BATTERY_CHANGED)){
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }

    }



    private boolean parseMessage(SmsMessage sms){
        String clearMessage = sms.getMessageBody().toLowerCase().trim();
        String[] parts = clearMessage.split(":");
        String passphrase = storageForBatteryStatus.getCurrentPassphrase();
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
                storageForBatteryStatus.removeRequest(phone);
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
        if (storageForBatteryStatus.getCurrentRequests().isEmpty()){
            text = context.getString(R.string.sms_no_requests);
        }else{
            for(String ph : storageForBatteryStatus.getCurrentRequests()){
                text += ph + "\n";
            }
        }
        sendSMS("Current requests: \n" + text);
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
}
