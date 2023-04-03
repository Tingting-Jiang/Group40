package edu.northeastern.group40.Project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import edu.northeastern.group40.Project.Models.Brand;
import edu.northeastern.group40.Project.Models.Color;
import edu.northeastern.group40.Project.Models.Fuel;
import edu.northeastern.group40.Project.Models.Mileage;
import edu.northeastern.group40.Project.Models.MyLocation;
import edu.northeastern.group40.Project.Models.User;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.Project.Models.VehicleBodyStyle;
import edu.northeastern.group40.R;

public class ProjectSignUpActivity extends AppCompatActivity {

    // Declare UI elements
    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private EditText mPhoneEditText;
    private Button mSignUpButton;
    private TextView mSignInLink;

    private CheckBox mIsCarOwnerCheckBox;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_sign_up);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        View rootView = findViewById(R.id.sign_up_layout);

        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(rootView);
        setContentView(scrollView);

        // Bind UI elements to Java variables
        mUsernameEditText = findViewById(R.id.username_edit_text);
        mEmailEditText = findViewById(R.id.email_edit_text);
        mPhoneEditText = findViewById(R.id.phone_number_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mIsCarOwnerCheckBox = findViewById(R.id.is_car_owner_checkbox);

        mSignUpButton = findViewById(R.id.sign_up_button);
        mSignInLink = findViewById(R.id.sign_in_link);

        // Set click listener for sign up button
        mSignUpButton.setOnClickListener(v -> {
            if (checkValidInput()) {
                writeNewUser(mUsernameEditText.getText().toString(),
                        mPasswordEditText.getText().toString(),
                        mEmailEditText.getText().toString(),
                        mPhoneEditText.getText().toString(),
                        mIsCarOwnerCheckBox.isChecked());
            }
        });

        // Set click listener for sign in link
        mSignInLink.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectSignUpActivity.this, ProjectSignInActivity.class);
            startActivity(intent);
        });
    }

    private boolean checkValidInput() {
        boolean valid = true;
        if (mUsernameEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input username", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (mEmailEditText.getText().toString().isEmpty() || !mEmailEditText.getText().toString().contains("@")) {
            Toast.makeText(this, "Please input valid email", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (mPhoneEditText.getText().toString().isEmpty() || mPhoneEditText.getText().toString().length() != 10
        || !mPhoneEditText.getText().toString().matches("^\\d+")){
            Toast.makeText(this, "Please input valid phone number", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (mPasswordEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input valid password", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void writeNewUser(String username, String password, String email, String phone, boolean isCarOwner) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String uid = Objects.requireNonNull(currentUser).getUid();
                        User user = new User(username, password, email, phone, isCarOwner, uid);
                        saveUserToDatabase(user);
                        Toast.makeText(this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProjectSignUpActivity.this, ProjectSignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(User user) {
        DatabaseReference reference = mDatabase.child("users").child(user.getUserID());
        //TODO: here add fake data to db
        saveVehicles(user);

        reference.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(ProjectSignUpActivity.this, ProjectSignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(ProjectSignUpActivity.this, "Failed to sign up with this email",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveVehicles(User user) {
        DatabaseReference vehicleDB = FirebaseDatabase.getInstance().getReference("vehicles");
        String vehicleKey = vehicleDB.push().getKey();

        String dbString = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000006-Tue%20Mar%2028%2019%3A37%3A13%20PDT%202023?alt=media&token=dcf3b137-9a01-4b32-acba-0079849b57a4";
        MyLocation testLocation = null;
        try {
            testLocation = new MyLocation(35.40273, -120.95154, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Vehicle vehicle = new Vehicle(Brand.HONDA, Brand.Model.ACCORD, Color.WHITE, VehicleBodyStyle.CROSSOVER,
                Fuel.GASOLINE, Mileage.BETWEEN_5K_AND_10K, 4, testLocation, 1,
                "2023 Brand New Accord", dbString,"04/14/2023", "04/21/2023", user.getUserID(), vehicleKey);
        vehicle.setReviewResult("4.2");
        vehicle.setReviewTotalNumber(100);

        assert vehicleKey != null;
        vehicleDB.child(vehicleKey).setValue(vehicle);

        List<String> sampleList = new ArrayList<>();
        sampleList.add(vehicleKey);
        user.setVehicles(sampleList);
    }
}