package edu.northeastern.group40.Project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import edu.northeastern.group40.Project.Models.AvailableDate;
import edu.northeastern.group40.Project.Models.Brand;
import edu.northeastern.group40.Project.Models.MyLocation;
import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.Project.RecyclerView.CarListAdapter;
import edu.northeastern.group40.R;

public class CarListActivity extends AppCompatActivity implements SelectListener {
    private CarListAdapter carListAdapter;
    private static final String TAG = "CarListActivity";
    private static final String PRICE = "PRICE";
    private static final String MILEAGE = "MILEAGE";
    private static final String DISTANCE = "DISTANCE";
    private static final String REVIEW_COUNT = "REVIEW_COUNT";
    private static final String DEFAULT = "";
    private final List<Vehicle> backupVehicleList = new ArrayList<>();
    private List<Vehicle> vehicleList = new ArrayList<>();
    private Brand.Model rentModel = null;
    private Brand rentBrand = null;
    private AvailableDate targetAvailableDate;
    private Button priceSort, reviewSort, distanceSort, mileageSort;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usersDB;
    private DatabaseReference vehicleDB;
    private FirebaseUser currUser = null;
    private MyLocation destinationLocation = null;
    private SearchView searchView;
    private RecyclerView carRecView;
    private String selectedFilter = "";
    private Integer yellow, blue, black, white;
    private Button noCarBtn;
    private static final String BLUE_CAR = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000360-Sat%20Apr%2008%2011%3A11%3A54%20PDT%202023?alt=media&token=b41ebfd4-d8e1-4838-8dbe-d65f1746136f";
    private static final String BLACK_CAR = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000364-Sat%20Apr%2008%2011%3A08%3A22%20PDT%202023?alt=media&token=51f262f7-4e4e-4006-ac11-31c9963be660";
    private static final String RED_CAR = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000362-Sat%20Apr%2008%2011%3A12%3A25%20PDT%202023?alt=media&token=664dd4f4-c7db-4af5-bbc4-424987c87d16";
    private static final String WHITE_CAR = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000359-Sat%20Apr%2008%2011%3A12%3A50%20PDT%202023?alt=media&token=6ef12cc3-7c93-4f08-bbbc-314ce0d39005";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);
        initUI();
        initRecyclerView();
        setupUI();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void initUI() {
        // Todo: get  the filter items
        this.mileageSort = findViewById(R.id.mileageSort);
        this.priceSort = findViewById(R.id.priceSort);
        this.distanceSort = findViewById(R.id.distanceSort);
        this.reviewSort = findViewById(R.id.reviewSort);
        this.yellow = ContextCompat.getColor(getApplicationContext(),R.color.light_yellow);
        this.blue = ContextCompat.getColor(getApplicationContext(),R.color.sky_blue);
        this.black = ContextCompat.getColor(getApplicationContext(),R.color.black);
        this.white = ContextCompat.getColor(getApplicationContext(),R.color.white);
        this.noCarBtn = findViewById(R.id.no_cars_txt);
        initButton();


        searchView = findViewById(R.id.searchVehicle);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                
                ArrayList<Vehicle> filterList = new ArrayList<>();
                for (Vehicle v : backupVehicleList) {
                    if (v.toString().contains(newText.toUpperCase(Locale.ROOT))) {
                        filterList.add(v);
                    }
                }
                vehicleList.clear();
                vehicleList.addAll(filterList);
                carListAdapter.notifyDataSetChanged();
                initButton();
                return false;
            }
        });


        this.rentBrand = getIntent().getStringExtra("VehicleBrand") == null ? null: Brand.valueOf(getIntent().getStringExtra("VehicleBrand"));
        this.rentModel = getIntent().getStringExtra("VehicleModel") == null ? null : Brand.Model.valueOf(getIntent().getStringExtra("VehicleModel"));
        this.targetAvailableDate = (AvailableDate) getIntent().getSerializableExtra("AvailableDate");
        this.destinationLocation = (MyLocation) getIntent().getSerializableExtra("destinationLocation");
        if (destinationLocation == null) {
            try {
                destinationLocation = new MyLocation(37.40273, -121.95154, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void initButton() {
        if (vehicleList.size() == 0) {
            noCarBtn.setVisibility(View.VISIBLE);
            noCarBtn.setOnClickListener(v -> {
               onBackPressed();
            });
        } else {
            noCarBtn.setVisibility(View.GONE);
            }

    }

    private void lookSelected(Button button) {
        button.setTextColor(black);
        button.setBackgroundColor(yellow);
    }

    private void lookUnselected(Button button) {
        button.setTextColor(white);
        button.setBackgroundColor(blue);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateFilter() {
        switch (selectedFilter){
            case PRICE:
                vehicleList.sort(new SortByRentPrice());
                break;
            case DISTANCE:
                vehicleList.sort(new SortByDistance(destinationLocation));
                break;
            case REVIEW_COUNT:
                vehicleList.sort(new SortByReview());
                break;
            case MILEAGE:
                vehicleList.sort(new SortByMileage());
                break;
            default:
                Collections.shuffle(vehicleList);
                unSelectAll();
                break;
        }
        carListAdapter.notifyDataSetChanged();
        initButton();
    }

    private void unselectOtherSort(String originSort) {
        switch (originSort){
            case "price":
                lookUnselected(priceSort);
                break;
            case "distance":
                lookUnselected(distanceSort);
                break;
            case "review_count":
                lookUnselected(reviewSort);
                break;
            case "mileage":
                lookUnselected(mileageSort);
                break;
            default:
                unSelectAll();
                break;
        }

    }

    private void unSelectAll() {
        lookUnselected(priceSort);
        lookUnselected(distanceSort);
        lookUnselected(reviewSort);
        lookUnselected(mileageSort);
    }

    private void setupUI() {
        usersDB = mDatabase.child("users");
        vehicleDB = mDatabase.child("vehicles");
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currUser != null;

        fetchDataFromDB();
    }

    private void syncBackupList() {
        if (backupVehicleList.size() == 0) {
            backupVehicleList.addAll(vehicleList);
        }
    }


    private void fetchDataFromDB() {
        vehicleDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vehicleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Vehicle currVehicle = dataSnapshot.getValue(Vehicle.class);
                    assert currVehicle != null;
//                    if (!currVehicle.getOwnerID().equals(currUser.getUid())) {
//                        if ( rentBrand != null && rentModel != null) {
//                            if (currVehicle.getBrand().equals(rentBrand)
//                                    && currVehicle.getModel().equals(rentModel)
//                                    && meetRequirement(currVehicle)
//                            ) {
//                                vehicleList.add(currVehicle);
//                            }
//                        } else if (rentBrand == null && meetRequirement(currVehicle)) {
//                            Log.i(TAG, "image: " + currVehicle.getCarImage());
//                            vehicleList.add(currVehicle);
//                        }
//                    }
                    if (currVehicle.getCarImage() == null || currVehicle.getCarImage().equals("")) {
                        switch (currVehicle.getColor()) {
                            case RED:
                                currVehicle.setCarImage(RED_CAR);
                                break;
                            case BLACK:
                                currVehicle.setCarImage(BLACK_CAR);
                                break;
                            case BLUE:
                                currVehicle.setCarImage(BLUE_CAR);
                                break;
                            default:
                                currVehicle.setCarImage(WHITE_CAR);
                                break;
                        }
                        vehicleDB.child(currVehicle.getVehicleID()).setValue(currVehicle);
                    }
                    vehicleList.add(currVehicle);
                }
                syncBackupList();
                carListAdapter.notifyDataSetChanged();
                initButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean meetRequirement(Vehicle vehicle) {
        return vehicle.isAvailable() &&
                vehicle.getAvailableDate().isAvailable(targetAvailableDate) &&
                vehicle.getPlace().getStateName().equals(destinationLocation.getStateName());
    }

    private void initRecyclerView() {
        carRecView = findViewById(R.id.car_list_recView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        carRecView.setLayoutManager(layoutManager);
        carListAdapter = new CarListAdapter(CarListActivity.this, vehicleList, this, destinationLocation);
        carRecView.setAdapter(carListAdapter);
    }

    public void backToFilterPage(View view) {
        onBackPressed();
    }


    @Override
    public void onCarSelect(Integer position) {
        Intent intent = new Intent(CarListActivity.this, CarDetailActivity.class);
        Vehicle car = vehicleList.get(position);
        intent.putExtra("carDetail", car);
        intent.putExtra("targetDate", targetAvailableDate);
        startActivity(intent);
    }

    static class SortByRentPrice implements Comparator<Vehicle> {

        @Override
        public int compare(Vehicle v1, Vehicle v2) {
            return v1.getRentPrice() - v2.getRentPrice();
        }
    }
    static class SortByReview implements Comparator<Vehicle> {

        @Override
        public int compare(Vehicle v1, Vehicle v2) {
            Double review1 = Double.parseDouble(v1.getReviewResult());
            Double review2 = Double.parseDouble(v2.getReviewResult());
            return Double.compare(review2, review1);
        }
    }
    static class SortByMileage implements Comparator<Vehicle> {

        @Override
        public int compare(Vehicle v1, Vehicle v2) {
            return v1.getMileage().compareTo(v2.getMileage());
        }
    }
    static class SortByDistance implements Comparator<Vehicle> {
        private MyLocation destination;

        public SortByDistance(MyLocation destination) {
            this.destination = destination;
        }

        @Override
        public int compare(Vehicle v1, Vehicle v2) {
            return (int) v1.getPlace().distanceToInMiles(destination) - (int) v2.getPlace().distanceToInMiles(destination);
        }
    }

    public void onSortPrice(View view) {
        updateSort(PRICE, priceSort);
    }

    public void onSortMileage(View view) {
        updateSort(MILEAGE, mileageSort);
    }
    public void onSortDistance(View view) {
        updateSort(DISTANCE, distanceSort);

    }
    public void onSortReviewCount(View view) {
       updateSort(REVIEW_COUNT, reviewSort);
    }

    private void updateSort(String status, Button button) {
        if (selectedFilter.equals(status)) {
            selectedFilter = DEFAULT;
            lookUnselected(button);
        }
        else {
            unselectOtherSort(selectedFilter);
            lookSelected(button);
            selectedFilter = status;
        }
        updateFilter();

    }
}