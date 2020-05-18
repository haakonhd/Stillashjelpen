package no.hiof.bo20_g28.stillashjelpen.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.fragment.FourthControlSchemeFragment;
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefectFixed;

public class DefectFixedRecyclerViewAdapter  extends RecyclerView.Adapter<DefectFixedRecyclerViewAdapter.ViewHolder> {

    private List<ControlSchemeDefectFixed> defectFixedDataList;
    private LayoutInflater layoutInflater;
    private DefectFixedRecyclerViewAdapter.ItemClickListener clickListener;

    public DefectFixedRecyclerViewAdapter(Context context, List<ControlSchemeDefectFixed> defectFixedDataList) {
        this.defectFixedDataList = defectFixedDataList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.defectfixed_item, parent, false);
        Log.d("defectFixed", "DefectFixed inflated.");
        return new DefectFixedRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ControlSchemeDefectFixed controlSchemeDefectFixed = defectFixedDataList.get(position);

        holder.controlDate.setText(controlSchemeDefectFixed.getControlDate().toString());
        holder.fixedDate.setText(controlSchemeDefectFixed.getDefectFixedDate().toString());
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
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return defectFixedDataList.size();
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
