package edu.northeastern.group40.Project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.Project.RecyclerView.MyCarAdapter;
import edu.northeastern.group40.R;

public class MyCarsActivity extends AppCompatActivity implements SelectListener {

    private final List<Vehicle> carList = new ArrayList<>();

    private final HashSet<String> carIdList = new HashSet<>();
    private MyCarAdapter myCarsAdapter;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference vehiclesDB = mDatabase.child("vehicles");
    private DatabaseReference myCarDB;
    private FirebaseUser currUser;
    private RecyclerView carRecView;
    private String uid;

    private Boolean hasCar = false;

    private Button noCarsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars);
        initRecyclerView();
        setupUI();
    }

    private void initRecyclerView() {
        carRecView = findViewById(R.id.car_list_of_user_recView);
        carRecView.setLayoutManager(new LinearLayoutManager(this));
        myCarsAdapter = new MyCarAdapter(carList, this);
        carRecView.setAdapter(myCarsAdapter);
        noCarsBtn = findViewById(R.id.no_car_button_for_new_car_owner);

    }

    private void setupUI(){
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currUser.getUid();
        myCarDB = mDatabase.child("users").child(uid).child("vehicles");
        fetchDataFromDB();
    }

    private void fetchDataFromDB() {
        myCarDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carIdList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String vehicleId = ds.getValue(String.class);
                    carIdList.add(vehicleId);
                    hasCar = true;
                }
                setupNoCarButton();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        vehiclesDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Vehicle vehicle = ds.getValue(Vehicle.class);
                    if (carIdList.contains(vehicle.getVehicleID())) {
                        carList.add(vehicle);
                    }
                }
                myCarsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setupNoCarButton() {
        if (hasCar) {
            noCarsBtn.setVisibility(View.INVISIBLE);
        } else {
            noCarsBtn.setVisibility(View.VISIBLE);
            noCarsBtn.setOnClickListener(v -> {
                Intent intent = new Intent(MyCarsActivity.this, AddVehicleActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onCarSelect(Integer position) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}