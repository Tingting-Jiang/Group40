package edu.northeastern.group40.A8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

    private UserAdapter userAdapter;
    private ArrayList<User> userList;
    private String chosenFriend = null;
    private User myInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        RecyclerView recyclerView = findViewById(R.id.contacts_recycler_list);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
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
                    assert currUser != null;
                    if (!currUser.getUserId().equals(currUid))
                        userList.add(currUser);
                    else {
                        myInfo = currUser;
                    }
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

    public void checkMore(View view) {
        View dialogView = LayoutInflater.from(ContactsActivity.this)
                .inflate(R.layout.dialog_more, null);

        TextView details = dialogView.findViewById(R.id.details_txt);
        details.setText(myInfo.displayStickerSend());
        AlertDialog detailsDialog = new MaterialAlertDialogBuilder(ContactsActivity.this)
                .setTitle("Details")
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) ->
                        dialog.dismiss()
                )
                .create();
        detailsDialog.show();
        detailsDialog.setCancelable(true);

    }

}
