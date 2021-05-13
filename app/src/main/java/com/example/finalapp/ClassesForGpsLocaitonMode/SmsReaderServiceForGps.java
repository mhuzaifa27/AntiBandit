package com.example.finalapp.ClassesForGpsLocaitonMode;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

public class SmsReaderServiceForGps extends Service {
    SmsListenerForGPS mListenerGps;

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this,GpsLocationMode.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification =
                    new Notification.Builder(this, "YOUR CHANNEL ID")
                            .setContentTitle("GPS Location Mode")
                            .setContentText("GPS Location is Activated")
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
        registerReceiverForSMS();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        getApplicationContext().unregisterReceiver(mListenerGps);
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
        IntentFilter filter1 = new IntentFilter("com.gan4x4.LOCATION_UPDATE");
        IntentFilter filter2 = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        IntentFilter filter3 = new IntentFilter("com.gan4x4.LOCATION_SEND");
        IntentFilter filter4 = new IntentFilter("android.intent.action.BOOT_COMPLETED");

        filter1.addAction("com.gan4x4.LOCATION_UPDATE");
        filter1.setPriority(2);
        filter2.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter2.setPriority(3);
        filter3.addAction("com.gan4x4.LOCATION_SEND");
        filter2.setPriority(1);
        filter4.addAction("android.intent.action.BOOT_COMPLETED");
        filter2.setPriority(1);


        mListenerGps = new SmsListenerForGPS();
        getApplication().registerReceiver(mListenerGps, filter1);
        getApplication().registerReceiver(mListenerGps, filter2);
        getApplication().registerReceiver(mListenerGps, filter3);
        getApplication().registerReceiver(mListenerGps, filter4);

    }
}
