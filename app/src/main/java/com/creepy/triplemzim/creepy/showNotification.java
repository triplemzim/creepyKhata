package com.creepy.triplemzim.creepy;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;


import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * created by Zim on 12/3/2017.
 */

public class showNotification {
    public static void onStart(Context context, String msg){

        PendingIntent pIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_UPDATE_CURRENT,
                new Intent(context, LoginActivity.class), 0);


        Notification n = new Notification.Builder(context)
                .setContentTitle("HajiraKhata")
                .setContentText("Login " + msg)
                .setSmallIcon(R.drawable.ic_transparent)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_transparent))
                .setContentIntent(pIntent)
                .setAutoCancel(false)
                .build();



        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Cancel the notification after its selected
        n.flags |= Notification.FLAG_AUTO_CANCEL;
//        n.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(0, n);





//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(context)
//                        .setSmallIcon(R.drawable.ic_creepy)
//                        .setAutoCancel(true)
//                        .setContentTitle("Hajirakhata")
//                        .setContentText("Login " + msg);
//        Intent resultIntent = new Intent(context, LoginActivity.class);
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(LoginActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//
//
//        mNotificationManager.notify(notificationID, mBuilder.build());
    }
}
