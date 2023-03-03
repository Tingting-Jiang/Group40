package edu.northeastern.group40.A8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
    private DatabaseReference stickerDB;
    private String myId;
    private User myInfo;
    private final List<Message> messageList = new ArrayList<>();
    private String friendUserName;
    private final List<Sticker> stickerList = new ArrayList<>();
    private final Handler handler = new Handler();
    private volatile boolean getDataDone = false;
    private String stickerTotalInfo = "";
//    private String myName = "default name";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        init();
        createMsgRecView();
        createStickersRecView();
//        usersDB.child(friendUserId).addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//                assert user != null;
//                friendUserName = user.getNickname();
//                title.setText("Message to " + friendUserName);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        usersDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    @SuppressLint("SetTextI18n")
    private void updateUserInfo(DataSnapshot snapshot) {
        User currUser = snapshot.getValue(User.class);
        if (currUser.getUserId().equals(myId)) {
            myInfo = currUser;
            Log.i(TAG, myInfo.displayStickerSend());
        } else {
            friendUserName = currUser.getNickname();
            title.setText("Message to " + friendUserName);

        }
    }

    private void sendMsg(String sender, String receiver, Sticker chosenSticker) {
        // save message to database

        @SuppressLint("SimpleDateFormat")
        String timestamp = new SimpleDateFormat(TIME_FORMAT).format(new Date());
        Message newMsg = new Message(chosenSticker.getStickerId(), sender, receiver, timestamp.substring(0, 10),timestamp.substring(11));
        messageDB.push().setValue(newMsg);

        Toast.makeText(MessageActivity.this, chosenSticker.getStickerName() + " sent :)", Toast.LENGTH_SHORT).show();

        // TODO: SAVE current sticker to my profile

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
                originData.put(chosenSticker.getStickerName(), originNum + 1);
                user.setStickerSend(originData);
                currentData.setValue(user);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                   @Nullable DataSnapshot currentData) {
                Log.d(TAG, "postTransaction: onComplete: " + error);
                Toast.makeText(MessageActivity.this, "Get DB error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init() {
        messageDB = mDatabase.child("Messages");
        stickerDB = mDatabase.child("Stickers");
        usersDB = mDatabase.child("Users");
        title = findViewById(R.id.title);
        this.friendUserId = getIntent().getStringExtra("chosenFriend");

        myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }



    private void createMsgRecView() {

        RecyclerView msgRecView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        msgRecView.setHasFixedSize(true); //TODO: READ DOC
        msgRecView.setLayoutManager(layoutManager);
        mMsgAdapter = new MessageListAdapter(MessageActivity.this, messageList);
        msgRecView.setAdapter(mMsgAdapter);

        // after set up Recycler UI, connect to database to see if there is unread msg
        readMsg(myId, friendUserId);
    }

    private void getStickerDataFromDB() {
        FetchStickerDataFromDB runnableThread = new FetchStickerDataFromDB();
        new Thread(runnableThread).start();
    }

    class FetchStickerDataFromDB implements Runnable {
        @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
        @Override
        public void run() {
            if (!getDataDone) {
                fetchData();

            }
        }
    }

    private void fetchData() {
//        HashMap<String, Object> map1 = new HashMap<>();
//
//        map1.put("stickerName", "cat");
//        map1.put("stickerId", "2131165430");
//
//        stickersDRef.push().setValue(map1);
//
//        HashMap<String, Object> map2 = new HashMap<>();
//
//        map2.put("stickerName", "rabbit");
//        map2.put("stickerId", "2131165431");
//
//        stickersDRef.push().setValue(map2);
        ValueEventListener eventListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Sticker currSticker = ds.getValue(Sticker.class);
                    // show users except current user
                    assert currSticker != null;
                    stickerList.add(currSticker);
                }

                handler.post(() -> {
                    getDataDone = true;
                    stickerAdapter.notifyDataSetChanged();
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MessageActivity.this, "Failed to load sticker list from DB", Toast.LENGTH_SHORT).show();
            }
        };
        stickerDB.addListenerForSingleValueEvent(eventListener);
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

//            stickerAdapter.notifyItemChanged(position);
        };
        stickerAdapter.setOnItemCheckedListener(itemCheckedListener);

        stickerRecView.setAdapter(stickerAdapter);

        getStickerDataFromDB();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}