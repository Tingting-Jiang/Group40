package edu.northeastern.group40.Project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group40.A8.MessageActivity;
import edu.northeastern.group40.A8.RecyclerView.MessageListAdapter;
import edu.northeastern.group40.Project.Models.Car;
import edu.northeastern.group40.Project.RecyclerView.CarListAdapter;
import edu.northeastern.group40.R;

public class CarListActivity extends AppCompatActivity {
    private CarListAdapter carListAdapter;
    private static final String TAG = "CarListActivity";
    private final List<Car> carList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);
        init();
        initRecyclerView();
    }


    private void init() {
        //TODO: GET DATA FROM DB
        for (int i = 0; i < 5; i++) {
            carList.add(new Car("2022 Lexus", "3000", "5.0", String.valueOf(i), "9.8", "123"));
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