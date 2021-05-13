package com.example.finalapp.ClassesForGpsLocaitonMode;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.example.finalapp.R;

/*
    This class created only to provide capability with android 8+
    due to this :
        https://developer.android.com/about/versions/oreo/background-location-limits.html
    and this :
        https://www.journaldev.com/23653/android-oreo-implicit-and-explicit-broadcast-receiver
    OS behaviors

 */


public class GPSForegroundServiceForAndroid8 extends Service implements LocationListener {
    public static final int DEFAULT_NOTIFICATION_ID = 101;
    LocationManager locationManager;
    String phone;
    String notificationChannelId;
    int attempt;

    @Override
    public void onCreate() {
        notificationChannelId = createNotificationChannel();
        super.onCreate();
    }


    public int onStartCommand(Intent intent, int flags, int startId) throws SecurityException{
        Context context = getApplicationContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        phone = intent.getExtras().getString("phone");
        Notification notification = getNotification(getString(R.string.foreground_start),getString(R.string.by_request)+phone);
       startForeground(DEFAULT_NOTIFICATION_ID,notification);



        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 70, this);


        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(26)
    public Notification getNotification(String title,String text) {
        Intent notificationIntent = new Intent(this, GpsLocationMode.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new Notification.Builder(this, notificationChannelId)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        //.setTicker(getText(R.string.ticker_text))
                        .build();
        return notification;
    }

    @TargetApi(26)
    private String createNotificationChannel(){

        String NOTIFICATION_CHANNEL_ID = "com.gan4x4.simplesmslocator";
        String channelName = "Simple SMS locator";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        return NOTIFICATION_CHANNEL_ID;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        attempt++;


        if (SmsListenerForGPS.minimalAccuracy > location.getAccuracy()) {

            String key = LocationManager.KEY_LOCATION_CHANGED;
            Intent intent = new Intent("com.gan4x4.LOCATION_UPDATE");
            intent.putExtra("phone", phone);
            intent.putExtra("attempt", attempt);
            intent.putExtra(key, location);
            sendBroadcast(intent);
        }

    }

    @Override
    public void onDestroy(){

        locationManager.removeUpdates(this);
        stopForeground(true);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
