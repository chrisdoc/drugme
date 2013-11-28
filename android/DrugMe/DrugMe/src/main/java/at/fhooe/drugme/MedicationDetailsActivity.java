package at.fhooe.drugme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.JodaTimePermission;

import at.fhooe.drugme.model.Medication;
import butterknife.ButterKnife;
import butterknife.InjectView;

import org.joda.time.*;
import org.joda.time.chrono.StrictChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ch on 28.11.13.
 */
public class MedicationDetailsActivity extends Activity {


    @InjectView(R.id.medication_detail_name)
    TextView nameView;
    @InjectView(R.id.medication_detail_startdate)
    TextView startdateView;
    @InjectView(R.id.medication_detail_enddate)
    TextView enddateView;
    @InjectView(R.id.medication_detail_timepassed)
    View timepassedView;
    @InjectView(R.id.medication_detail_timetogo)
    View timetogoView;


    @InjectView(R.id.medication_detail_pill)
    TextView pillView;

    @InjectView(R.id.medication_detail_time)
    TextView timeView;
    Medication medication = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.inject(this);
        medication = getIntent().getParcelableExtra("medication");

        nameView.setText(medication.getName());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate = null;
        Date enddate = null;
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        try {
            startdate = sdf.parse(medication.getStartdate());
            startdateView.setText(dateFormat.format(startdate));

        } catch (Exception e) {
            // handle exception here !
        }

        try {
            enddate = sdf.parse(medication.getEnddate());
            enddateView.setText(dateFormat.format(enddate));
        } catch (Exception e) {

        }
        if (enddate != null && startdate != null) {
            long end = enddate.getTime();
            long now = new Date().getTime();
            long start = startdate.getTime();
            start = now - start;
            end = end - now;
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    20, (float) start / (float) (start + end));
            timepassedView.setLayoutParams(param);
            param = new LinearLayout.LayoutParams(
                    0,
                    20, (float) end / (float) (start + end));
            timetogoView.setLayoutParams(param);
        }


        DateTime next = new DateTime(medication.getNextMedicationTime());
        DateTime now = new DateTime(new Date());
        Period diff = new Period(now, next);
        if (diff.getDays() > 0)
            timeView.setText(String.format("%d days %d hours %d minutes", diff.getDays(), diff.getHours(), diff.getMinutes()));
        else
            timeView.setText(String.format("%d hours %d minutes", diff.getHours(), diff.getMinutes()));

        pillView.setText(String.format("%d pills",medication.getNextMedicationValue()));

    }
}
