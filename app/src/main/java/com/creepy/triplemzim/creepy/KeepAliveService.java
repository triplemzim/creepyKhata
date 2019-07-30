package com.creepy.triplemzim.creepy;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

public class KeepAliveService extends Service {

    private WifiReceiver wifiReceiver = null;
    private final String TAG = "KeepAliveService";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        wifiReceiver = new WifiReceiver();
        IntentFilter wifiIntentFilter = new IntentFilter();
        wifiIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, wifiIntentFilter);
        Log.d(TAG, "onCreate: created WifiReceiver");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wifiReceiver != null){
            unregisterReceiver(wifiReceiver);
        }

        Log.d(TAG, "onDestroy: unregistering wifi broadcast and dying now...");

    }
}
