package at.fhooe.drugme.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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

        new AsyncTask<String, Void, String>() {
            String TAG="DragMe";

            @Override
            protected String doInBackground(String... urls) {
                OkHttpClient client = new OkHttpClient();

                String response = "";
                for (String url : urls) {

                    HttpURLConnection connection =null;
                    InputStream in = null;
                    try {
                        connection=client.open(new URL(url));
                        // Read the response.

                        in = connection.getInputStream();

                        Reader r=new InputStreamReader(in);

                        return readFully(r);
                    }
                    catch (MalformedURLException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    catch(Exception e){
                        Log.e( TAG,e.getMessage());

                    }
                    finally {

                        if (in != null) try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return response;
            }

            @Override
            protected void onPostExecute(String result) {
                Gson gson = new Gson();
                MedicationPlan plan=gson.fromJson(result, MedicationPlan.class);

                // We need an Editor object to make preference changes.
                // All objects are from android.context.Context

                SharedPreferences settings = getSharedPreferences(getString(R.string.shared_pref_name), 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString(getString(R.string.pref_medication_plan),result);

                // Commit the edits!
                editor.commit();
                Log.d(TAG,"stored new medication plan");

                BusProvider.getInstance().post(plan);
            }

        }.execute(new String[]{url});

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

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
