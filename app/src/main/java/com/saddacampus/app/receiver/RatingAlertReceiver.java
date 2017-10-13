package com.saddacampus.app.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.saddacampus.app.R;
import com.saddacampus.app.activity.SplashActivity;

/**
 * Created by shubham on 25/7/17.
 */

public class RatingAlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(context,"Rate your previous order ", "Your feedback helps us to improve our services.",
                "Allert");
    }

    public  void createNotification(Context context, String msg, String msgTxt, String msgAlert){
        PendingIntent notificIntent = PendingIntent.getActivity(context,0,new Intent(context,SplashActivity.class),0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.saddalogo)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgTxt);


        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1,mBuilder.build());
    }
}
