package at.fhooe.drugme.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Medication implements Parcelable{

    @Expose
    private String info;
    @Expose
    private String startdate;
    @Expose
    private String intake;
    @Expose
    private String enddate;
    @Expose
    private String name;
    @Expose
    private String dose;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Medication withInfo(String info) {
        this.info = info;
        return this;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public Medication withStartdate(String startdate) {
        this.startdate = startdate;
        return this;
    }

    public String getIntake() {
        return intake;
    }

    public void setIntake(String intake) {
        this.intake = intake;
    }

    public Medication withIntake(String intake) {
        this.intake = intake;
        return this;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public Medication withEnddate(String enddate) {
        this.enddate = enddate;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Medication withName(String name) {
        this.name = name;
        return this;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public Medication withDose(String dose) {
        this.dose = dose;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(info);
        dest.writeString(startdate);
        dest.writeString(intake);
        dest.writeString(enddate);
        dest.writeString(name);
        dest.writeString(dose);
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
        info=in.readString();
        startdate=in.readString();
        intake=in.readString();
        enddate=in.readString();
        name=in.readString();
        dose=in.readString();


    }
    public Date getNextMedicationTime(){
      String[] intakes=intake.split("-");
        Date now=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        ArrayList<Date> times = new ArrayList<Date>();

        if(!intakes[0].equals("0")){
            calendar.setTime(now);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            if(hours>=8){
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            } 
            calendar.set(Calendar.HOUR_OF_DAY,8);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            times.add(calendar.getTime());

        }
        if(!intakes[1].equals("0")){
            calendar.setTime(now);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            if(hours>=12){
                calendar.add(Calendar.DAY_OF_MONTH,1);
            }
            calendar.set(Calendar.HOUR_OF_DAY,12);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            times.add(calendar.getTime());
        }
        if(!intakes[2].equals("0")){
            calendar.setTime(now);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            if(hours>=18){
                calendar.add(Calendar.DAY_OF_MONTH,1);
            }
            calendar.set(Calendar.HOUR_OF_DAY,18);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            times.add(calendar.getTime());
        }

      Collections.sort(times);
      return times.get(0);
    }
    public int getNextMedicationValue(){

        DateTime next=new DateTime(getNextMedicationTime());
        String[] intakes=intake.split("-");
        if(next.getHourOfDay()==8){
            return Integer.parseInt(intakes[0]);
        }
        else if(next.getHourOfDay()==12){
            return Integer.parseInt(intakes[1]);
        }
        else if(next.getHourOfDay()==18){
            return Integer.parseInt(intakes[2]);
        }
        return -1;
    }
}

