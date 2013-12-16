package at.fhooe.drugme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Comparator;

import at.fhooe.drugme.model.Medication;
import at.fhooe.drugme.model.MedicationPlan;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MedicationIntake extends Activity {

    MedicationPlan plan;
    @InjectView(R.id.medication_intake_list)
    ListView intakeView;
    private ArrayList<Medication> mMedications;
    private MedicationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);
        ButterKnife.inject(this);
        if (savedInstanceState != null) {

        }

        plan = (MedicationPlan) getIntent().getParcelableExtra("plan");
        mMedications = new ArrayList<Medication>();
        mMedications.clear();
        mMedications.addAll(plan.getCurrentMedications());
        //mMedicationList= new ArrayList<MedicationIntake>();
        mAdapter = new MedicationAdapter(this, R.layout.row_intake_list, mMedications);
      /*  mAdapter.sort(new Comparator<Medication>() {
            @Override
            public int compare(Medication lhs, Medication rhs) {

                return new DateTime(lhs.getNextMedicationTime()).compareTo(new DateTime(rhs));

            }
        });*/
        intakeView.setAdapter(mAdapter);
        intakeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MedicationIntake.this, MedicationIntakeDetailsActivity.class);
                i.putExtra("medication", mMedications.get(position));
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onNavigateUp() {
        Intent i = new Intent(this, DrugMeActivity.class);
        i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.medication, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
