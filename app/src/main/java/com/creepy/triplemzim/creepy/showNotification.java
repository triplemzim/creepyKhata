package com.creepy.triplemzim.creepy;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * created by Zim on 12/3/2017.
 */

public class showNotification {

    private static final String CHANNEL_ID = "CreepyKhata";
    public static void onStart(Context context, String msg){

//        PendingIntent pIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_UPDATE_CURRENT,
//                new Intent(context, LoginActivity.class), 0);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

//        Notification n = new Notification.Builder(context)
//                .setContentTitle("HajiraKhata")
//                .setContentText("Login " + msg)
//                .setSmallIcon(R.drawable.ic_transparent)
////                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_transparent))
//                .setContentIntent(pIntent)
//                .setAutoCancel(false)
//                .build();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_transparent)
                .setContentTitle(context.getString(R.string.hajirakhata))
                .setContentText("Login " + msg)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(0 , mBuilder.build());

//        NotificationManager notificationManager =
//                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        // Cancel the notification after its selected
//        n.flags |= Notification.FLAG_AUTO_CANCEL;
////        n.flags |= Notification.FLAG_NO_CLEAR;
//        notificationManager.notify(0, n);





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
