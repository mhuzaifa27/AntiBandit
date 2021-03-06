package com.example.finalapp.ClassesForGpsLocaitonMode;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.finalapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SmsListenerForGPS extends BroadcastReceiver {
    final static double minimalAccuracy = 75.0;

    Location location = null;
    int attempt = 0;
    String phone = null;
    Context context = null;
    StorageForGPS storageForGPS = null ;
    boolean coarse = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        storageForGPS = StorageForGPS.getInstance(context);
        String action = intent.getAction();
        /*
            New SMS
         */
       // Toast.makeText(context, "in On receive of SMS Listener", Toast.LENGTH_SHORT).show();

        if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
            SmsMessage[] messages = extractSmsFromIntent(intent);
            for(SmsMessage m : messages){
                if (parseMessage(m)){

                    if (storageForGPS.hasRequestFromPhone(phone) && getActivePendingIntent() != null) {
                        sendSMS(context.getString(R.string.already_processed));
                    }

                    Location approxLocation = sendApproxLocationSms();
                    if (! coarse || approxLocation == null) {

                        if (! GPSLocator.isGpsEnabled(context)){
                            sendSMS(context.getString(R.string.gps_network_not_enabled));
                        }
                        tryGetFineLocation();
                    }
                    coarse = false;
                }
            }
        }

        /*
            Request from activity after getting permission
        */
        if(intent.getAction().equals("com.gan4x4.SEND_LOCATION")){
            readIntentExtras(intent); // Contain only phone
            if (phone != null && ! phone.isEmpty()){
                tryGetFineLocation();
            }
        }

        /*
            Request from GPS when it determine current location
        */
        if (intent.getAction().equals("com.gan4x4.LOCATION_UPDATE")){
            readIntentExtras(intent);
            if (location != null && ! storageForGPS.getCurrentRequests().isEmpty()) {
                if (location.hasAccuracy() && location.getAccuracy() < minimalAccuracy) {
                    for (String s : storageForGPS.getCurrentRequests()) {
                        phone = s;
                        sendSMS(composeSmsText(location));
                    }
                    storageForGPS.clearRequests();
                    stopService();
                }
                else{
                    Log.d("SMSListener", "Location updated but accuracy is bad:" + location.getAccuracy());
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        // In Android 8+ location periodically determining in foreground service
                        tryGetFineLocation();
                    }
                }
            }else {
                Log.d("SMSListener", "Location updated but empty");
            }

        }

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Log.d("SMSListener", "Start after boot");
            if (! storageForGPS.getCurrentRequests().isEmpty()){
                tryGetFineLocation();
            }
        }
    }


    private PendingIntent getActivePendingIntent(){
        return PendingIntent.getBroadcast(context, 0,
                new Intent("com.gan4x4.LOCATION_UPDATE"),
                PendingIntent.FLAG_NO_CREATE);
    }

    private boolean parseMessage(SmsMessage sms){
        String clearMessage = sms.getMessageBody().toLowerCase().trim();
        String[] parts = clearMessage.split(":");
        String passphrase = storageForGPS.getCurrentPassphrase();
        if (parts.length == 0){
            return false;
        }

        String messagePassphrase = parts[0];
        if (! passphrase.toLowerCase().equals(messagePassphrase)){
            return false;
        }

        this.phone = sms.getOriginatingAddress();

        // Parse params

        for(int i=1;i<parts.length;i++){

            if (parts[i].toLowerCase().equals("reset")){
                storageForGPS.removeRequest(phone);
                sendSMS("Request canceled");
                continue;
            }

            if (parts[i].toLowerCase().equals("coarse")){
                coarse = true;
                continue;
            }

            if (parts[i].toLowerCase().equals("requests")){
                sendListOfCurrentRequests();
                // Only send list of phones waiting for location
                return false;
            }
        }

        return true;
    }

    private void sendListOfCurrentRequests(){
        String text = "";
        if (storageForGPS.getCurrentRequests().isEmpty()){
            text = context.getString(R.string.sms_no_requests);
        }else{
            for(String ph : storageForGPS.getCurrentRequests()){
                text += ph + "\n";
            }
        }
        sendSMS("Current requests: \n" + text);
    }



    private Location sendApproxLocationSms(){
        String smsText;
        Location approxLocation = tryGetLastKnownLocation();
        if (approxLocation != null) {
            smsText = context.getString(R.string.sms_last_known)+" "+composeSmsText(approxLocation);
        }else{
            smsText = context.getString(R.string.sms_welcome);
        }
        sendSMS(smsText);
        return approxLocation;
    }

    private void readIntentExtras(Intent intent){
        String key = LocationManager.KEY_LOCATION_CHANGED;
        location = (Location) intent.getExtras().get(key);
        phone = intent.getExtras().getString("phone");
        attempt = intent.getExtras().getInt("attempt",0);
    }

    public SmsMessage[] extractSmsFromIntent(Intent intent){
        SmsMessage[] msgs = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        } else{
            // For ver. prior 19
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                //---retrieve the SMS message received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for(int i=0; i<msgs.length; i++){
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }
            }
        }
        return msgs;
    }

    public void tryGetFineLocation(){
        try {
            cancelOldPendingIntent();
            sendLocationRequest();
            if (phone != null) {
                storageForGPS.addRequest(phone);
            }
        } catch ( SecurityException e ) {
            openActivityToRequestPermission();
        }
    }

    private void cancelOldPendingIntent(){
        PendingIntent currentRequest = getActivePendingIntent();
        if (currentRequest != null){
            currentRequest.cancel();
        }
    }

    public Location tryGetLastKnownLocation(){
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch ( SecurityException e ) {
            return null;
        }
    }

    private void sendLocationRequest() throws SecurityException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent i = new Intent(context, GPSForegroundServiceForAndroid8.class);
            i.putExtra("phone", phone);
            context.startService(i);
        }else {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Intent intentp = new Intent("com.gan4x4.LOCATION_UPDATE");
            intentp.putExtra("phone", phone);
            intentp.putExtra("attempt", attempt++);
            intentp.putExtra("satellites", 7);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentp, PendingIntent.FLAG_UPDATE_CURRENT);
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, pendingIntent);
        }
    }

    private void openActivityToRequestPermission(){
        Intent i = new Intent();
        i.putExtra("phone",phone);
        i.setClassName(context.getPackageName(),GpsLocationMode.class.getName());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    /**
        New 2017 api  for google maps
        https://developers.google.com/maps/documentation/urls/guide#universal-cross-platform-syntax
    */
    public String composeSmsText(Location loc){
        Float accuracy = loc.getAccuracy();
        String url = "https://www.google.com/maps/search/?api=1&zoom="+getZoom(accuracy)+"&query="+loc.getLatitude()+","+loc.getLongitude();

  return url+"\n accuracy:"+accuracy+" tm: "+getTime(loc);
    }

    public int getZoom(Float accuracy){
        return 20;
    }

    public String getTime(Location loc){
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(loc.getTime());

        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        return format.format(calendar.getTime());


        //return calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.HOUR)+calendar.get(Calendar.MINUTE);

    }

    public void sendSMS(String text){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, text, null, null);
    }

    public void stopService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent i = new Intent(context, GPSForegroundServiceForAndroid8.class);
            context.stopService(i);
        }


    }
}
