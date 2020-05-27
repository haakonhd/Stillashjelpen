package no.hiof.bo20_g28.stillashjelpen.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.fragment.FourthControlSchemeFragment;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefectFixed;

public class DefectFixedRecyclerViewAdapter  extends RecyclerView.Adapter<DefectFixedRecyclerViewAdapter.ViewHolder> {

    private List<ControlSchemeDefectFixed> mData;
    private LayoutInflater layoutInflater;
    private DefectFixedRecyclerViewAdapter.ItemClickListener clickListener;

    public DefectFixedRecyclerViewAdapter(Context context, List<ControlSchemeDefectFixed> defectFixedDataList) {
        this.mData = defectFixedDataList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.defectfixed_item, parent, false);
        Log.d("defectFixed", "DefectFixed inflated.");
        return new DefectFixedRecyclerViewAdapter.ViewHolder(view);
    }

    private static String getSimpleDateFormat(Date date){
        Format formatter;
        formatter = new SimpleDateFormat("dd/mm/yyyy");
        return formatter.format(date);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ControlSchemeDefectFixed controlSchemeDefectFixed = mData.get(position);

//        holder.controlDate.setText(controlSchemeDefectFixed.getControlDate().toString());
        holder.controlDate.setText(getSimpleDateFormat(controlSchemeDefectFixed.getControlDate()));
//        holder.fixedDate.setText(controlSchemeDefectFixed.getDefectFixedDate().toString());
        holder.fixedDate.setText(getSimpleDateFormat(controlSchemeDefectFixed.getDefectFixedDate()));
        holder.signature.setText(controlSchemeDefectFixed.getSignature());

        Log.d("DefectFixed", (String) holder.controlDate.getText());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView controlDate;
        TextView fixedDate;
        TextView signature;

        ViewHolder(View itemView) {
            super(itemView);
            controlDate = itemView.findViewById(R.id.defectFixedControlDateTextView);
            fixedDate = itemView.findViewById(R.id.defectFixedDateFixedTextView);
            signature = itemView.findViewById(R.id.defectFixedSignatureTextView);;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                try {
                    clickListener.onDefectFixedItemClick(view, getAdapterPosition());
                }
                catch (ClassCastException e) {
                    throw new ClassCastException(e.getMessage());
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ControlSchemeDefectFixed getItem(int id) {
        return mData.get(id);
    }

    public interface ItemClickListener {
        void onDefectFixedItemClick(View view, int Position);
    }
}
