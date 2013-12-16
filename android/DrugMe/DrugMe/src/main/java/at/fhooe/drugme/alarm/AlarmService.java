package at.fhooe.drugme.alarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

import at.fhooe.drugme.MedicationIntake;
import at.fhooe.drugme.R;
import at.fhooe.drugme.model.Medication;
import at.fhooe.drugme.model.MedicationPlan;

/**
 * Created by ch on 04.12.13.
 */
public class AlarmService extends Service {
    private static final int EXEC_INTERVAL = 20 * 1000;
    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        Log.d("DrugMe", "alarm intent" + AlarmService.class.getSimpleName());
        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);


        MedicationPlan plan = MedicationPlan.loadPlan(this);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("MedicationIntake: ")
                .setContentText("Events received");
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String[] events = new String[6];

        inboxStyle.setBigContentTitle("MedicationIntake: ");

        for (Medication med : plan.getCurrentMedications()) {
            inboxStyle.addLine(med.getMedication() + " intake: " + med.getNextMedicationValue());
        }

        mBuilder.setStyle(inboxStyle);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        if (prefs.getBoolean("notifications_new_message_light", false)) {
            mBuilder.setLights(getResources().getColor(R.color.light_blue), 500, 2000);
        }
        if (prefs.getBoolean("notifications_new_message_vibrate", false)) {
            mBuilder.setVibrate(new long[]{100, 200, 100, 500});
        }
        //   Cloner cloner= new Cloner();
        //MedicationPlan cur= (MedicationPlan) cloner.deepClone(plan.getCurrentMedications());
        Intent resultIntent = new Intent(this, MedicationIntake.class);

        resultIntent.putExtra("plan", plan);
        //resultIntent.putExtra(CommonConstants.EXTRA_MESSAGE, msg);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Because clicking the notification launches a new ("special") activity,
        // there's no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);


        mManager.notify(0, mBuilder.build());

        //TODO call alarm manager for next event
//        rescheduleAlarm();

        return START_STICKY;
    }

    ;

    private void rescheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this.getApplicationContext(), Alarm.class);
        PendingIntent intentExecuted = PendingIntent.getBroadcast(this.getApplicationContext(), 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, 20);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
                EXEC_INTERVAL, intentExecuted);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


}
