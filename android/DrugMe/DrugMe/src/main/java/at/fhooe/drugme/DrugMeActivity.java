package at.fhooe.drugme;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import at.fhooe.drugme.alarm.Alarm;
import at.fhooe.drugme.alarm.AlarmService;
import at.fhooe.drugme.model.Medication;
import at.fhooe.drugme.model.MedicationPlan;
import at.fhooe.drugme.utils.UserProfileLoader;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import static com.squareup.okhttp.internal.Util.readFully;

import com.google.android.gms.*;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

public class DrugMeActivity extends Activity {


    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "903890830463";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "DrugMe";


    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;

    @InjectView(R.id.medication_listview)
    ListView medicationListView;


    MedicationAdapter mAdapter;
    MedicationPlan mPlan;
    List<Medication> mMedications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);


        context = getApplicationContext();

        mPlan = new MedicationPlan();
        mMedications = new ArrayList<Medication>();

        //mMedicationList= new ArrayList<MedicationIntake>();
        mAdapter = new MedicationAdapter(this, R.layout.row_medication_list, mMedications);
        mAdapter.sort(new Comparator<Medication>() {
            @Override
            public int compare(Medication lhs, Medication rhs) {

                return new DateTime(lhs.getNextMedicationTime()).compareTo(new DateTime(rhs));

            }
        });
        medicationListView.setAdapter(mAdapter);
        medicationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(DrugMeActivity.this, MedicationDetailsActivity.class);
                i.putExtra("medication", mMedications.get(position));
                startActivity(i);
            }
        });
        loadMedicationPlan();

        testAlarm();

    }

    private void testAlarm() {

/*
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND,20);

        Intent myIntent = new Intent(DrugMeActivity.this, AlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DrugMeActivity.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

        Log.d(TAG,"calendar "+calendar.getTime().toLocaleString());
*/

int i=10;
        Intent intent = new Intent(this, Alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (i * 1000), pendingIntent);
        Toast.makeText(this, "Alarm set in " + i + " seconds",
                Toast.LENGTH_LONG).show();

    }

    private void loadMedicationPlan() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_pref_name), 0);
                String json = prefs.getString(getString(R.string.pref_medication_plan), "");
                if (json.isEmpty()) {
                    mPlan = new MedicationPlan();
                    mMedications.clear();
                    mMedications.addAll(mPlan.getCurrentMedications());

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter.notifyDataSetChanged();

                        }
                    });
                } else {
                    mPlan = new Gson().fromJson(json, MedicationPlan.class);
                    mMedications.clear();
                    mMedications.addAll(mPlan.getCurrentMedications());

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter.notifyDataSetChanged();

                            mAdapter.sort(new Comparator<Medication>() {
                                @Override
                                public int compare(Medication lhs, Medication rhs) {

                                    return new DateTime(lhs.getNextMedicationTime()).compareTo(new DateTime(rhs.getNextMedicationTime()));

                                }
                            });
                        }
                    });

                }
                return null;
            }
        }.execute(null, null, null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        // Unregister the listener whenever a key changes
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drug_me, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this,SettingsActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Subscribe
    public void newMedicationPlan(MedicationPlan plan) {
        mPlan = plan;
        //mMedicationList.clear();
        //mMedicationList.addAll(plan.getMedications());
        Log.d(TAG, "otto " + plan);
        mMedications.clear();
        mMedications.addAll(mPlan.getCurrentMedications());

        mAdapter.notifyDataSetChanged();
        mAdapter.sort(new Comparator<Medication>() {
            @Override
            public int compare(Medication lhs, Medication rhs) {
                try {
                    return new DateTime(lhs.getNextMedicationTime()).compareTo(new DateTime(rhs));
                } catch (Exception e) {
                    return -1;
                }


            }
        });
        Crouton.makeText(this, "MedicationIntake plan has been updated", Style.INFO).show();
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            OkHttpClient client = new OkHttpClient();

            String response = "";
            for (String url : urls) {

                HttpURLConnection connection = null;
                InputStream in = null;
                try {
                    connection = client.open(new URL(url));
                    // Read the response.

                    in = connection.getInputStream();

                    Reader r = new InputStreamReader(in);


                    return readFully(r);//new String(responseB, "UTF-8");
                } catch (MalformedURLException e) {
                    Log.e(getString(R.string.app_name), e.getMessage());
                } catch (Exception e) {
                    Log.e(getString(R.string.app_name), e.getMessage());

                } finally {

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
            MedicationPlan plan = gson.fromJson(result, MedicationPlan.class);
            Log.d(getString(R.string.app_name), result);
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
