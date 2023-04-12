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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group40.Project.Models.AvailableDate;
import edu.northeastern.group40.Project.Models.Brand;
import edu.northeastern.group40.Project.Models.Color;
import edu.northeastern.group40.Project.Models.Fuel;
import edu.northeastern.group40.Project.Models.Mileage;
import edu.northeastern.group40.Project.Models.MyLocation;
import edu.northeastern.group40.Project.Models.Order;
import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.Project.Models.VehicleBodyStyle;
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
//        addFakeOrder();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    private void addFakeOrder() {
        String dbString = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000006-Tue%20Mar%2028%2019%3A37%3A13%20PDT%202023?alt=media&token=dcf3b137-9a01-4b32-acba-0079849b57a4";
        MyLocation testLocation = null;
        try {
            testLocation = new MyLocation(35.40273, -120.95154, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Vehicle vehicle1 = new Vehicle(Brand.HONDA, Brand.Model.ACCORD, Color.WHITE, VehicleBodyStyle.CROSSOVER,
                Fuel.GASOLINE, Mileage.BETWEEN_5K_AND_10K, 4, testLocation, 1,
                "2023 Brand New Accord", dbString,"04/14/2023", "04/21/2023", "123", "3455");
        vehicle1.setReviewResult("4.2");
        vehicle1.setReviewTotalNumber(100);

        // NO-2
        Vehicle vehicle2 = new Vehicle(Brand.HONDA, Brand.Model.ACCORD, Color.WHITE, VehicleBodyStyle.SUV,
                Fuel.GASOLINE, Mileage.LESS_THAN_10K, 5, testLocation, 2,
                "2023 Brand New CAMRY with Super big screen and super comfortable seats", dbString, "04/14/2023", "04/21/2023", "123", "3455");
        vehicle2.setReviewResult("4.2");
        vehicle2.setReviewTotalNumber(56);
        // (String orderId, Vehicle orderedVehicle, AvailableDate orderDate, int orderPriceTotal, String ownerId)

        Order order1 = new Order("123", vehicle1, new AvailableDate("04/14/2023", "04/21/2023"), 150, "12345");
        Order order2 = new Order("123", vehicle2, new AvailableDate("04/14/2023", "04/21/2023"), 250, "12345");
        orderList.add(order1);
        orderList.add(order2);
//        orderList.add(order1);
//        orderList.add(order2);
        myOrdersAdapter.notifyDataSetChanged();
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