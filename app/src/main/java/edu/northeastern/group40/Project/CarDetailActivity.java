package edu.northeastern.group40.Project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import edu.northeastern.group40.Project.Models.AvailableDate;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.R;

public class CarDetailActivity extends AppCompatActivity {

    private Vehicle chosenVehicle;
    private TextView carTitle, carReviewNumber, carPrice, carDescription, totalPrice;
    private RatingBar ratingBar;
    private ImageView carPhoto;
    private int rentLength;
    private Button addToCartButton;
    private AvailableDate targetDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        initUI();
        chosenVehicle = (Vehicle) getIntent().getSerializableExtra("carDetail");
        targetDate = (AvailableDate) getIntent().getSerializableExtra("targetDate");
        rentLength = targetDate.calculateDuration();
        updateButtonVisibility();
        setupUI();
    }

    private void updateButtonVisibility() {
        if (rentLength == 0) {
            addToCartButton.setVisibility(View.INVISIBLE);
        } else {
            addToCartButton.setOnClickListener(v -> openPaymentActivity());
        }
    }

    private void openPaymentActivity() {
        Intent intent = new Intent(CarDetailActivity.this, PaymentActivity.class);
        intent.putExtra("carDetail", chosenVehicle);
        intent.putExtra("rentLength", rentLength);
        intent.putExtra("targetDate", targetDate);
        startActivity(intent);
    }


    private void initUI() {
        carPhoto = findViewById(R.id.car_photo);
        carTitle = findViewById(R.id.car_title_txt);
        ratingBar = findViewById(R.id.order_rating);
        carReviewNumber = findViewById(R.id.order_number);
        carPrice = findViewById(R.id.car_price);
        carDescription = findViewById(R.id.detailed_description);
        totalPrice = findViewById(R.id.total_price);
        addToCartButton = findViewById(R.id.addToCartBtn);
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