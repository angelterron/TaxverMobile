package e.valka.taxver.Notifications.Azure;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.microsoft.windowsazure.messaging.NotificationHub;

import e.valka.taxver.R;
import e.valka.taxver.navigationActivity;

public class RegistrationIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 1;
    public RegistrationIntentService () {
        super (navigationActivity.TAG);
    }

    @Override
    protected void onHandleIntent (@Nullable Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (this);
        String resultString;
        String regID;
        String storeToken;

        try {
            //if (task.getResult () == null) throw new IllegalArgumentException ("Task's result missing.");

            //String FCM_token = task.getResult().getToken ();

            String FCM_token = FirebaseInstanceId.getInstance().getToken ();
            NotificationHub hub;

            if ((regID = sharedPreferences.getString("registrationID", null)) == null) {

                hub = new NotificationHub (NotificationSettings.HubName,
                        NotificationSettings.HubListenConnectionString, this);

                Log.i ("PEKA", "Attempting a new registration with NH using FCM token: " + FCM_token);
                regID = hub.register (FCM_token).getRegistrationId ();

                resultString = "New NH registration Successfully - RegId: " + regID;
                Log.i ("PEKA", resultString);

                sharedPreferences.edit ().putString ("registrationID", regID).apply ();
                sharedPreferences.edit ().putString ("FCMtoken", FCM_token).apply ();

            } else if (!(storeToken  = sharedPreferences.getString ("FCMtoken", "")).equals (FCM_token)) {

                hub = new NotificationHub (NotificationSettings.HubName,
                        NotificationSettings.HubListenConnectionString, this);

                Log.i ("PEKA", "NH registration refreshing with token: " + FCM_token);
                regID = hub.register (FCM_token).getRegistrationId ();

                resultString = "New registration successfully - RegId " + regID;
                Log.i ("PEKA", resultString);

                sharedPreferences.edit ().putString ("registrationID", resultString).apply ();
                sharedPreferences.edit ().putString ("FCMtoken", FCM_token).apply ();

            } else {

                resultString = "Previously registered successfully - RedId " + regID;
                Log.i ("PEKA", resultString);

            }
        } catch (Exception e) {
            Log.e ("PEKA", resultString = "Failed to complete registration", e);
        }

        if (navigationActivity.isVisible) {
            navigationActivity.mainActivity.ToastNotify (resultString);
        }
    }
}