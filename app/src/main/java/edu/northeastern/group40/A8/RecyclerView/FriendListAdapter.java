package edu.northeastern.group40.A8.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.group40.A8.Models.ItemCheckedListener;
import edu.northeastern.group40.A8.Models.User;
import edu.northeastern.group40.R;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListViewHolder>{
    private final List<User> friends;
    private ItemCheckedListener listener;

    public FriendListAdapter(List<User> friends) {
        this.friends = friends;
    }

    public void setOnItemCheckedListener(ItemCheckedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item_card, parent, false);
        return new FriendListViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListViewHolder holder, int position) {
        User currentUser = friends.get(position);
        holder.name.setText(currentUser.nickname);

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
