package edu.northeastern.group40.Project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group40.A8.Models.User;
import edu.northeastern.group40.Project.Models.Brand;
import edu.northeastern.group40.Project.Models.Car;
import edu.northeastern.group40.Project.Models.Color;
import edu.northeastern.group40.Project.Models.Fuel;
import edu.northeastern.group40.Project.Models.Mileage;
import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.Project.Models.VehicleBodyStyle;
import edu.northeastern.group40.Project.RecyclerView.CarListAdapter;
import edu.northeastern.group40.R;

public class CarListActivity extends AppCompatActivity implements SelectListener {
    private CarListAdapter carListAdapter;
    private static final String TAG = "CarListActivity";
    private final List<Vehicle> vehicleList = new ArrayList<>();
    private String rentCarType;
    private String rentDate;
    private boolean displayPriceLowToHigh = false;
    private Chip rentTypeChip, rentDateChip, rentPriceChip;
    private static final String PRICE_LOW_TO_HIGH = "Low to High";
    private static final String PRICE_HIGH_TO_LOW = "High to Low";
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usersDB;
    private DatabaseReference vehicleDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);
        init();
        initRecyclerView();
    }


    private void init() {
        // Todo: get  the filter items
        usersDB = mDatabase.child("Users");
        vehicleDB = mDatabase.child("Vehicles");
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currUser != null;

        this.rentTypeChip = findViewById(R.id.car_type_chip);
        this.rentDateChip = findViewById(R.id.rent_time_chip);
        this.rentPriceChip = findViewById(R.id.rent_price_chip);

        this.rentCarType = getIntent().getStringExtra("filter-type");
        this.rentDate = getIntent().getStringExtra("filter-date");
        this.displayPriceLowToHigh = getIntent().getBooleanExtra("filter-price", false);

        this.rentTypeChip.setText(this.rentCarType);
        this.rentDateChip.setText(this.rentDate);
        this.rentPriceChip.setText(this.displayPriceLowToHigh ? PRICE_LOW_TO_HIGH: PRICE_HIGH_TO_LOW);

        this.rentTypeChip.setOnClickListener(v -> {
            if (rentTypeChip.isChecked()) {
                Toast.makeText(CarListActivity.this, "Add car type filter", Toast.LENGTH_SHORT).show();
            }
        });
//        this.rentPriceChip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (rentPriceChip.isChecked()) {
//                    rentPriceChip.setChecked(false);
//                    rentPriceChip.setText(PRICE_HIGH_TO_LOW);
//                } else if (!rentPriceChip.isChecked()) {
//                    rentPriceChip.setChecked(true);
//                    rentPriceChip.setText(PRICE_LOW_TO_HIGH);
//                }
//            }
//        });


        //TODO: GET DATA FROM DB
        String dbString = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000348-Tue%20Mar%2028%2018%3A55%3A01%20PDT%202023?alt=media&token=ae842409-2594-4c9e-b5db-e1af597cd6e3";

        fetchDataFromDB();
        if (vehicleList.size() == 0) {
            for (int i = 0; i < 5; i++) {
                Vehicle vehicle = new Vehicle(Brand.HONDA, Brand.Model.ACCORD, Color.WHITE, VehicleBodyStyle.CROSSOVER,
                        Fuel.GASOLINE, Mileage.BETWEEN_5K_AND_10K, 4, 87,
                        "2023 Brand New Accord", dbString);
                vehicle.setReviewResult("4.1");
                vehicle.setReviewTotalNumber(56);
                vehicleList.add(vehicle);

            }
        }


    }

    private void fetchDataFromDB() {
        vehicleDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                vehicleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Vehicle currVehicle = dataSnapshot.getValue(Vehicle.class);
                    assert currVehicle != null;
                    // todo: filter cars that meet requirement
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
        carListAdapter = new CarListAdapter(CarListActivity.this, vehicleList, this);
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