package edu.northeastern.group40.Project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group40.Project.Models.Car;
import edu.northeastern.group40.Project.RecyclerView.CarListAdapter;
import edu.northeastern.group40.R;

public class CarListActivity extends AppCompatActivity {
    private CarListAdapter carListAdapter;
    private static final String TAG = "CarListActivity";
    private final List<Car> carList = new ArrayList<>();
    private String rentCarType;
    private String rentDate;
    private boolean displayPriceLowToHigh = false;
    private Chip rentTypeChip, rentDateChip, rentPriceChip;
    private static final String PRICE_LOW_TO_HIGH = "Low to High";
    private static final String PRICE_HIGH_TO_LOW = "High to Low";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);
        init();
        initRecyclerView();
    }


    private void init() {
        // Todo: get  the filter items

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
        if (carList.size() == 0) {
            for (int i = 0; i < 5; i++) {
                carList.add(new Car("2022 Lexus", "3000", "5.0", String.valueOf(i), "9.8", "123","88"));
            }
        }


    }

    private void initRecyclerView() {
        RecyclerView carRecView = findViewById(R.id.car_list_recView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        carRecView.setLayoutManager(layoutManager);
        carListAdapter = new CarListAdapter(CarListActivity.this, carList);
        carRecView.setAdapter(carListAdapter);

    }


}