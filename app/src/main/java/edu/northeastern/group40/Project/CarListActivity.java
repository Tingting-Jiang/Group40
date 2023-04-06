package edu.northeastern.group40.Project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private String currentSearchText = "";

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


        searchView = (SearchView) findViewById(R.id.searchVehicle);
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
                return false;
            }
        });


        this.rentBrand = Brand.valueOf(getIntent().getStringExtra("VehicleBrand"));
        this.rentModel = Brand.Model.valueOf(getIntent().getStringExtra("VehicleModel"));
        this.targetAvailableDate = (AvailableDate) getIntent().getSerializableExtra("AvailableDate");
        this.destinationLocation = (MyLocation) getIntent().getSerializableExtra("destinationLocation");
        Log.i(TAG, "Got: " + rentModel + "-" + rentBrand + "-" + targetAvailableDate.toString() + "-" + destinationLocation.toString());
        if (destinationLocation == null) {
            try {
                destinationLocation = new MyLocation(37.40273, -121.95154, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        //TODO: GET DATA FROM DB
        String dbString = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000006-Tue%20Mar%2028%2019%3A37%3A13%20PDT%202023?alt=media&token=dcf3b137-9a01-4b32-acba-0079849b57a4";
        MyLocation testLocation = null;
        try {
            testLocation = new MyLocation(35.40273, -120.95154, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fetchDataFromDB();
//        if (vehicleList.size() == 0) {
//            // NO-1
//            Vehicle vehicle1 = new Vehicle(Brand.HONDA, Brand.Model.ACCORD, Color.WHITE, VehicleBodyStyle.CROSSOVER,
//                    Fuel.GASOLINE, Mileage.BETWEEN_5K_AND_10K, 4, testLocation, 1,
//                    "2023 Brand New Accord", dbString,"04/14/2023", "04/21/2023", "123", "3455");
//            vehicle1.setReviewResult("4.2");
//            vehicle1.setReviewTotalNumber(100);
//            vehicleList.add(vehicle1);
//            // NO-2
//            Vehicle vehicle2 = new Vehicle(Brand.HONDA, Brand.Model.ACCORD, Color.WHITE, VehicleBodyStyle.SUV,
//                    Fuel.GASOLINE, Mileage.LESS_THAN_10K, 5, testLocation, 2,
//                    "2023 Brand New CAMERY with Super big screen and super comfortable seats", dbString, "04/14/2023", "04/21/2023", "123", "3455");
//            vehicle2.setReviewResult("4.2");
//            vehicle2.setReviewTotalNumber(56);
//            vehicleList.add(vehicle2);
//            // NO-3
//            Vehicle vehicle3 = new Vehicle(Brand.HONDA, Brand.Model.ACCORD, Color.WHITE, VehicleBodyStyle.SUV,
//                    Fuel.GASOLINE, Mileage.BETWEEN_10K_AND_100K, 5, testLocation, 3,
//                    "2023 Brand New SUV", dbString, "04/14/2023", "04/21/2023", "123", "3455");
//            vehicle3.setReviewResult("2.9");
//            vehicle3.setReviewTotalNumber(90);
//            vehicleList.add(vehicle3);
//            // NO-4
//            Vehicle vehicle4 = new Vehicle(Brand.HONDA, Brand.Model.ACCORD, Color.WHITE, VehicleBodyStyle.SUV,
//                    Fuel.GASOLINE, Mileage.BETWEEN_5K_AND_10K, 5, testLocation, 4,
//                    "2023 Brand New SUV", dbString, "04/14/2023", "04/21/2023", "123", "3455");
//            vehicle4.setReviewResult("4.3");
//            vehicle4.setReviewTotalNumber(130);
//            vehicleList.add(vehicle4);
//        }

//        syncBackupList();
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
                    // todo: filter cars that meet requirement
//                    if (!currVehicle.getOwnerID().equals(currUser.getUid())
//                            && currVehicle.getBrand().equals(rentBrand)
//                            && currVehicle.getModel().equals(rentModel)
//                            && currVehicle.getAvailableDate().isAvailable(targetAvailableDate)
//                        ) {
//                            vehicleList.add(currVehicle);
//                    }
                    Log.d(TAG, currVehicle.toString());
                    if (currVehicle.getImage() == null) {
                        currVehicle.setCarImage("https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000350-Thu%20Apr%2006%2011%3A29%3A49%20PDT%202023?alt=media&token=7eb807c1-ef3b-4221-91a2-17f4b5a00b8e");
                    }
                    vehicleList.add(currVehicle);



                }
                Toast.makeText(CarListActivity.this, "GET DATA LEN： " + vehicleList.size(), Toast.LENGTH_LONG).show();
                syncBackupList();
                carListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
       // TODO: PUT OBJECT  INTO INTENT
        // https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
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
            return v2.getReviewTotalNumber() - v1.getReviewTotalNumber();
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
            return (int) v2.getPlace().distanceToInMiles(destination) - (int) v1.getPlace().distanceToInMiles(destination);
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