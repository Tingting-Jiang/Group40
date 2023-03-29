package edu.northeastern.group40.Project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
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

import edu.northeastern.group40.Project.Models.Brand;
import edu.northeastern.group40.Project.Models.Color;
import edu.northeastern.group40.Project.Models.Fuel;
import edu.northeastern.group40.Project.Models.Mileage;
import edu.northeastern.group40.Project.Models.MyLocation;
import edu.northeastern.group40.Project.Models.PriceOrder;
import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.Project.Models.VehicleBodyStyle;
import edu.northeastern.group40.Project.RecyclerView.CarListAdapter;
import edu.northeastern.group40.R;

public class CarListActivity extends AppCompatActivity implements SelectListener {
    private CarListAdapter carListAdapter;
    private static final String TAG = "CarListActivity";
    private final List<Vehicle> vehicleList = new ArrayList<>();
    private VehicleBodyStyle rentCarType = null;
    private String rentDate;
    private boolean displayPriceLowToHigh = true;
    private PriceOrder displayPrice = PriceOrder.PRICE_LOW_TO_HIGH;
    private Chip rentTypeChip, rentDateChip, rentPriceChip;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usersDB;
    private DatabaseReference vehicleDB;
    private MyLocation destinationLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);
        initUI();
        setupUI();
        initRecyclerView();
    }

    private void initUI() {
        // Todo: get  the filter items
        this.rentTypeChip = findViewById(R.id.car_type_chip);
        this.rentDateChip = findViewById(R.id.rent_time_chip);
        this.rentPriceChip = findViewById(R.id.rent_price_chip);

        this.rentCarType = VehicleBodyStyle.valueOf(getIntent().getStringExtra("VehicleBodyStyle"));
        this.rentDate = getIntent().getStringExtra("filter-date");
        this.destinationLocation = (MyLocation) getIntent().getSerializableExtra("destinationLocation");
        if (destinationLocation == null) {
            try {
                destinationLocation = new MyLocation(37.40273, -121.95154, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.rentTypeChip.setText(this.rentCarType.toString());
        this.rentDateChip.setText(this.rentDate);
        this.rentPriceChip.setText("$ - $$$");

        this.rentTypeChip.setOnClickListener(v -> {
            if (rentTypeChip.isChecked()) {
                Toast.makeText(CarListActivity.this, "Add car type filter", Toast.LENGTH_SHORT).show();
            } else {
                this.rentCarType = null;
            }
            updateFilter();
        });
//        this.rentPriceChip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (rentTypeChip.isChecked()) {
//                    rentPriceChip.setText("$ - $$$");
//                    displayPriceLowToHigh = true;
//                } else {
//                    rentPriceChip.setText("$$$ - $");
//                    displayPriceLowToHigh = false;
//                }
//            }
//        });
    }

    private void setupUI() {
        usersDB = mDatabase.child("Users");
        vehicleDB = mDatabase.child("Vehicles");
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currUser != null;

        //TODO: GET DATA FROM DB
        String dbString = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000006-Tue%20Mar%2028%2019%3A37%3A13%20PDT%202023?alt=media&token=dcf3b137-9a01-4b32-acba-0079849b57a4";
        MyLocation testLocation = null;
        try {
            testLocation = new MyLocation(35.40273, -120.95154, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fetchDataFromDB(this.rentCarType);
        if (vehicleList.size() == 0) {
            for (int i = 0; i < 3; i++) {
                Vehicle vehicle = new Vehicle(Brand.HONDA, Brand.Model.ACCORD, Color.WHITE, VehicleBodyStyle.CROSSOVER,
                        Fuel.GASOLINE, Mileage.BETWEEN_5K_AND_10K, 4, testLocation, 87,
                        "2023 Brand New Accord", dbString);
                vehicle.setReviewResult("4.2");
                vehicle.setReviewTotalNumber(56);
                vehicleList.add(vehicle);

            }
            Vehicle vehicle = new Vehicle(Brand.HONDA, Brand.Model.ACCORD, Color.WHITE, VehicleBodyStyle.SUV,
                    Fuel.GASOLINE, Mileage.BETWEEN_5K_AND_10K, 5, testLocation, 100,
                    "2023 Brand New Accord", dbString);
            vehicle.setReviewResult("4.2");
            vehicle.setReviewTotalNumber(56);
            vehicleList.add(vehicle);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateFilter() {
        if (this.rentCarType != null) {
            this.vehicleList.stream().filter(vehicle -> vehicle.getVehicleBodyStyle().equals(this.rentCarType));
        }
        carListAdapter.notifyDataSetChanged();
    }


    private void fetchDataFromDB(VehicleBodyStyle rentCarType) {
        vehicleDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vehicleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Vehicle currVehicle = dataSnapshot.getValue(Vehicle.class);
                    assert currVehicle != null;
                    // todo: filter cars that meet requirement
                    if (currVehicle.getVehicleBodyStyle().equals(rentCarType))
                        vehicleList.add(currVehicle);
                }
                carListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView carRecView = findViewById(R.id.car_list_recView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        carRecView.setLayoutManager(layoutManager);
        carListAdapter = new CarListAdapter(CarListActivity.this, vehicleList, this, destinationLocation);
        carRecView.setAdapter(carListAdapter);
    }

    public void backToFilterPage(View view) {
        Intent intent = new Intent(CarListActivity.this, ProjectActivity.class);
        startActivity(intent);
    }


    @Override
    public void onCarSelect(Vehicle car) {
        Intent intent = new Intent(CarListActivity.this, CarDetailActivity.class);
        intent.putExtra("carDetail", car);
        intent.putExtra("rentLength", 5);
        startActivity(intent);
       // TODO: PUT OBJECT  INTO INTENT
        // https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
        // 1. OBJECT implements Serializable and
    }
}