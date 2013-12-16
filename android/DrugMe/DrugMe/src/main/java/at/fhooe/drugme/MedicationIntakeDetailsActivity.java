package at.fhooe.drugme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import at.fhooe.drugme.model.Medication;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ch on 28.11.13.
 */
public class MedicationIntakeDetailsActivity extends Activity {


    @InjectView(R.id.medication_detail_name)
    TextView nameView;


    @InjectView(R.id.medication_detail_pill)
    TextView pillView;

    @InjectView(R.id.medication_detail_info)
    TextView infoView;

    @InjectView(R.id.medication_detail_icon)
    ImageView medicationImage;
    Medication medication = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intake_details);
        ButterKnife.inject(this);
        medication = getIntent().getParcelableExtra("medication");

        nameView.setText(medication.getMedication());

        pillView.setText(String.format("%d pills", medication.getNextMedicationValue()));




        Picasso.with(this).load(String.format("http://%s/medications/%s/img",getString(R.string.api_base_url), medication.getMedication())).into(medicationImage);


        infoView.setText(medication.getInfo());

    }
}
