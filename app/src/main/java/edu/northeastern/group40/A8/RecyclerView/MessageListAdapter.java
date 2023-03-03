package edu.northeastern.group40.A8.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import edu.northeastern.group40.A8.Models.Message;
import edu.northeastern.group40.R;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final String TAG = "MessageListAdapter";

    private final Context mContext;
    private final List<Message> mMessages;

    FirebaseUser firebaseUser;

    public MessageListAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessages = messageList;
    }


    @NonNull
    @Override
    public MessageListAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_chat_me, parent, false);
        } else {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_chat_other, parent, false);
        }
        return new MessageViewHolder(view);
    }


    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Message message = mMessages.get(position);

        if (message.getSender().equals(firebaseUser.getUid())) {
            // If I am the current user who send the msg
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If my friend sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.MessageViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.sticker.setImageResource(Integer.parseInt(message.getMessage()));
        holder.timeText.setText(message.getTime());
        holder.nameText.setText(message.getSender());
        holder.dateText.setText(message.getDate());

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView timeText, nameText, dateText;
        public ImageView sticker;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.sticker = itemView.findViewById(R.id.sticker_icon);
            this.timeText = itemView.findViewById(R.id.timestamp_txt);
            this.nameText = itemView.findViewById(R.id.user_name_txt);
            this.dateText = itemView.findViewById(R.id.date_txt);
        }
    }


}
