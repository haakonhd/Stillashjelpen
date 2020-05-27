package no.hiof.bo20_g28.stillashjelpen.adapter;

import android.content.Context;
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
import no.hiof.bo20_g28.stillashjelpen.model.ControlSchemeDefect;

public class DefectRecyclerViewAdapter extends RecyclerView.Adapter<DefectRecyclerViewAdapter.ViewHolder> {

    private List<ControlSchemeDefect> mData;
    private LayoutInflater mInflater;
    private DefectRecyclerViewAdapter.ItemClickListener mClickListener;


    public DefectRecyclerViewAdapter(Context context, List<ControlSchemeDefect> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.defect_item, parent, false);
        return new ViewHolder(view);
    }

    private static String getSimpleDateFormat(Date date){
        Format formatter;
        formatter = new SimpleDateFormat("dd/mm/yyyy");
        return formatter.format(date);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ControlSchemeDefect schemeDefect = mData.get(position);
        holder.defectMessageTextView.setText(schemeDefect.getDefectDescription());
        holder.defectDateFound.setText(getSimpleDateFormat(schemeDefect.getfoundDate()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView defectMessageTextView;
        TextView defectDateFound;
        ViewHolder(View itemView) {
            super(itemView);
            defectMessageTextView = itemView.findViewById(R.id.defectMessageTextView);
            defectDateFound = itemView.findViewById(R.id.defectDateFound);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onDefectItemClick(view, getAdapterPosition());
        }
    }
    public ControlSchemeDefect getItem(int id) {
        return mData.get(id);
    }
    public interface ItemClickListener {
        void onDefectItemClick(View view, int Position);
    }

}
