package edu.northeastern.group40.A8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group40.A8.Models.ItemCheckedListener;
import edu.northeastern.group40.A8.Models.User;
import edu.northeastern.group40.A8.RecyclerView.FriendListAdapter;
import edu.northeastern.group40.R;

public class FriendListActivity extends AppCompatActivity {

    private static final String TAG = "FriendListActivity";

    private FriendListAdapter adapter;
    private List<User> friends = new ArrayList<>();
    private String chosenFriend = null;
    private String currentUserId;
    private Button nextButton;
    private ProgressBar progressBar;
    private final Handler handler = new Handler();
    private DatabaseReference mDatabase;

    private volatile boolean getDataDone = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        init();
        createRecyclerView();
        getDataDone();

        nextButton.setOnClickListener(this::startChat);
    }

    private void init() {
        nextButton = findViewById(R.id.next_button);
        currentUserId = getIntent().getStringExtra("currentUser");
        progressBar = findViewById(R.id.progressbar);
    }

    private void getDataDone() {
        // show the visibility of progress bar to show loading
        progressBar.setVisibility(View.VISIBLE);
        FetchDataFromDB runnableThread = new FetchDataFromDB();
        new Thread(runnableThread).start();
    }

    private void createRecyclerView() {
        Log.d(TAG, "current size---: " + friends.size());
        RecyclerView friendListRecView = findViewById(R.id.user_list_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        adapter = new FriendListAdapter(friends);


        ItemCheckedListener itemCheckedListener = position -> {
            friends.get(position).onItemChecked(position);
            chosenFriend = friends.get(position).userId;
            adapter.notifyItemChanged(position);
        };
        adapter.setOnItemCheckedListener(itemCheckedListener);
        friendListRecView.setAdapter(adapter);
        friendListRecView.setLayoutManager(layoutManager);

    }

    public void startChat(View view) {
        if (chosenFriend == null) {
            Toast.makeText(FriendListActivity.this, "Please choose a friend to talk with", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(FriendListActivity.this, MessageActivity.class);
            intent.putExtra("currentUser", currentUserId);
            intent.putExtra("chosenFriend", chosenFriend);
            startActivity(intent);
        }
    }


    class FetchDataFromDB implements Runnable {
        @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
        @Override
        public void run() {
            if (!getDataDone) {
                fetchData();

            }
        }
    }

    private void fetchData() {
        DatabaseReference usersDRef = mDatabase.child("Users");
        ValueEventListener eventListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User currUser = ds.getValue(User.class);
                    // show users except current user
                    assert currUser != null;
                    if (!currUser.userId.equals(currentUserId)) {
                        friends.add(currUser);
                    }
                }

                handler.post(() -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    getDataDone = true;
                    Log.i(TAG, "GET data NUM: " + friends.size());
                    adapter.notifyDataSetChanged();
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FriendListActivity.this, "Failed to load user list from DB", Toast.LENGTH_SHORT).show();
            }
        };
        usersDRef.addListenerForSingleValueEvent(eventListener);
    }
}