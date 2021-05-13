package com.example.finalapp;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class LockMonitor extends Service {

    public static final String TAG = "LockMonitor";

    public static final String ACTION_CHECK_LOCK = "com.sample.screenmonitor.LockMonitor.ACTION_CHECK_LOCK";
    public static final String EXTRA_CHECK_LOCK_DELAY_INDEX = "com.sample.screenmonitor.LockMonitor.EXTRA_CHECK_LOCK_DELAY_INDEX";
    public static final String EXTRA_STATE = "com.sample.screenmonitor.LockMonitor.EXTRA_STATE";

    BroadcastReceiver receiver = null;
    static final Timer timer = new Timer();
    CheckLockTask checkLockTask = null;

    public LockMonitor() {
        Log.d(TAG, "LockMonitor constructor");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "LM.onDestroy");
        super.onDestroy();

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "LM.onStartCommand");

        if (intent != null && intent.getAction() == ACTION_CHECK_LOCK) {
            checkLock(intent);
        }

        if (receiver == null) {
            // Unlike other broad casted intents, for these you CANNOT declare them in the Android Manifest;
            // instead they must be registered in an IntentFilter.
            receiver = new StateReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            registerReceiver(receiver, filter);
        }

        return START_STICKY;
    }

    void checkLock(final Intent intent) {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        final boolean isProtected = keyguardManager.isKeyguardSecure();
        final boolean isLocked = keyguardManager.inKeyguardRestrictedInputMode();
        final boolean isInteractive = powerManager.isInteractive();
        final int delayIndex = getSafeCheckLockDelay(intent.getIntExtra(EXTRA_CHECK_LOCK_DELAY_INDEX, -1));
        Log.i(TAG,
                String.format("LM.checkLock with state=%s, isProtected=%b, isLocked=%b, isInteractive=%b, delay=%d",
                        intent != null ? intent.getStringExtra(EXTRA_STATE) : "",
                        isProtected, isLocked, isInteractive, checkLockDelays[delayIndex])
        );

        if (checkLockTask != null) {
            Log.i(TAG, String.format("LM.checkLock: cancelling CheckLockTask[%x]", System.identityHashCode(checkLockTask)));
            checkLockTask.cancel();
        }

        if (isProtected && !isLocked && !isInteractive) {
            checkLockTask = new CheckLockTask(this, delayIndex);
            Log.i(TAG, String.format("LM.checkLock: scheduling CheckLockTask[%x] for %d ms", System.identityHashCode(checkLockTask), checkLockDelays[delayIndex]));
            timer.schedule(checkLockTask, checkLockDelays[delayIndex]);
            getApplicationContext().stopService(new Intent(getApplicationContext(), RingSirenService.class));
            final Intent newIntent = new Intent(getApplicationContext(), LockMonitor.class);
            newIntent.setAction(LockMonitor.ACTION_CHECK_LOCK);
            getApplicationContext().stopService(newIntent);

        } else {
            Log.d(TAG, "LM.checkLock: no need to schedule CheckLockTask");
            if (isProtected && isLocked) {
                Log.e(TAG, "Do important stuff here!");
            }
        }
    }

    static final int SECOND = 1000;
    static final int MINUTE = 60 * SECOND;
    // This tracks the deltas between the actual options of 5s, 15s, 30s, 1m, 2m, 5m, 10m
    // It also includes an initial offset and some extra times (for safety)
    static final int[] checkLockDelays = new int[] { 1*SECOND, 5*SECOND, 10*SECOND, 20*SECOND, 30*SECOND, 1*MINUTE, 3*MINUTE, 5*MINUTE, 10*MINUTE, 30*MINUTE };
    static int getSafeCheckLockDelay(final int delayIndex) {
        final int safeDelayIndex;
        if (delayIndex >= checkLockDelays.length) {
            safeDelayIndex = checkLockDelays.length - 1;
        } else if (delayIndex < 0) {
            safeDelayIndex = 0;
        } else {
            safeDelayIndex = delayIndex;
        }
        Log.v(TAG, String.format("getSafeCheckLockDelay(%d) returns %d", delayIndex, safeDelayIndex));
        return safeDelayIndex;
    }

    class CheckLockTask extends TimerTask {
        final int delayIndex;
        final Context context;
        CheckLockTask(final Context context, final int delayIndex) {
            this.context = context;
            this.delayIndex = delayIndex;
        }
        @Override
        public void run() {
            Log.i(TAG, String.format("CLT.run [%x]: redirect intent to LockMonitor", System.identityHashCode(this)));
            final Intent newIntent = new Intent(context, LockMonitor.class);
            newIntent.setAction(ACTION_CHECK_LOCK);
            newIntent.putExtra(EXTRA_CHECK_LOCK_DELAY_INDEX, getSafeCheckLockDelay(delayIndex + 1));
            context.startService(newIntent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "LM.onBind");
        return null;
    }
}
