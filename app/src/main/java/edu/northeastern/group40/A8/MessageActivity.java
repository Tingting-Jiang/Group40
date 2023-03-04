package edu.northeastern.group40.A8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.northeastern.group40.A8.Models.ItemCheckedListener;
import edu.northeastern.group40.A8.Models.Message;
import edu.northeastern.group40.A8.Models.Sticker;
import edu.northeastern.group40.A8.Models.User;
import edu.northeastern.group40.A8.RecyclerView.MessageListAdapter;
import edu.northeastern.group40.A8.RecyclerView.StickerAdapter;
import edu.northeastern.group40.R;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MessageActivity";
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String EMAIL_SUFFIX = "@123.com";
    private String friendUserId;
    private TextView title;
    private MessageListAdapter mMsgAdapter;
    private StickerAdapter stickerAdapter;
    private DatabaseReference reference;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference messageDB;
    private DatabaseReference usersDB;
    private String myId;
    private User myInfo;
    private final List<Message> messageList = new ArrayList<>();
    private String friendUserName;
    private String stickerTotalInfo = "Nothing";

    private static final int NOTIFICATION_ID = 1;


    private static final List<Sticker> stickerList = new ArrayList<Sticker>(){
        {
            add(new Sticker(String.valueOf(R.drawable.cat), "cat"));
            add(new Sticker(String.valueOf(R.drawable.rabbit), "rabbit"));
            add(new Sticker(String.valueOf(R.drawable.dog), "dog"));
            add(new Sticker(String.valueOf(R.drawable.horse), "horse"));
            add(new Sticker(String.valueOf(R.drawable.cow), "cow"));
            add(new Sticker(String.valueOf(R.drawable.sheep), "sheep"));
            add(new Sticker(String.valueOf(R.drawable.chick), "chick"));
            add(new Sticker(String.valueOf(R.drawable.duck), "duck"));
            add(new Sticker(String.valueOf(R.drawable.giraffe), "giraffe"));
            add(new Sticker(String.valueOf(R.drawable.pig), "pig"));
            add(new Sticker(String.valueOf(R.drawable.panda), "panda"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        init();
        createMsgRecView();
        createStickersRecView();
        createNotificationChannel();

        usersDB.addChildEventListener(new ChildEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getUserId().equals(friendUserId)) {
                    title.setText("Message to " + user.getNickname());
                } else if (user.getUserId().equals(myId)) {
                    updateUserInfo(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateUserInfo(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Channel name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    private void updateUserInfo(DataSnapshot snapshot) {
        User currUser = snapshot.getValue(User.class);
        if (currUser.getUserId().equals(myId)) {
            myInfo = currUser;
            stickerTotalInfo = myInfo.displayStickerSend();
            Log.i(TAG, myInfo.displayStickerSend());
        }
    }

    private void sendMsg(String sender, String receiver, Sticker chosenSticker) {
        // save message to database
        @SuppressLint("SimpleDateFormat")
        String timestamp = new SimpleDateFormat(TIME_FORMAT).format(new Date());
//        Log.i(TAG, "sender: " + (sender == null));
//        Log.i(TAG, "receiver: " + (receiver == null));
//        Log.i(TAG, "chosenSticker: " + (chosenSticker == null));
        Message newMsg = new Message(chosenSticker.getStickerId(), sender, receiver, timestamp.substring(0, 10),timestamp.substring(11), myInfo.getNickname());
        messageDB.push().setValue(newMsg);

        Toast.makeText(MessageActivity.this, chosenSticker.getStickerName() + " sent :)", Toast.LENGTH_SHORT).show();

        updateUserStickerData(chosenSticker);

        // update current message list
        readMsg(myId, friendUserId);
    }

    private void updateUserStickerData(Sticker chosenSticker) {
        usersDB.child(myId).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                User user = currentData.getValue(User.class);

                if (user == null)
                    return Transaction.success(currentData);

                HashMap<String, Integer> originData = user.getStickerSend();
                if (originData == null) {
                    originData = new HashMap<>();
                    Log.i(TAG, "create empty origin data");
                }
                Integer originNum = originData.getOrDefault(chosenSticker.getStickerName(), 0);
                assert originNum != null;
                originData.put(chosenSticker.getStickerName(), originNum + 1);
                user.setStickerSend(originData);
                currentData.setValue(user);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                   @Nullable DataSnapshot currentData) {
//                Log.d(TAG, "postTransaction: onComplete: " + error);
//                Toast.makeText(MessageActivity.this, "Get DB error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init() {
        messageDB = mDatabase.child("Messages");
        usersDB = mDatabase.child("Users");
        title = findViewById(R.id.title);
        this.friendUserId = getIntent().getStringExtra("chosenFriend");
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currUser != null;
        myId = currUser.getUid();
    }



    private void createMsgRecView() {
        RecyclerView msgRecView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecView.setLayoutManager(layoutManager);
        mMsgAdapter = new MessageListAdapter(MessageActivity.this, messageList);
        msgRecView.setAdapter(mMsgAdapter);

        // after set up Recycler UI, connect to database to see if there is unread msg
        readMsg(myId, friendUserId);
    }

    private void createStickersRecView() {
        RecyclerView stickerRecView = findViewById(R.id.sticker_rec_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        stickerRecView.setLayoutManager(layoutManager);
        stickerAdapter = new StickerAdapter(MessageActivity.this, stickerList);

        ItemCheckedListener itemCheckedListener = position -> {
            Sticker chosenSticker = stickerList.get(position);
            chosenSticker.onItemChecked(position);
            sendMsg(myId, friendUserId, chosenSticker);
        };
        stickerAdapter.setOnItemCheckedListener(itemCheckedListener);

        stickerRecView.setAdapter(stickerAdapter);
    }

    private void readMsg(String myId, String friendUserId) {
        DatabaseReference msgDRef = FirebaseDatabase.getInstance().getReference("Messages");
        msgDRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                int idx = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Message msg = dataSnapshot.getValue(Message.class);
                    if (msg.getReceiver().equals(myId) && msg.getSender().equals(friendUserId) ||
                            msg.getReceiver().equals(friendUserId) && msg.getSender().equals(myId)) {
                        if (idx > 0 && !TextUtils.isEmpty(messageList.get(idx-1).getDate())) {
                            msg.setDate();
                        }
                        messageList.add(msg);
                        idx++;
                    }
                }
                mMsgAdapter.notifyDataSetChanged();
                if(!messageList.isEmpty()) {
                    Message lastMsg = messageList.get(messageList.size() - 1);
                    if (lastMsg.getSender().equals(friendUserId)) {

//                        Intent intent = new Intent(MessageActivity.this, MessageActivity.class);
//                        intent.putExtra("friendUserId", friendUserId);
//                        PendingIntent pendingIntent = PendingIntent.getActivity(MessageActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                        String stickerID = lastMsg.getMessage();

                        int id = getResources().getIdentifier(stickerID, "drawable", getPackageName());

                        Bitmap graph = BitmapFactory.decodeResource(getResources(), id);

                        NotificationCompat.Builder builder = new NotificationCompat
                                .Builder(MessageActivity.this, "default")
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle("New message from " + lastMsg.getSenderFullName())
                                .setLargeIcon(graph)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//                                .setContentIntent(pendingIntent)
//                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager =
                                NotificationManagerCompat
                                        .from(MessageActivity.this);
                        try {
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                        } catch (SecurityException e) {
                            Toast.makeText(MessageActivity.this, "Get DB error: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void backToContactsList(View view) {
        onBackPressed();
    }

    public void checkMore(View view) {
        View dialogView = LayoutInflater.from(MessageActivity.this)
                .inflate(R.layout.dialog_more, null);

        TextView details = dialogView.findViewById(R.id.details_txt);
        details.setText(stickerTotalInfo);
        AlertDialog detailsDialog = new MaterialAlertDialogBuilder(MessageActivity.this)
                .setTitle("Details")
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                        dialog.dismiss();
                })
                .create();
        detailsDialog.show();
        detailsDialog.setCancelable(true);

    }


}