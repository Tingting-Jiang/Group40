package edu.northeastern.group40.Project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group40.Project.Models.Order;
import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.Project.RecyclerView.MyOrdersAdapter;
import edu.northeastern.group40.R;

public class MyOrdersActivity extends AppCompatActivity implements SelectListener {
    private static final String TAG = "MyOrdersActivity";
    private final List<Order> orderList = new ArrayList<>();
    private MyOrdersAdapter myOrdersAdapter;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usersDB;
    private DatabaseReference orderDB;
    private DatabaseReference vehicleDB;
    private FirebaseUser currUser;
    private Button noOrdersBtn;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        noOrdersBtn = findViewById(R.id.no_orders_btn);
        initButton();

        createRecyclerView();
        initDatabases();
        initOrderList();
    }


    private void initButton() {
        if (orderList.size() == 0) {
            noOrdersBtn.setVisibility(View.VISIBLE);
            noOrdersBtn.setOnClickListener(v -> {
                startActivity(new Intent(MyOrdersActivity.this, SearchActivity.class));
            });
        } else {
            noOrdersBtn.setVisibility(View.GONE);
        }
    }




    private void createRecyclerView() {
        RecyclerView historyRec = findViewById(R.id.history_rec);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        historyRec.setLayoutManager(layoutManager);
        myOrdersAdapter = new MyOrdersAdapter(MyOrdersActivity.this, orderList, this) ;
        historyRec.setAdapter(myOrdersAdapter);
    }

    private void initDatabases() {
        usersDB = mDatabase.child("users");
        orderDB = mDatabase.child("orders");
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currUser != null;
    }

    private void initOrderList() {
        orderDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Order currOrder =dataSnapshot.getValue(Order.class);
                    assert currOrder != null;
                    if (currOrder.getOwnerId().equals(currUser.getUid()))
                        orderList.add(currOrder);
                }
                myOrdersAdapter.notifyDataSetChanged();
                initButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    @Override
    public void onCarSelect(Integer position) {
        Vehicle vehicle = orderList.get(position).getOrderedVehicle();
        Intent intent = new Intent(MyOrdersActivity.this, CarDetailActivity.class);
        intent.putExtra("carDetail", vehicle);
        intent.putExtra("rentLength", 0);
        startActivity(intent);
    }
}