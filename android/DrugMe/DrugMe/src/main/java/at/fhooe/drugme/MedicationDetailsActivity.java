package at.fhooe.drugme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

    @InjectView(R.id.medication_frequency_monday)
    ImageView calendarMondayView;
    @InjectView(R.id.medication_frequency_tuesday)
    ImageView calendarTuesdayView;
    @InjectView(R.id.medication_frequency_wednesday)
    ImageView calendarWednesdayView;
    @InjectView(R.id.medication_frequency_thursday)
    ImageView calendarThursdayView;
    @InjectView(R.id.medication_frequency_friday)
    ImageView calendarFridayView;
    @InjectView(R.id.medication_frequency_saturday)
    ImageView calendarSaturdayView;
    @InjectView(R.id.medication_frequency_sunday)
    ImageView calendarSundayView;

    @InjectView(R.id.medication_detail_pill)
    TextView pillView;

    @InjectView(R.id.medication_detail_time)
    TextView timeView;

    @InjectView(R.id.medication_detail_info)
    TextView infoView;

    @InjectView(R.id.medication_detail_icon)
    ImageView medicationImage;
    Medication medication = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.inject(this);
        medication = getIntent().getParcelableExtra("medication");

        nameView.setText(medication.getMedication());
        //12/24/2013
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
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

        pillView.setText(String.format("%d pills", medication.getNextMedicationValue()));


        String[] frequencies = medication.getFrequency().split("-");
        if (frequencies.length == 7) {
            if (frequencies[0].equalsIgnoreCase("1")) {
                calendarMondayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_active));
            } else {
                calendarMondayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_inactive));
            }
            if (frequencies[1].equalsIgnoreCase("1")) {
                calendarTuesdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_active));
            } else {
                calendarTuesdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_inactive));
            }
            if (frequencies[2].equalsIgnoreCase("1")) {
                calendarWednesdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_active));
            } else {
                calendarWednesdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_inactive));
            }
            if (frequencies[3].equalsIgnoreCase("1")) {
                calendarThursdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_active));
            } else {
                calendarThursdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_inactive));
            }
            if (frequencies[4].equalsIgnoreCase("1")) {
                calendarFridayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_active));
            } else {
                calendarFridayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_inactive));
            }
            if (frequencies[5].equalsIgnoreCase("1")) {
                calendarSaturdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_active));
            } else {
                calendarSaturdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_inactive));
            }
            if (frequencies[6].equalsIgnoreCase("1")) {
                calendarSundayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_active));
            } else {
                calendarSundayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_inactive));
            }

        }


        Picasso.with(this).load(String.format("http://%s/medications/%s/img",getString(R.string.api_base_url), medication.getMedication())).into(medicationImage);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                calendarMondayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_today));
                break;
            case Calendar.TUESDAY:
                calendarTuesdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_today));
                break;
            case Calendar.WEDNESDAY:
                calendarWednesdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_today));
                break;
            case Calendar.THURSDAY:
                calendarThursdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_today));
                break;
            case Calendar.FRIDAY:
                calendarFridayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_today));
                break;
            case Calendar.SATURDAY:
                calendarSaturdayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_today));
                break;
            case Calendar.SUNDAY:
                calendarSundayView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_calendar_today));
                break;


        } 
        infoView.setText(medication.getInfo());

    }
}
