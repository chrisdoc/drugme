package at.fhooe.drugme.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import at.fhooe.drugme.BusProvider;
import at.fhooe.drugme.DrugMeActivity;
import at.fhooe.drugme.R;
import at.fhooe.drugme.model.MedicationPlan;
import at.fhooe.drugme.model.MedicationTask;
import at.fhooe.drugme.model.MedicationTaskListener;

import static com.squareup.okhttp.internal.Util.readFully;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private String TAG="DrugMe";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                // Post notification of received message.


                String method=extras.getString("method");
                if(method.equalsIgnoreCase("updateMedication")){
                    loadMedicationPlan(extras.getString("url"));

                    sendNotification("Your medication plan has been updated :)");

                }
                else{
                    sendNotification("gcm received: "+extras.toString());
                }

                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void loadMedicationPlan(String url) {


        MedicationTask task = new MedicationTask();
        task.setListener(new MedicationTaskListener() {
            @Override
            public void medicationTaskFinished(MedicationPlan _plan) {
                SharedPreferences settings = getSharedPreferences(getString(R.string.shared_pref_name), 0);
                SharedPreferences.Editor editor = settings.edit();


                editor.putString(getString(R.string.pref_medication_plan),new Gson().toJson(_plan));

                // Commit the edits!
                editor.commit();
                Log.d(TAG,"stored new medication plan");

                BusProvider.getInstance().post(_plan);
            }
        });
        task.execute(new String[]{url});


    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DrugMeActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("DrugMe")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        SharedPreferences prefs= PreferenceManager
                .getDefaultSharedPreferences(this);

        if(prefs.getBoolean("notifications_new_message_light",false)){
            mBuilder.setLights(getResources().getColor(R.color.light_blue), 500, 2000);
        }
        if(prefs.getBoolean("notifications_new_message_vibrate",false)){
            mBuilder.setVibrate(new long[]{100, 200, 100, 500});
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
