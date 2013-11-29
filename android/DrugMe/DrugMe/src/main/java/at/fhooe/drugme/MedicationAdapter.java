package at.fhooe.drugme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Date;
import java.util.List;

import at.fhooe.drugme.model.Medication;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MedicationAdapter extends ArrayAdapter<Medication> {

 List<Medication> medications;
Context context;
    public MedicationAdapter(Context context, int textViewResourceId,
                              List<Medication> medications) {
        super(context, textViewResourceId, medications);
        this.context=context;
        this.medications=medications;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.row_medication_list, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.name.setText(medications.get(position).getName());
        DateTime next = new DateTime(medications.get(position).getNextMedicationTime());
        DateTime now = new DateTime(new Date());
        Period diff = new Period(now, next);
        if (diff.getDays() > 0)
            holder.time.setText(String.format("%d days %d hours %d minutes", diff.getDays(), diff.getHours(), diff.getMinutes()));
        else
            holder.time.setText(String.format("%d hours %d minutes", diff.getHours(), diff.getMinutes()));

        holder.pill.setText(String.format("%d pills",medications.get(position).getNextMedicationValue()));

        if(position%2==0){
            view.setBackgroundColor(context.getResources().getColor(R.color.bg_blue));
        }
        else{
            view.setBackgroundColor(0);
        }
        return view;



    }

    static class ViewHolder {
        @InjectView(R.id.medication_row_name)
        TextView name;
        @InjectView(R.id.medication_row_time)
        TextView time;
        @InjectView(R.id.medication_row_pill)
        TextView pill;
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
