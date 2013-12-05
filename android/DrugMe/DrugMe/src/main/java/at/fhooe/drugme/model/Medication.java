package at.fhooe.drugme.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import at.fhooe.drugme.DrugMeActivity;

public class Medication implements Parcelable {

    @Expose
    private String name;
    @Expose
    private String patient;
    @Expose
    private String medication;
    @Expose
    private String dose;
    @Expose
    private String intake;
    @Expose
    private String frequency;
    @Expose
    private String startdate;
    @Expose
    private String enddate;
    @Expose
    private String info;
    @Expose
    private String _id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getIntake() {
        return intake;
    }

    public void setIntake(String intake) {
        this.intake = intake;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(name);
        dest.writeString(patient);
        dest.writeString(medication);
        dest.writeString(dose);
        dest.writeString(intake);
        dest.writeString(frequency);
        dest.writeString(startdate);
        dest.writeString(enddate);
        dest.writeString(info);
        dest.writeString(_id);

    }

    public static final Parcelable.Creator<Medication> CREATOR
            = new Parcelable.Creator<Medication>() {
        public Medication createFromParcel(Parcel in) {
            return new Medication(in);
        }

        public Medication[] newArray(int size) {
            return new Medication[size];
        }
    };


    private Medication(Parcel in) {
        name = in.readString();
        patient = in.readString();
        medication = in.readString();
        dose = in.readString();
        intake = in.readString();
        frequency = in.readString();
        startdate = in.readString();
        enddate = in.readString();
        info = in.readString();
        _id = in.readString();

    }

    public Date getNextMedicationTime() {
        String[] intakes = intake.split("-");
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        ArrayList<Date> times = new ArrayList<Date>();

        if (!intakes[0].equals("0")) {
            calendar.setTime(now);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            if (hours >= 8) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            times.add(calendar.getTime());

        }
        if (!intakes[1].equals("0")) {
            calendar.setTime(now);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            if (hours >= 12) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            times.add(calendar.getTime());
        }
        if (!intakes[2].equals("0")) {
            calendar.setTime(now);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            if (hours >= 18) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            times.add(calendar.getTime());
        }

        Collections.sort(times);
        return times.get(0);
    }
    public Date getStartDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;


        try {
            date = sdf.parse(startdate);


        } catch (Exception e) {
            // handle exception here !
        }
        return date;
    }
    public Date getEndDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;

        try {
            date = sdf.parse(enddate);


        } catch (Exception e) {
            // handle exception here !
        }
        return date;
    }
    public int getNextMedicationValue() {

        DateTime next = new DateTime(getNextMedicationTime());
        String[] intakes = intake.split("-");
        if (next.getHourOfDay() == 8) {
            return Integer.parseInt(intakes[0]);
        } else if (next.getHourOfDay() == 12) {
            return Integer.parseInt(intakes[1]);
        } else if (next.getHourOfDay() == 18) {
            return Integer.parseInt(intakes[2]);
        }
        return -1;
    }
}

