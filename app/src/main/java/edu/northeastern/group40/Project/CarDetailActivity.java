package edu.northeastern.group40.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.R;

public class CarDetailActivity extends AppCompatActivity {

    private Vehicle chosenVehicle;
    private TextView carTitle, carReviewNumber, carPrice, carDescription, totalPrice;
    private RatingBar ratingBar;
    private ImageView carPhoto;
    private int rentLength;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        initUI();
        chosenVehicle = (Vehicle) getIntent().getSerializableExtra("carDetail");
        rentLength = getIntent().getIntExtra("rentLength", 0);
        setupUI();
    }



    private void initUI() {
        carPhoto = findViewById(R.id.car_photo);
        carTitle = findViewById(R.id.car_title_txt);
        ratingBar = findViewById(R.id.order_rating);
        carReviewNumber = findViewById(R.id.order_number);
        carPrice = findViewById(R.id.car_price);
        carDescription = findViewById(R.id.detailed_description);
        totalPrice = findViewById(R.id.total_price);
    }

    @SuppressLint("SetTextI18n")
    private void setupUI() {
        float totalPriceNeed = rentLength * chosenVehicle.getRentPrice();
        Picasso.get()
                .load(chosenVehicle.getImage())
                .fit()
                .into(carPhoto);

        // TODO: CHANGE the image to car image
        carTitle.setText(chosenVehicle.getTitle());
        ratingBar.setRating(Float.parseFloat(chosenVehicle.getReviewResult()));
        carReviewNumber.setText("by "+ chosenVehicle.getReviewTotalNumber()+ " users ");
        carPrice.setText("$ " + chosenVehicle.getRentPrice());
        carDescription.setText(chosenVehicle.toString());
        totalPrice.setText("Total: $ " + totalPriceNeed);
        //TODO: CHANGE CAR this to real car total price
    }

    public void onBackToCarList(View view) {
        onBackPressed();
    }
}