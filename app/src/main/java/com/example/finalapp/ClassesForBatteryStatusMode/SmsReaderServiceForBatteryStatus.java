package com.example.finalapp.ClassesForBatteryStatusMode;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

public class SmsReaderServiceForBatteryStatus extends Service {
    SmsListenerForBatteryStatus mListenerForBatteryStatus;

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this,BatteryStatusMode.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification =
                    new Notification.Builder(this, "YOUR CHANNEL ID")
                            .setContentTitle("Battery Status Mode")
                            .setContentText("Battery Status Mode is Activated")
                            //.setSmallIcon(R.drawable.icon)
                            .setContentIntent(pendingIntent)
                            //.setTicker(getText(R.string.ticker_text))
                            .build();

            startForeground(4, notification);
        }
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiverForSMS();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        getApplicationContext().unregisterReceiver(mListenerForBatteryStatus);
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

    private void registerReceiverForSMS() {
        IntentFilter filter1 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        IntentFilter filter2 = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

        filter1.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter1.setPriority(2);
        filter2.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter2.setPriority(1);

        mListenerForBatteryStatus = new SmsListenerForBatteryStatus();
        getApplication().registerReceiver(mListenerForBatteryStatus, filter1);
        getApplication().registerReceiver(mListenerForBatteryStatus, filter2);

    }
}
