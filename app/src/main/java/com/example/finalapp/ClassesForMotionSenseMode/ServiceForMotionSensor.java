package com.example.finalapp.ClassesForMotionSenseMode;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;

import com.example.finalapp.MainActivity;
import com.example.finalapp.RingSirenService;

public class ServiceForMotionSensor extends Service
        implements SensorEventListener {

    private SensorManager sManagerForAccel;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    AlertDialog alertDialog;
    private static final int SENSOR_SENSITIVITY = 4;
    int accelSwitch= 0;
    private static final String myTagForPowerManager = "myApp:WakeLock";
    private PowerManager.WakeLock mWakeLock;
    MotionSenseMode i;


    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this,MotionSenseMode.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification =
                    new Notification.Builder(this, "YOUR CHANNEL ID")
                            .setContentTitle("Motion Sensor Mode")
                            .setContentText("Motion Sensor Mode is Activated")
                            //.setSmallIcon(R.drawable.icon)
                            .setContentIntent(pendingIntent)
                            //.setTicker(getText(R.string.ticker_text))
                            .build();
            i=new MotionSenseMode();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, myTagForPowerManager);
            mWakeLock.acquire();
            initSensors();

            startForeground(10, notification);
        }
        else{
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, myTagForPowerManager);
            mWakeLock.acquire();
            initSensors();
        }
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        accelSwitch=intent.getIntExtra("accelSwitch",0);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
            sManagerForAccel.unregisterListener(this);

            if(mWakeLock.isHeld())mWakeLock.release();
        }
        else {
            sManagerForAccel.unregisterListener(this);
            if(mWakeLock.isHeld())mWakeLock.release();
        }
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

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onSensorChanged(SensorEvent event) {


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if (mAccel > 0.5) {
                if (accelSwitch == 1) {
                    KeyguardManager myKM = (KeyguardManager) getApplication().getSystemService(Context.KEYGUARD_SERVICE);
                    if( myKM.inKeyguardRestrictedInputMode()) {
                            getApplication().startService(new Intent(getApplication(), RingSirenService.class));
                    }
                    else {
                            getApplication().stopService(new Intent(getApplication(),RingSirenService.class));
                        SharedPreferences.Editor editor10 = getSharedPreferences("Swch4 State", Context.MODE_PRIVATE).edit();
                        editor10.putBoolean("Swch4 State", false);
                        editor10.commit();
                        Intent goToMotionSensorService=new Intent(getApplicationContext(), ServiceForMotionSensor.class);
                        getApplication().stopService(goToMotionSensorService);
                        /*if(MainActivity.count>0)
                        {
                            SharedPreferences.Editor spEditorForCount;
                            spEditorForCount=getSharedPreferences("countValue",Context.MODE_PRIVATE).edit();
                            spEditorForCount.putInt("countValue",MainActivity.count=MainActivity.count-1);
                            spEditorForCount.commit();
                        }*/
                        //i.disableFeature(getApplicationContext());

                }

            }
        }
    }
    }
    private void initSensors() {
        //for sensors

        sManagerForAccel = (SensorManager) getApplication().getSystemService(SENSOR_SERVICE);
        accelerometer = sManagerForAccel.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        alertDialog = new AlertDialog.Builder(getApplication()).create();

        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sManagerForAccel.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);

    }
}
