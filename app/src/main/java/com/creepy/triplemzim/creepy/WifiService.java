package com.creepy.triplemzim.creepy;

import android.app.Service;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by HP on 11/30/2017.
 */

public class WifiService extends JobService {

    private UserLogin userLogin = null;
    private static final String TAG = "WifiService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Log.d(TAG, "onStartJob: job started");

        if(checkValidity() == false) return false;

        Log.d(TAG, "onStartJob: validity true");
        userLogin = new UserLogin(getApplicationContext(), true);
        userLogin.execute((Void) null);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    private boolean checkValidity() {
        WifiManager wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        if(ssid.contains("ReveSystems") == false){
            return false;
        }

       return true;
    }


}
