package edu.northeastern.group40.A8.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.group40.A8.Models.ItemCheckedListener;
import edu.northeastern.group40.R;

public class FriendListViewHolder extends RecyclerView.ViewHolder {

    public ImageView icon;
    public TextView name;


    public FriendListViewHolder(@NonNull View itemView, ItemCheckedListener listener) {
        super(itemView);
        this.icon = itemView.findViewById(R.id.friend_icon);
        this.name = itemView.findViewById(R.id.friend_name_tv_id);

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