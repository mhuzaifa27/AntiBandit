package com.example.finalapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.finalapp.ClassesForChargingSenseMode.ChargerSenseMode;
import com.example.finalapp.ClassesForRingAlarmMode.RingAlarmMode;

public class RingSirenService extends Service {

    MediaPlayer mPlayer;

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this, RingAlarmMode.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification =
                    new Notification.Builder(this, "YOUE CHANNEL ID")
                            .setContentTitle("Ring Alarm Mode")
                            .setContentText("Charge Sense Mode is Activated")
                            //.setSmallIcon(R.drawable.icon)
                            .setContentIntent(pendingIntent)
                            //.setTicker(getText(R.string.ticker_text))
                            .build();

            startForeground(400, notification);
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AudioManager audioManager = (AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        if(mPlayer==null){
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.siren);
        if (!mPlayer.isPlaying()) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(1,1);
            mPlayer.start();

        }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            super.onDestroy();
        }
    }


}
