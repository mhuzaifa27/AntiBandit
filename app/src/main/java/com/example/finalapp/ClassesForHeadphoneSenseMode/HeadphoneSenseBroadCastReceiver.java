package com.example.finalapp.ClassesForHeadphoneSenseMode;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.finalapp.MainActivity;
import com.example.finalapp.R;
import com.example.finalapp.RingSirenService;

public class HeadphoneSenseBroadCastReceiver extends BroadcastReceiver {
    boolean headphonePlug;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    headphonePlug=false;
                    break;
                case 1:
                    headphonePlug=true;
                    break;
                default:
            }
        }

            if(headphonePlug!=false){
                context.stopService(new Intent(context, RingSirenService.class));
            }
            else{
                //showChargerSenseNotification(context);
                context.startService(new Intent(context, RingSirenService.class));
            }


    }
    private void showChargerSenseNotification(Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //TODO: the following check is for API>26 to create Channels
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("2",
                    "HEADPHONE SENSE MODE",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("HEADPHONE SENSE DESCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle("Headphone Sense Mode") // title for notification
                .setContentText("Tap for Disable Headphone Sense Mode")// message for notification
                .setAutoCancel(true);// clear notification after click

        //TODO: This intent is for going to main activity when tap on notification in status bar
        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(), 1,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(2, mBuilder.build());
    }
}
