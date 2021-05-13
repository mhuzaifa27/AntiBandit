package com.example.finalapp.ClassesForRingAlarmMode;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.finalapp.R;

import java.util.HashSet;
import java.util.Set;

public class StorageForRingAlarm {
    final static String PASSPHRASE_KEY = "passPhraseForRingAlarm";
    final static String REQUESTS_KEY = "requests";
    final static String PREFERENCES_KEY = "SMSRingAlarm_SP";
    final static String FIRST_START_KEY = "first_start";

    Context context;
    private SharedPreferences p;
    private static StorageForRingAlarm storageForRingAlarm;

    private StorageForRingAlarm(Context context){
        this.context = context;
        p = context.getSharedPreferences(PREFERENCES_KEY,Context.MODE_PRIVATE);
    }

    public static StorageForRingAlarm getInstance(Context context){
        if (StorageForRingAlarm.storageForRingAlarm == null){
            storageForRingAlarm = new StorageForRingAlarm(context.getApplicationContext());
        }
        return storageForRingAlarm;
    }
    protected boolean isFirstStart(){
        return p.getBoolean(FIRST_START_KEY,true);
    }


    public String getCurrentPassphrase(){
        return p.getString(PASSPHRASE_KEY,context.getString(R.string.default_passphrase_for_ring_alarm));
    }

    public void savePassphrase(String phrase){
        SharedPreferences.Editor editor = p.edit();
        editor.putString(PASSPHRASE_KEY, phrase);
        editor.commit();
    }



    public void removeRequest(String phone){
        Set<String> currentRequests = getCurrentRequests();
        if (currentRequests.contains(phone)){
            currentRequests.remove(phone);
            SharedPreferences.Editor editor = p.edit();
            editor.putStringSet(REQUESTS_KEY, currentRequests);
            editor.commit();
        }
    }

    public Set<String> getCurrentRequests(){
        return p.getStringSet(REQUESTS_KEY,new HashSet<String>());
    }
}
