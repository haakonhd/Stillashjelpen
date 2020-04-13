package no.hiof.bo20_g28.stillashjelpen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import no.hiof.bo20_g28.stillashjelpen.GlideApp;
import no.hiof.bo20_g28.stillashjelpen.R;
import no.hiof.bo20_g28.stillashjelpen.model.Wall;

public class WallRecyclerViewAdapter extends RecyclerView.Adapter<WallRecyclerViewAdapter.ViewHolder> {

    private List<Wall> mData;
    private Context context;
    private LayoutInflater mInflater;
    private WallRecyclerViewAdapter.ItemClickListener mClickListener;

    // data is passed into the constructor
    public WallRecyclerViewAdapter(Context context, List<Wall> data) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.wall_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Wall wall = mData.get(position);
        holder.wallNameTextView.setText(wall.getWallName());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");
        RequestOptions options = new RequestOptions();
        options.centerCrop();

        if (wall.getPictureId() != null) {
            GlideApp.with(context)
                    .asBitmap()
                    .load(storageReference.child(wall.getPictureId()))
                    .apply(options)
                    .into(holder.wallListImageView);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView wallNameTextView;
        ImageView wallListImageView;

        ViewHolder(View itemView) {
            super(itemView);
            wallNameTextView = itemView.findViewById(R.id.wallNameTextView);
            wallListImageView = itemView.findViewById(R.id.wallListImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onWallItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Wall getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onWallItemClick(View view, int position);
    }
}
