package e.valka.taxver.Notifications.Azure;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.microsoft.windowsazure.notifications.NotificationsHandler;

import e.valka.taxver.R;
import e.valka.taxver.navigationActivity;

public class MyHandler extends NotificationsHandler {
    private static final int NOTIFICATION_ID = 1;
    private Context context;

    @Override
    public void onReceive (Context context, Bundle bundle) {
        this.context = context;

        String nhMessage = bundle.getString ("message");
        Log.i ("PEKA", nhMessage);

        if (navigationActivity.isVisible) {
            navigationActivity.mainActivity.ToastNotify (nhMessage);
        }else{
            if(navigationActivity.mainActivity.getID().equals(nhMessage))
                sendNotification (nhMessage);
        }
    }

    private void sendNotification (String msg) {
        Intent intent = new Intent (context, navigationActivity.class);
        intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity (context, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            if (mNotificationManager == null) return;

            if (mNotificationManager.getNotificationChannel (context.getString (R.string.notitication_channel_id)) == null) {
                String channel_id = context.getString (R.string.notitication_channel_id);
                CharSequence channel_description = context.getString (R.string.notification_channel_name);
                int channel_importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel channel = new NotificationChannel(channel_id, channel_description, channel_importance);
                mNotificationManager.createNotificationChannel (channel);
            }
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri (RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder (context, context.getString (R.string.notitication_channel_id));
        mBuilder.setSmallIcon (R.mipmap.ic_launcher)
                .setContentTitle ("Notification Hub Demo")
                .setStyle (new NotificationCompat.BigTextStyle ().bigText (msg))
                .setAutoCancel (true)
                .setSound (defaultSoundUri)
                .setContentText (msg);

        mBuilder.setContentIntent (contentIntent);

        if (mNotificationManager != null)
            mNotificationManager.notify (NOTIFICATION_ID, mBuilder.build ());
    }
}
