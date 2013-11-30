package at.fhooe.drugme.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


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
        in.readList(medications,null);
        name=in.readString();
    }
    public MedicationPlan(){
        name="";
    }
}