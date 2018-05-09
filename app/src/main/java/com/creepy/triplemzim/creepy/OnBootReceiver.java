package com.creepy.triplemzim.creepy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {

    private final String TAG = "OnBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent backgroundService = new Intent(context, KeepAliveService.class);
        context.startService(backgroundService);
        Log.d(TAG, "onReceive: starting background service");
    }
}
