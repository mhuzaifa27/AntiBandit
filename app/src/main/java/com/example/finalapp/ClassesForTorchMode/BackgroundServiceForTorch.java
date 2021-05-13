package com.example.finalapp.ClassesForTorchMode;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;


public class BackgroundServiceForTorch extends Service {
    private SmsReceiverForTorch smsReceiver;
    private IntentFilter filter;

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this,TorchMode.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification =
                    new Notification.Builder(this, "YOUR CHANNEL ID")
                            .setContentTitle("TORCH Mode")
                            .setContentText("TORCH Mode is Activated")
                            //.setSmallIcon(R.drawable.icon)
                            .setContentIntent(pendingIntent)
                            //.setTicker(getText(R.string.ticker_text))
                            .build();

            startForeground(4, notification);
        }
        super.onCreate();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this,"Background SMS Monitoring Started",Toast.LENGTH_LONG).show();
        smsReceiver = new SmsReceiverForTorch();
        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        //filter.setPriority(2147483647);
        getApplication().registerReceiver(smsReceiver, filter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this,"Background SMS Monitoring Stopped",Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        else{
            getApplication().unregisterReceiver(smsReceiver);
        }
        CameraService.CameraInstance(this).destroyCamera();
    }
}
