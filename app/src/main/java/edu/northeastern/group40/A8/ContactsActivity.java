package edu.northeastern.group40.A8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


import edu.northeastern.group40.A8.Models.ItemCheckedListener;
import edu.northeastern.group40.A8.Models.User;
import edu.northeastern.group40.A8.RecyclerView.UserAdapter;
import edu.northeastern.group40.R;

public class ContactsActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private DatabaseReference databaseRef;
    private UserAdapter userAdapter;
    private ArrayList<User> userList;
    private String chosenFriend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        recyclerView = findViewById(R.id.contacts_recycler_list);
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);

        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currUser != null;
        String myId = currUser.getUid();
        FirebaseDatabase.getInstance().getReference("Messages").addChildEventListener(new NotificationListener(myId,this));

        ItemCheckedListener itemCheckedListener = position -> {
            userList.get(position).onItemChecked(position);
            chosenFriend = userList.get(position).getUserId();
            startChat();
        };
        userAdapter.setOnItemCheckedListener(itemCheckedListener);

        recyclerView.setAdapter(userAdapter);
        String currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User currUser =dataSnapshot.getValue(User.class);
                    if (!currUser.getUserId().equals(currUid))
                        userList.add(currUser);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void startChat() {
        if (chosenFriend == null) {
            Toast.makeText(ContactsActivity.this, "Please choose a friend to talk with", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(ContactsActivity.this, MessageActivity.class);
            intent.putExtra("chosenFriend", chosenFriend);
            startActivity(intent);
        }
    }

}
