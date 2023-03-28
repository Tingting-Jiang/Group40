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

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group40.A8.Models.User;
import edu.northeastern.group40.Project.Models.Car;
import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.Project.RecyclerView.CarListAdapter;
import edu.northeastern.group40.R;

public class CarListActivity extends AppCompatActivity implements SelectListener {
    private CarListAdapter carListAdapter;
    private static final String TAG = "CarListActivity";
    private final List<Car> vehicleList = new ArrayList<>();
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

        fetchDataFromDB();
        if (vehicleList.size() == 0) {
            for (int i = 0; i < 5; i++) {
                vehicleList.add(new Car("2022 Lexus", "3000", "5.0", String.valueOf(i), "9.8", "123","88"));
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
                    Car currCar = dataSnapshot.getValue(Car.class);
                    assert currCar != null;
                    // todo: filter cars that meet requirement

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
    public void onCarSelect(Car car) {
        Intent intent = new Intent(CarListActivity.this, CarDetailActivity.class);
        intent.putExtra("carDetail", car);
        intent.putExtra("rentLength", 5);
        startActivity(intent);
       // TODO: PUT OBJECT  INTO INTENT
        // https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
        // 1. OBJECT implements Serializable and
    }
}