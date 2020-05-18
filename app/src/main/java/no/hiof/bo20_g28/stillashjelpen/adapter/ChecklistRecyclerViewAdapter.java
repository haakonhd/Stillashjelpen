package no.hiof.bo20_g28.stillashjelpen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import no.hiof.bo20_g28.stillashjelpen.ControlSchemeActivity;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.model.ChecklistItem;

public class ChecklistRecyclerViewAdapter  extends RecyclerView.Adapter<ChecklistRecyclerViewAdapter.ViewHolder> {

    private List<ChecklistItem> itemData;
    private Context context;
    private LayoutInflater layoutInflater;
    private ChecklistRecyclerViewAdapter.ItemClickListener itemClickListener;


    public ChecklistRecyclerViewAdapter(Context context, List<ChecklistItem> data){
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.itemData = data;
        try{
            itemClickListener = ((ItemClickListener) context);
        }catch(ClassCastException e){
//            throw new ClassCastException("Activity must implement ItemClickListener.");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.checklist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChecklistRecyclerViewAdapter.ViewHolder holder, int position) {
        ChecklistItem item = itemData.get(position);
        holder.cl_text.setText(item.getText());
        holder.cl_text.setClickable(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cl_checkbox.performClick();
            }
        });
        holder.cl_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cl_checkbox.performClick();
            }
        });

        holder.cl_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.cl_checkbox.isChecked())
                    itemData.get(position).setChecked(true);
                else
                    itemData.get(position).setChecked(false);
                try{
                    itemClickListener.onChecklistItemClicked(holder.cl_checkbox, position);
                }catch(ClassCastException e){
                    throw new ClassCastException(e.getMessage());
                }
            }
        });

        if(!item.isParent()) {
            //setting margin for child-elements
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.cl_checkbox.getLayoutParams();
            p.setMargins(60, 0, 0, 0);
            holder.cl_checkbox.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return itemData.size();
    }

    public ChecklistItem getItem(int id){ return itemData.get(id); }

    public void setClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onChecklistItemClicked(View view, int position);
        void onMethodCallback();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox cl_checkbox;
        TextView cl_text;
        Button cl_button;

        ViewHolder(View itemView) {
            super(itemView);
            cl_checkbox = itemView.findViewById(R.id.cl_checkbox);
            cl_text = itemView.findViewById(R.id.cl_text);
            cl_button = itemView.findViewById(R.id.cl_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onChecklistItemClicked(view, getAdapterPosition());
        }
    }
}
