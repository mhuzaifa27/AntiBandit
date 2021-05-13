package com.example.finalapp.ClassesForGpsLocaitonMode;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.finalapp.R;

import java.util.HashSet;
import java.util.Set;

public class StorageForGPS {
    final static String PASSPHRASE_KEY = "passPhraseForGPS";
    final static String REQUESTS_KEY = "requests";
    final static String PREFERENCES_KEY = "SMSLocator_SP";

    final static String FIRST_START_KEY = "first_start";

    Context context;
    private SharedPreferences prefs;
    private static StorageForGPS _storageForGPS;

    private StorageForGPS(Context context){
        this.context = context;
        prefs = context.getSharedPreferences(PREFERENCES_KEY, 0);
    }

    public static StorageForGPS getInstance(Context context){
        if (StorageForGPS._storageForGPS == null){
            _storageForGPS = new StorageForGPS(context.getApplicationContext());
        }
        return _storageForGPS;
    }

    protected boolean isFirstStart(){
        return prefs.getBoolean(FIRST_START_KEY,true);
    }

    public String getCurrentPassphrase(){
        return prefs.getString(PASSPHRASE_KEY,context.getString(R.string.default_passphrase));
    }

    public void savePassphrase(String phrase){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PASSPHRASE_KEY, phrase);
        editor.commit();
    }


    public boolean hasRequestFromPhone(String phone){
        return getCurrentRequests().contains(phone);
    }

    public void addRequest(String phone){
        if (phone == null){
            return;
        }
        Set<String> currentRequests = getCurrentRequests();
        if (! currentRequests.contains(phone)){
            currentRequests.add(phone);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet(REQUESTS_KEY, currentRequests);
            editor.commit();
        }
    }

    public void removeRequest(String phone){
        Set<String> currentRequests = getCurrentRequests();
        if (currentRequests.contains(phone)){
            currentRequests.remove(phone);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet(REQUESTS_KEY, currentRequests);
            editor.commit();
        }
    }

    public Set<String> getCurrentRequests(){
        return prefs.getStringSet(REQUESTS_KEY,new HashSet<String>());
    }

    public void clearRequests(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(REQUESTS_KEY, null);
        editor.commit();
    }

}
