package edu.northeastern.group40.A8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.northeastern.group40.A8.Models.User;
import edu.northeastern.group40.A8.RecyclerView.UserAdapter;
import edu.northeastern.group40.R;

public class ContactsActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private DatabaseReference databaseRef;
    private UserAdapter UserAdapter;
    private ArrayList<User> Userlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        recyclerView = findViewById(R.id.contacts_recycler_list);
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Userlist = new ArrayList<>();
        UserAdapter = new UserAdapter(this,Userlist);
        recyclerView.setAdapter(UserAdapter);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Map<String,String> td=(HashMap<String, String>)dataSnapshot.getValue();
                    User user = new User(td.get("userEmail"), td.get("userId"));
                    Userlist.add(user);
                }
                UserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
