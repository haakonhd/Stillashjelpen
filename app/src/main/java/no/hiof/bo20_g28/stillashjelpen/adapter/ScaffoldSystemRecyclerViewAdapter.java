package no.hiof.bo20_g28.stillashjelpen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.model.ScaffoldingSystem;

public class ScaffoldSystemRecyclerViewAdapter extends RecyclerView.Adapter<ScaffoldSystemRecyclerViewAdapter.ViewHolder>{

    private List<ScaffoldingSystem> mData;
    private LayoutInflater mInflater;
    private ScaffoldSystemRecyclerViewAdapter.ItemClickListener mClickListener;

    // data is passed into the constructor
    public ScaffoldSystemRecyclerViewAdapter(Context context, List<ScaffoldingSystem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.scaffold_system_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScaffoldingSystem ss = mData.get(position);
        holder.scaffoldSystemNameListTextView.setText(ss.getScaffoldingSystemName());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView scaffoldSystemNameListTextView;

        ViewHolder(View itemView) {
            super(itemView);
            scaffoldSystemNameListTextView = itemView.findViewById(R.id.scaffoldSystemNameListTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick2(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public ScaffoldingSystem getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ScaffoldSystemRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick2(View view, int position);
    }
}