package at.fhooe.drugme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Date;
import java.util.List;

import at.fhooe.drugme.model.Medication;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class MedicationAdapter extends ArrayAdapter<Medication> {

 List<Medication> medications;
Context context;
    private Filter filter;
int textViewResourceId;
    public MedicationAdapter(Context context, int textViewResourceId,
                              List<Medication> medications) {
        super(context, textViewResourceId, medications);
        this.context=context;
        this.medications=medications;
        this.textViewResourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(textViewResourceId, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.name.setText(medications.get(position).getMedication());
        DateTime next = new DateTime(medications.get(position).getNextMedicationTime());
        DateTime now = new DateTime(new Date());
        Period diff = new Period(now, next);
        if(holder.time!=null){
            if (diff.getDays() > 0)
                holder.time.setText(String.format("%d days %d hours %d minutes", diff.getDays(), diff.getHours(), diff.getMinutes()));
            else
                holder.time.setText(String.format("%d hours %d minutes", diff.getHours(), diff.getMinutes()));
        }


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
        @Optional
        @InjectView(R.id.medication_row_time)
        TextView time;
        @InjectView(R.id.medication_row_pill)
        TextView pill;
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public Filter getFilter() {
        if(filter == null)
            filter = new MedicationDateFilter();
        return filter;

    }

    private class MedicationDateFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results=new FilterResults();

            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    }
}
