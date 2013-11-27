package at.fhooe.drugme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
        holder.information.setText(medications.get(position).getIntake());
        // etc...

        return view;



    }

    static class ViewHolder {
        @InjectView(R.id.medication_name)
        TextView name;
        @InjectView(R.id.medication_information)
        TextView information;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
