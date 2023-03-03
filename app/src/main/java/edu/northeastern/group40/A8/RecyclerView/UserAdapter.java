package edu.northeastern.group40.A8.RecyclerView;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.group40.A8.Models.ItemCheckedListener;
import edu.northeastern.group40.A8.Models.User;
import edu.northeastern.group40.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    Context context;
    ArrayList<User> list;
    private ItemCheckedListener listener;

    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.contacts_view_card, parent, false);
        return new MyViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = list.get(position);
        holder.userName.setText(user.getNickname());
        holder.userEmail.setText(user.getEmail());
        holder.checkBox.setChecked(!user.getStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemCheckedListener(ItemCheckedListener listener) {
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userEmail;
        CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView, ItemCheckedListener listener) {
            super(itemView);
            userName = itemView.findViewById(R.id.tvName);
            userEmail = itemView.findViewById(R.id.tvEmail);
            checkBox = itemView.findViewById(R.id.checkBox);

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