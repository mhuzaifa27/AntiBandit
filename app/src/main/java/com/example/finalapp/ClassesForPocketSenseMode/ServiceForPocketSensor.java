package com.example.finalapp.ClassesForPocketSenseMode;

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

import com.example.finalapp.RingSirenService;

public class ServiceForPocketSensor extends Service
        implements SensorEventListener {

    private SensorManager sManagerForProximity;
    private SensorManager sManagerForAccel;

    private Sensor proximity;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    private static final int SENSOR_SENSITIVITY = 3;
    int proximitySwitch = 0;
    private static final String myTagForPowerManager = "myApp:WakeLock";
    private PowerManager.WakeLock mWakeLock;


    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this,PocketSenseMode.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification =
                    new Notification.Builder(this, "YOUR CHANNEL ID")
                                .setContentTitle("Pocket Sense Mode")
                            .setContentText("Pocket Sense Mode is Activated")
                            //.setSmallIcon(R.drawable.icon)
                            .setContentIntent(pendingIntent)
                            //.setTicker(getText(R.string.ticker_text))
                            .build();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, myTagForPowerManager);
            mWakeLock.acquire();
            initSensors();

            startForeground(50, notification);
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
        proximitySwitch=intent.getIntExtra("proximitySwitch",0);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
            sManagerForProximity.unregisterListener(this);
            sManagerForAccel.unregisterListener(this);
            mWakeLock.release();
        }
        else {
            sManagerForProximity.unregisterListener(this);
            sManagerForAccel.unregisterListener(this);
            mWakeLock.release();
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

        //Restart the service once it has been killed android
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()== Sensor.TYPE_PROXIMITY){
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                }
                else if (proximitySwitch==1) {
                    getApplication().startService(new Intent(getApplication(), RingSirenService.class));
                }
            }
    }
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
                    KeyguardManager myKM = (KeyguardManager) getApplication().getSystemService(Context.KEYGUARD_SERVICE);
                    if(!myKM.inKeyguardRestrictedInputMode()) {
                        getApplication().stopService(new Intent(getApplication(),RingSirenService.class));
                        SharedPreferences.Editor editor4 = getSharedPreferences("Swch3 State", Context.MODE_PRIVATE).edit();
                        editor4.putBoolean("Swch3 State", false);
                        editor4.commit();
                        Intent goToMotionSensorService=new Intent(getApplicationContext(),ServiceForPocketSensor.class);
                        getApplication().stopService(goToMotionSensorService);
                    }

                }
            }
        }

    private void initSensors() {
        //for sensors
        sManagerForProximity = (SensorManager) getApplication().getSystemService(SENSOR_SERVICE);
        proximity = sManagerForProximity.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sManagerForProximity.registerListener(this, proximity,
                SensorManager.SENSOR_DELAY_NORMAL);


        sManagerForAccel = (SensorManager) getApplication().getSystemService(SENSOR_SERVICE);
        accelerometer = sManagerForAccel.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sManagerForAccel.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }
}
