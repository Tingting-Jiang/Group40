package edu.northeastern.group40.A8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private ImageButton sendMsgButton;
    private EditText msgEditTxt;
    private TextView title;
    private MessageListAdapter mMsgAdapter;
    private StickerAdapter stickerAdapter;
    private DatabaseReference reference;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser;
    private final List<Message> messageList = new ArrayList<>();
    private String friendUserName;
    private final List<Sticker> stickerList = new ArrayList<>();
    private final Handler handler = new Handler();
    private volatile boolean getDataDone = false;
    private String chosenStickerId = null;
//    private String myName = "default name";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Log.i(TAG, "STICKER ID: "+ R.drawable.cat);
        Log.i(TAG, "STICKER ID: "+ R.drawable.rabbit);
        init();
        createMsgRecView();
        createStickersRecView();

//        sendMsgButton.setOnClickListener(v -> {
//            String textContent = msgEditTxt.getText().toString();
//            if (TextUtils.isEmpty(textContent)) {
//                Toast.makeText(MessageActivity.this, "You cannot send empty message", Toast.LENGTH_SHORT).show();
//            } else {
//                sendMsg(firebaseUser.getUid(), friendUserId, textContent);
//            }
//            msgEditTxt.setText("");
//
//        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(friendUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                friendUserName = user.getNickname();
                title.setText("Message to " + friendUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMsg(String sender, String receiver, String textContent) {
        // save message to database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> map = new HashMap<>();

        @SuppressLint("SimpleDateFormat")
        String timestamp = new SimpleDateFormat(TIME_FORMAT).format(new Date());

        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("message", textContent);
        map.put("date", timestamp.substring(0, 10));
        map.put("time", timestamp.substring(11));
//        map.put("senderFullName", myName);
        dbRef.child("Messages").push().setValue(map);

        // update current message list
        readMsg(firebaseUser.getUid(), friendUserId);

    }

    @SuppressLint("SetTextI18n")
    private void init() {
//        sendMsgButton = findViewById(R.id.send_button);
//        msgEditTxt = findViewById(R.id.message_edit_txt);
        title = findViewById(R.id.title);
        this.friendUserId = getIntent().getStringExtra("chosenFriend");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
    }



    private void createMsgRecView() {

        RecyclerView msgRecView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setStackFromEnd(true);
        msgRecView.setHasFixedSize(true); //TODO: READ DOC
        msgRecView.setLayoutManager(layoutManager);
        mMsgAdapter = new MessageListAdapter(MessageActivity.this, messageList);
        msgRecView.setAdapter(mMsgAdapter);

        // after set up Recycler UI, connect to database to see if there is unread msg
        readMsg(firebaseUser.getUid(), friendUserId);
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
        DatabaseReference usersDRef = mDatabase.child("Stickers");
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
                    Log.i(TAG, "GET data NUM: " + stickerList.size());
                    stickerAdapter.notifyDataSetChanged();

                    if (stickerList.size() == 0) {
                        stickerList.add(new Sticker(String.valueOf(R.drawable.cat)));
                        stickerList.add(new Sticker(String.valueOf(R.drawable.rabbit)));
                        stickerList.add(new Sticker(String.valueOf(R.drawable.cat)));
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MessageActivity.this, "Failed to load sticker list from DB", Toast.LENGTH_SHORT).show();
            }
        };
        usersDRef.addListenerForSingleValueEvent(eventListener);
    }

    private void createStickersRecView() {

        getStickerDataFromDB();
        RecyclerView stickerRecView = findViewById(R.id.sticker_rec_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        stickerRecView.setLayoutManager(layoutManager);
        stickerAdapter = new StickerAdapter(MessageActivity.this, stickerList);

        ItemCheckedListener itemCheckedListener = position -> {
            stickerList.get(position).onItemChecked(position);
            chosenStickerId = stickerList.get(position).getImageId();
            sendMsg( firebaseUser.getUid(), friendUserId, chosenStickerId);
//            stickerAdapter.notifyItemChanged(position);
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
                Log.i(TAG, "MESSAGE item --" + messageList.size());
                mMsgAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}