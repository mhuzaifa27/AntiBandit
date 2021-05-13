package com.example.finalapp.ClassesForChargingSenseMode;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;

import com.example.finalapp.MainActivity;
import com.example.finalapp.R;
import com.example.finalapp.RingSirenService;

public class ChargeSenseBroadCastReceiver extends BroadcastReceiver {
    int chargePlug;
    boolean usbCharge,acCharge;

    @Override
    public void onReceive(Context context, Intent intent) {

        chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;



            if((acCharge||usbCharge)!=false){
                context.stopService(new Intent(context, RingSirenService.class));
            }
            else{
                showChargerSenseNotification(context);
                context.startService(new Intent(context, RingSirenService.class));
            }
        }

    private void showChargerSenseNotification(Context context) {
            NotificationManager mNotificationManager =
                    (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
           //TODO: the following check is for API>26 to create Channels
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("1",
                        "CHARGER SENSE",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("CHARGER SENSE DESCRIPTION");
                mNotificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "YOUR_CHANNEL_ID")
                    .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                    .setContentTitle("Charger Sense Mode") // title for notification
                    .setContentText("Tap for Disable Charger Sense Mode")// message for notification
                    .setAutoCancel(true);// clear notification after click

            //TODO: This intent is for going to main activity when tap on notification in status bar
            Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(), 1,intent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(pi);
            mNotificationManager.notify(1, mBuilder.build());
        }
    }