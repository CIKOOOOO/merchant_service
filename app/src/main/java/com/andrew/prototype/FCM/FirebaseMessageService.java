package com.andrew.prototype.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.andrew.prototype.Activity.MainActivity;
import com.andrew.prototype.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendMyNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void sendMyNotification(String title, String message) {
        String CHANNEL_ID = "my_channel_01";
        CharSequence charSequence = "Punya Andrew";

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_bca)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bca))
                .setContentTitle(title)
                .setContentText(message)
                .setChannelId(CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(new long[0])
                .setContentIntent(pendingIntent);

        NotificationChannel mChannel;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, charSequence, NotificationManager.IMPORTANCE_HIGH);
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100,200,300,400});
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        /*
         * It will run, when apps is installed for the first time only or when token changes
         * */
        Log.e("asd", "New Token : " + s);
    }
}
