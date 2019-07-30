package com.creepy.triplemzim.creepy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Zim on 11/30/2017.
 */

public class WifiService extends JobService {

    private UserLogin userLogin = null;
    private static final String TAG = "WifiService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Log.d(TAG, "onStartJob: job started");

        if(!checkValidity()) return false;

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

        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = false;
        if(activeNetwork != null){
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }

        if(ssid.contains("ReveSystems") && isWiFi){
            return true;
        }

       return false;
    }


}
