package at.fhooe.drugme.model;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import at.fhooe.drugme.R;


public class MedicationPlan implements Parcelable {

    @Expose
    private List<Medication> medications = new ArrayList<Medication>();
    @Expose
    private String name;

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }

    public MedicationPlan withMedications(List<Medication> medications) {
        this.medications = medications;
        return this;
    }
    public List<Medication> getCurrentMedications(){
        ArrayList<Medication> current= new ArrayList<Medication>();
        DateTime end=null;
        DateTime start=null;
        for(Medication med:medications){
            end=new DateTime(med.getEndDate());
            start=new DateTime(med.getStartDate());
            if(end.isAfterNow()&&start.isBeforeNow()){
                current.add(med);
            }
        }
        return current;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MedicationPlan withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(medications);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<MedicationPlan> CREATOR
            = new Parcelable.Creator<MedicationPlan>() {
        public MedicationPlan createFromParcel(Parcel in) {
            return new MedicationPlan(in);
        }

        public MedicationPlan[] newArray(int size) {
            return new MedicationPlan[size];
        }
    };

    private MedicationPlan(Parcel in) {
        medications = new ArrayList<Medication>();
        in.readList(medications,getClass().getClassLoader());
        name=in.readString();
    }
    public MedicationPlan(){
        name="";
    }
    static public MedicationPlan loadPlan(Context activity){
        SharedPreferences prefs = activity.getSharedPreferences(activity.getString(R.string.shared_pref_name), 0);
        String json = prefs.getString(activity.getString(R.string.pref_medication_plan), "");
        if (json.isEmpty()) {
            return new MedicationPlan();
     } else {
            return new Gson().fromJson(json, MedicationPlan.class);
        }
    }
}