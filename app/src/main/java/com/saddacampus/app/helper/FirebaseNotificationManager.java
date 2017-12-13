package com.saddacampus.app.helper;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.saddacampus.app.activity.MainActivity;
import com.saddacampus.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Devesh Anand on 03-09-2017.
 */

public class FirebaseNotificationManager {

    private static final String TAG = FirebaseNotificationManager.class.getSimpleName();

    public void createNotification(Context context, String type, RemoteMessage.Notification notification, Map data){
        Log.d(TAG,"resting");
        if(type.equals("new_order")){
            createNewOrderNotification(context,data);
        }
    }

    void createNewOrderNotification(Context context, Map data) {

        Log.d(TAG,data.toString());


        String title = data.get("title").toString();
        String college = data.get("college").toString();
        String hostel = data.get("hostel").toString();
        String total = data.get("total").toString();
        String mode = data.get("mode").toString();

        String body = college+"\t\t\t\t"+hostel+'\n'+total+"\t\t\t\t"+mode+'\n';

        JSONArray items;
        String itemsInfo="";

        try {
            items = new JSONArray(data.get("items").toString());
            for(int i=0 ;i<items.length(); i++){
                JSONObject itemInfo = items.getJSONObject(i);
                String itemQuantity = itemInfo.getString("quantity");
                JSONObject item = itemInfo.getJSONObject("info");
                String itemName = item.getString("item_name");

                itemsInfo = itemsInfo.concat(itemName).concat(" * ").concat(itemQuantity)+'\n';
            }
        }catch (JSONException e){
            Log.d(TAG,e.getMessage());
        }

        body = body + itemsInfo;

        Intent intent = new Intent(context, MainActivity.class);
        Intent intent1 = new Intent(context,MainActivity.class);
        Intent intent2 = new Intent(context,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 1 /* Request code */, intent1,
                PendingIntent.FLAG_ONE_SHOT);

        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 2 /* Request code */, intent2,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri=
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(body);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(bigTextStyle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .addAction(R.mipmap.ic_launcher_round,"Confirm",pendingIntent1)
                .addAction(R.mipmap.ic_launcher_round,"Decline",pendingIntent2)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true);

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}
