package com.creepy.triplemzim.creepy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.text.DateFormat;
import java.util.Date;

/**
 * created by Zim on 11/30/2017.
 */

public class WifiReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiReceiver";
    public FirebaseJobDispatcher dispatcher;
    public Job job;
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive: Broadcast Received ");

        WifiManager wifiManager= (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        Log.d(TAG, "onReceive: ssid: " + ssid);
//        Log.d(TAG, "onReceive: " +String.valueOf(LoginActivity.autoCheck));
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SharedPreferences pref = context.getSharedPreferences("LoginActivity",Context.MODE_PRIVATE);
        String temp = pref.getString("autoCheck", "false");
        Boolean autoCheck;
//        if(temp.equals("false")){
//            autoCheck = false;
//        }
//        else autoCheck = true;
        autoCheck = !temp.equals("false");

        if(!ssid.contains("ReveSystems") || !autoCheck){
            Log.d(TAG, "onReceive: Cancelling broadcast");
            try {
                dispatcher.cancelAll();
            }catch (Exception e){
                //do nothing
            }
        }
        else{
            if(pref.getString("lastDate","a").equals(
                    DateFormat.getDateInstance().format(new Date()).toString())){
                return;    
            }

            dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
            job = dispatcher.newJobBuilder()
                    // the JobService that will be called
                    .setService(WifiService.class)
                    // uniquely identifies the job
                    .setTag("wifi-service")
                    // one-off job
                    .setRecurring(false)
                    // don't persist past a device reboot
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                    // start between 0 and 60 seconds from now
                    .setTrigger(Trigger.NOW)
                    // don't overwrite an existing job with the same tag
                    .setReplaceCurrent(true)
                    // retry with exponential backoff
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    .build();


            Log.d(TAG, "onReceive: calling jobservice");
            dispatcher.mustSchedule(job);
        }
    }
}
