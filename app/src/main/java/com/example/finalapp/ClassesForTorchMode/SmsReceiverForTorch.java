package com.example.finalapp.ClassesForTorchMode;

/**
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SmsReceiverForTorch extends BroadcastReceiver {
    String messageLowerCase="";
    String phone = null;

    public SmsReceiverForTorch() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String message = currentMessage.getDisplayMessageBody();
                    phone=currentMessage.getOriginatingAddress();
                    messageLowerCase = message.toLowerCase();
                    try {
                        if (messageLowerCase.indexOf("flashon")>-1) {
                            CameraService.CameraInstance(context).turnOnFlashLight();
                            sendSMS("Now Flash is ON");
                        }
                        if (messageLowerCase.indexOf("flashoff")>-1) {
                            CameraService.CameraInstance(context).turnOffFlashLight();
                            sendSMS("Now Flash is OFF");
                        }
                    } catch (Exception e) {
                    }

                }
            }

        } catch (Exception e) {

        }

    }
    public void sendSMS(String text){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, text, null, null);
    }
}
