package com.example.finalapp.ClassesForHeadphoneSenseMode;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import com.example.finalapp.MainActivity;

public class HeadphoneBroadCastRunningService extends Service {
    HeadphoneSenseBroadCastReceiver mHeadphoneBroadcastReceiver;
    IntentFilter iFilter;

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this, HeadphoneSenseMode.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification =
                    new Notification.Builder(this, "YOUE CHANNEL ID")
                            .setContentTitle("Headphone Sense Mode")
                            .setContentText("Headphone Sense Mode is Activated")
                            //.setSmallIcon(R.drawable.icon)
                            .setContentIntent(pendingIntent)
                            //.setTicker(getText(R.string.ticker_text))
                            .build();

            startForeground(2, notification);
        }
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHeadphoneBroadcastReceiver = new HeadphoneSenseBroadCastReceiver();
        iFilter=new IntentFilter();
        iFilter.addAction("android.intent.action.HEADSET_PLUG");
        getApplicationContext().registerReceiver(mHeadphoneBroadcastReceiver, iFilter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        getApplicationContext().unregisterReceiver(mHeadphoneBroadcastReceiver);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android
    }

}
