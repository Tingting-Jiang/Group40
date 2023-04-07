package edu.northeastern.group40.Project;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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



    //TODO: -------remove below------
    DatabaseReference vehicleDB;
    Map<Brand, List<Brand.Model>> brandModelMap = new HashMap<>();
    List<MyLocation> locationList = new ArrayList<>();
    private static final String TAG = "FakeData";
    private static final String NAME = "Test";
    private static final String PASSWORD = "-1234";
    private static final String EMAIL = "@123.com";
    private static final String PHONE = "09876543210";
    private static final String BLUE_CAR = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000351-Thu%20Apr%2006%2011%3A27%3A12%20PDT%202023?alt=media&token=2cf1e1be-9be3-4feb-904b-dc88d0bf95d3";
    private static final String BLACK_CAR = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000353-Thu%20Apr%2006%2011%3A28%3A48%20PDT%202023?alt=media&token=7d612dd9-c2d1-4fc6-afcb-ca3cbd90b248";
    private static final String RED_CAR = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000350-Thu%20Apr%2006%2011%3A29%3A49%20PDT%202023?alt=media&token=7eb807c1-ef3b-4221-91a2-17f4b5a00b8e";
    private static final String WHITE_CAR = "https://firebasestorage.googleapis.com/v0/b/mobile-project-5dfc0.appspot.com/o/images%2Fimage%253A1000000354-Thu%20Apr%2006%2011%3A29%3A23%20PDT%202023?alt=media&token=43823e7c-3e13-4989-bcad-85cb61165ec5";
    RandomEnumGenerator color = new RandomEnumGenerator(Color.class);
    RandomEnumGenerator fuel = new RandomEnumGenerator(Fuel.class);
    RandomEnumGenerator bodyStyle = new RandomEnumGenerator(VehicleBodyStyle.class);
    RandomEnumGenerator mileage = new RandomEnumGenerator(Mileage.class);
    RandomEnumGenerator brand = new RandomEnumGenerator(Brand.class);
    // --- remove above---------


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
        mUsernameEditText = findViewById(R.id.email_sign_in_edit_text);
        mEmailEditText = findViewById(R.id.email_edit_text);
        mPhoneEditText = findViewById(R.id.phone_number_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mIsCarOwnerCheckBox = findViewById(R.id.is_car_owner_checkbox);

        mSignUpButton = findViewById(R.id.sign_up_button);
        mSignInLink = findViewById(R.id.sign_in_link);

        // Set click listener for sign up button
        mSignUpButton.setOnClickListener(v -> {
//            if (checkValidInput()) {
//                writeNewUser(mUsernameEditText.getText().toString(),
//                        mPasswordEditText.getText().toString(),
//                        mEmailEditText.getText().toString(),
//                        mPhoneEditText.getText().toString(),
//                        mIsCarOwnerCheckBox.isChecked());
//            }
            addFakeData();
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
                        Exception error = task.getException();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Sign up failed")
                                .setMessage(error.getMessage())
                                .setPositiveButton("OK", null)
                                .create()
                                .show();
                    }
                });
    }

    private void saveUserToDatabase(User user) {
        DatabaseReference reference = mDatabase.child("users").child(user.getUserID());
        //TODO: here add fake data to db
//        saveVehicles(user);

        reference.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(ProjectSignUpActivity.this, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(ProjectSignUpActivity.this, "Failed to sign up with this email",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String getURL(Color testColor) {
        switch ((Color) testColor) {
            case WHITE:
                return WHITE_CAR;
            case BLACK:
                return BLACK_CAR;
            case RED:
                return RED_CAR;
            default:
                return BLUE_CAR;
        }
    }


    // TODO: REMOVE BELOW

    private void addFakeData() {
        initBrandModels();
        initLocations();
        int idx = 0;
        generateUser(idx);

    }


    private void initBrandModels() {
        brandModelMap.put(Brand.HONDA, Arrays.asList(Brand.Model.ACCORD, Brand.Model.CIVIC, Brand.Model.CR_V, Brand.Model.FIT, Brand.Model.ODYSSEY, Brand.Model.PILOT, Brand.Model.RIDGELINE, Brand.Model.CITY, Brand.Model.BRIO, Brand.Model.AMZE));
        brandModelMap.put(Brand.TOYOTA, Arrays.asList(Brand.Model.CAMRY, Brand.Model.COROLLA, Brand.Model.RAV4, Brand.Model.PRIUS, Brand.Model.YARIS, Brand.Model.HIGHLANDER, Brand.Model.TACOMA, Brand.Model.SIENNA, Brand.Model.AVALON, Brand.Model.SUPRA));
        brandModelMap.put(Brand.FORD, Arrays.asList(Brand.Model.FOCUS, Brand.Model.FIESTA, Brand.Model.FUSION, Brand.Model.MUSTANG, Brand.Model.ESCAPE, Brand.Model.EDGE, Brand.Model.EXPLORER, Brand.Model.RANGER, Brand.Model.EXPEDITION, Brand.Model.TAURUS));
        brandModelMap.put(Brand.CHEVROLET, Arrays.asList(Brand.Model.CRUZE, Brand.Model.MALIBU, Brand.Model.IMPALA, Brand.Model.CAMARO, Brand.Model.EQUINOX, Brand.Model.TRAVERSE, Brand.Model.COLORADO, Brand.Model.SILVERADO, Brand.Model.SUBURBAN, Brand.Model.TAHOE));
        brandModelMap.put(Brand.NISSAN, Arrays.asList(Brand.Model.ALTIMA, Brand.Model.MAXIMA, Brand.Model.ROGUE, Brand.Model.SENTRA, Brand.Model.VERSA, Brand.Model.MURANO, Brand.Model.PATHFINDER, Brand.Model.TITAN, Brand.Model.ARMADA, Brand.Model.KICKS));
        brandModelMap.put(Brand.BMW, Arrays.asList(Brand.Model.X1, Brand.Model.X3, Brand.Model.X5, Brand.Model.X6, Brand.Model.Z4, Brand.Model.I3, Brand.Model.I8));
        brandModelMap.put(Brand.MERCEDES_BENZ, Arrays.asList(Brand.Model.C_CLASS, Brand.Model.E_CLASS, Brand.Model.S_CLASS, Brand.Model.A_CLASS, Brand.Model.CLASSE_GLA, Brand.Model.GLE_CLASS, Brand.Model.SLC_CLASS, Brand.Model.GLK_CLASS, Brand.Model.GL_CLASS, Brand.Model.SLS_CLASS));
        brandModelMap.put(Brand.AUDI, Arrays.asList(Brand.Model.A3, Brand.Model.A4, Brand.Model.A5, Brand.Model.A6, Brand.Model.A7, Brand.Model.A8, Brand.Model.Q3, Brand.Model.Q5, Brand.Model.Q7, Brand.Model.R8));
        brandModelMap.put(Brand.LEXUS, Arrays.asList(Brand.Model.ES, Brand.Model.IS, Brand.Model.LC, Brand.Model.LS, Brand.Model.LX, Brand.Model.NX, Brand.Model.RC, Brand.Model.RX, Brand.Model.UX, Brand.Model.GX));
        brandModelMap.put(Brand.MAZDA, Arrays.asList(Brand.Model.MAZDA3, Brand.Model.MAZDA6, Brand.Model.MX5_MIATA, Brand.Model.CX3, Brand.Model.CX5, Brand.Model.CX9, Brand.Model.MAZDA2, Brand.Model.MAZDA5, Brand.Model.CX30, Brand.Model.MAZDA_CX50));

//        Color randomColor = (Color) color.randomEnum();
    }

    public void initLocations() {
        try {
            // CA bay area
            locationList.add(new MyLocation(37.50273, -121.95154, this));  //0
            locationList.add(new MyLocation(37.7749, -122.4194, this));
            locationList.add(new MyLocation(37.8044, 122.2711, this));
            locationList.add(new MyLocation(37.3382, 121.8863, this));
            locationList.add(new MyLocation(37.4419, 122.1430, this));
//            locationList.add(new MyLocation(37.8716, 122.2727, this));
//            locationList.add(new MyLocation(37.5620, 122.3255, this));
//            locationList.add(new MyLocation(37.3541, 121.9552, this));
//            locationList.add(new MyLocation(37.5485, 121.9886, this));
//            locationList.add(new MyLocation(37.4852, 122.2364, this)); // 9
//            locationList.add(new MyLocation(37.3688, 122.0363, this)); // 10

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class RandomEnumGenerator<T extends Enum<T>> {
        private final Random PRNG = new Random();
        private final T[] values;

        public RandomEnumGenerator(Class<T> e) {
            values = e.getEnumConstants();
        }

        public T randomEnum() {
            return values[PRNG.nextInt(values.length)];
        }
    }

    public void generateUser(int idx) {
        String newUserName = NAME + idx;
        mAuth.createUserWithEmailAndPassword(newUserName + EMAIL, newUserName + PASSWORD)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String uid = Objects.requireNonNull(currentUser).getUid();
                        User user = new User(newUserName, newUserName + PASSWORD, newUserName + EMAIL, PHONE, true, uid);
                        saveFakeData(user, idx);

                    } else {
                        // If sign in fails, display a message to the user.
                        Exception error = task.getException();
                        Log.e(TAG, "SignUp failed: " + error);
                    }
                });
    }

    private void saveFakeData(User user, int idx) {
        DatabaseReference reference = mDatabase.child("users").child(user.getUserID());
        //TODO: here add fake data to db
        saveVehicles(user, idx);

        reference.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProjectSignUpActivity.this, "Saved to DB",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProjectSignUpActivity.this, "Failed to sign up with this email",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveVehicles(User user, int idx) {
        vehicleDB = FirebaseDatabase.getInstance().getReference("vehicles");
        String vehicleKey = vehicleDB.push().getKey();
        Brand testBrand = (Brand) brand.randomEnum();
        List<Brand.Model> modelList = brandModelMap.get(testBrand);
        Brand.Model testModel = modelList.get(new Random().nextInt(modelList.size()));
        VehicleBodyStyle testVehicleBodyStyle = (VehicleBodyStyle) bodyStyle.randomEnum();
        Fuel testFuel = (Fuel) fuel.randomEnum();

        Mileage testMileage = (Mileage) mileage.randomEnum();
        Color testColor = (Color) color.randomEnum();
        String imageURL = getURL(testColor);
        Log.d(TAG, "url: " + imageURL);
        MyLocation testLocation = locationList.get(idx);
        int testCap = new Random().nextInt(7);
        int max = 110, min = 70;
        int testPrice = (int)Math.floor(Math.random() * (max - min + 1) + min);

        int testReviewNum = (int)Math.floor(Math.random() * (max - min + 1) + min);

        double max1 = 5.0f, min1 = 2.0f;
        double randomReview = min + new Random().nextDouble() * (max1 - min1);
        String title = "2023 " + testColor.toString() + " " + testBrand + " " + testModel + " ";

        Vehicle vehicle = new Vehicle(testBrand, testModel, testColor, testVehicleBodyStyle,
                testFuel, testMileage, testCap, testLocation, testPrice,
                title,  imageURL,"04/14/2023", "04/21/2023", user.getUserID(), vehicleKey);
        vehicle.setReviewResult(String.valueOf(randomReview));
        vehicle.setReviewTotalNumber(testReviewNum);

        assert vehicleKey != null;
        vehicleDB.child(vehicleKey).setValue(vehicle);

        List<String> sampleList = new ArrayList<>();
        sampleList.add(vehicleKey);
        user.setVehicles(sampleList);
    }

}