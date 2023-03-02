package edu.northeastern.group40.A8.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.group40.A8.Models.ItemCheckedListener;
import edu.northeastern.group40.A8.Models.Sticker;
import edu.northeastern.group40.R;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {

    private final List<Sticker> images;
    private final Context mContext;

    private ItemCheckedListener listener;

    public StickerAdapter(Context context, List<Sticker> images) {
        mContext = context;
        this.images = images;
    }

    public void setOnItemCheckedListener(ItemCheckedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StickerAdapter.StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_card, parent, false);
        return new StickerAdapter.StickerViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerAdapter.StickerViewHolder holder, int position) {
        String singleImage = images.get(position).getImageId();
        holder.image.setImageResource(Integer.parseInt(singleImage));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class StickerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;


        public StickerViewHolder(@NonNull View itemView, ItemCheckedListener listener) {
            super(itemView);
            this.image = itemView.findViewById(R.id.sticker_id);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemChecked(position);
                    }
                }
            });
        }
    }
}

