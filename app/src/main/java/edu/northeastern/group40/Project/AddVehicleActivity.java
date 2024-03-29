package edu.northeastern.group40.Project;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


import edu.northeastern.group40.Project.Models.Brand;
import edu.northeastern.group40.Project.Models.Color;
import edu.northeastern.group40.Project.Models.Fuel;
import edu.northeastern.group40.Project.Models.MyLocation;
import edu.northeastern.group40.Project.Models.Mileage;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.Project.Models.VehicleBodyStyle;
import edu.northeastern.group40.R;

public class AddVehicleActivity extends AppCompatActivity {

    private static final String TAG = "AddVehicle";
    private static final String API_KEY = "AIzaSyAAhr6pbohtGh_mPid3btYA2XQZ9KlJ9Bs";
    private List<Place.Field> placeFields;
    private Place inputPlace;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Uri imageUploadUri;
    private String imageUrlInDB = "";
    private TextView selectedStartDateTextView;
    private TextView selectedEndDateTextView;
    private Calendar startCalendar, endCalendar;
    private boolean imageUploaded;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private DatabaseReference vehicleInUserDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("vehicles");
        vehicleInUserDB = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("vehicles");
        inputPlace = null;
        imageUploaded = false;
        setContentView(R.layout.activity_project_add_vehicle);
        placeFields = Arrays.asList(Place.Field.ADDRESS, Place.Field.ID, Place.Field.LAT_LNG);
        AutoCompleteTextView colorMenu = findViewById(R.id.colorMenu);
        AutoCompleteTextView bodyStyleMenu = findViewById(R.id.bodyStyleMenu);
        AutoCompleteTextView brandMenu = findViewById(R.id.brandMenu);
        AutoCompleteTextView modelMenu = findViewById(R.id.modelMenu);
        AutoCompleteTextView fuelMenu = findViewById(R.id.fuelMenu);
        AutoCompleteTextView mileageMenu = findViewById(R.id.mileageMenu);
        AutoCompleteTextView capacityMenu = findViewById(R.id.capacityMenu);
        AutoCompleteTextView[] menuList = new AutoCompleteTextView[]
                {colorMenu, bodyStyleMenu, brandMenu, modelMenu, fuelMenu, mileageMenu, capacityMenu};
        for (AutoCompleteTextView menu : menuList) {
            menu.setOnItemClickListener((parent, view, position, id) -> menu.setError(null));
        }
        initDropDownMenu(colorMenu, Color.class);
        initDropDownMenu(bodyStyleMenu, VehicleBodyStyle.class);
        initDropDownMenu(brandMenu, Brand.class);

        // init model based on selected brand
        brandMenu.setOnItemClickListener((parent, view, position, id) -> {
            brandMenu.setError(null);
            Brand brand = (Brand) parent.getItemAtPosition(position);
            ArrayAdapter<Brand.Model> modelAdapter =
                    new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, brand.getModels());
            modelMenu.setAdapter(modelAdapter);
        });



        initDropDownMenu(fuelMenu, Fuel.class);
        initDropDownMenu(mileageMenu, Mileage.class);
        capacityMenu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(1, 2, 3, 4, 5, 6)));

        Places.initialize(getApplicationContext(), API_KEY);
        PlacesClient placesClient = Places.createClient(this);

        AutoCompleteTextView streetInput = findViewById(R.id.placeInput);
        TextView cityInput = findViewById(R.id.city);
        TextView stateInput = findViewById(R.id.state);
        TextView zipCodeInput = findViewById(R.id.zip_code);

        List<AutocompletePrediction> predictions = new ArrayList<>();
        PlaceAutocompleteAdapter placeAdapter = new PlaceAutocompleteAdapter(this, predictions);
        streetInput.setAdapter(placeAdapter);

        streetInput.setOnItemClickListener((parent, view, position, id) -> {
            streetInput.setError(null);
            AutocompletePrediction prediction = (AutocompletePrediction) parent.getItemAtPosition(position);
            streetInput.setText(prediction.getPrimaryText(null));
            FetchPlaceRequest request = FetchPlaceRequest.builder(prediction.getPlaceId(), placeFields)
                    .build();
            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                inputPlace = place;

                String address = place.getAddress();
                Log.w(TAG, address);
                assert address != null;
                String[] parts = address.split(", ");
                int len = parts.length;
                String stateCode = parts[len - 2];
                String[] stateAndCode = stateCode.split(" ");
                cityInput.setText(parts[len - 3]);
                stateInput.setText(stateAndCode[0]);
                zipCodeInput.setText(stateAndCode[1]);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    Log.e(TAG, "Place not found: " + exception.getMessage() + ", statusCode=" + statusCode);
                }
            });
        });


        streetInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                inputPlace = null;
                String input = s.toString();
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(input)
                        .setCountry("US")
                        .build();
                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    predictions.clear();
                    predictions.addAll(response.getAutocompletePredictions());
                    predictions.removeIf(prediction -> prediction.getFullText(null).toString().split(",").length != 4);
                    placeAdapter.notifyDataSetChanged();
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                });
            }
        });

        AutoCompleteTextView vehicleTitle = findViewById(R.id.titleInput);
        AutoCompleteTextView rentPrice = findViewById(R.id.priceInput);
        ImageView carImageView = findViewById(R.id.carImage);

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            String title = vehicleTitle.getText().toString();
            Color selectedColor = getResult(colorMenu, Color.class);
            VehicleBodyStyle selectedVehicleBodyStyle = getResult(bodyStyleMenu, VehicleBodyStyle.class);
            Brand selectedBrand = getResult(brandMenu, Brand.class);
            Brand.Model selectedModel = getResult(modelMenu, Brand.Model.class);
            Fuel selectedFuel = getResult(fuelMenu, Fuel.class);
            Mileage selectedMileage = Mileage.fromString(mileageMenu.getText().toString());
            if (title.isEmpty()) vehicleTitle.setError("Input title");
            if (selectedMileage == null) mileageMenu.setError("Choose mileage");
            Integer capacity = null;
            try {
                capacity = Integer.valueOf(capacityMenu.getText().toString());
            } catch (IllegalArgumentException e) {
                capacityMenu.setError("Invalid value");
            }
            if (rentPrice.getText().toString().isEmpty()) {
                rentPrice.setError("Input price");
                return;
            }
            int price = Integer.parseInt(rentPrice.getText().toString());
            if (inputPlace == null) streetInput.setError("Input valid street");
            if (!imageUploaded) {
                Snackbar.make(carImageView, "Please upload an image of the vehicle", Snackbar.LENGTH_LONG)
                        .show();
                return;
            }
            if(selectedStartDateTextView.getText().length() != 22 || selectedEndDateTextView.getText().length() != 20){
                Snackbar.make(selectedStartDateTextView, "Please input availability", Snackbar.LENGTH_LONG)
                        .show();
                return;
            }
            String startDate = selectedStartDateTextView.getText().toString().substring(12);
            String endDate = selectedEndDateTextView.getText().toString().substring(10);

            if (selectedColor != null && selectedVehicleBodyStyle != null && selectedBrand != null
                    && selectedModel != null && selectedFuel != null && selectedMileage != null
                    && capacity != null && inputPlace != null) {


                String vehicleKey = mDatabase.push().getKey();
                Vehicle vehicle = new Vehicle(selectedBrand, selectedModel, selectedColor, selectedVehicleBodyStyle,
                        selectedFuel, selectedMileage, capacity, new MyLocation(inputPlace),
                        price, title, null, startDate, endDate, user.getUid(), vehicleKey);
                Log.d(TAG, vehicle.toString());
                getImageURL(vehicle);

//                Log.w(TAG, new MyLocation(inputPlace).address);
                // missing user
            }
        });

        //availability
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        selectedStartDateTextView = findViewById(R.id.start_date);
        selectedEndDateTextView = findViewById(R.id.end_date);
        Button showStartDatePickerButton = findViewById(R.id.show_start_date_picker);
        Button showEndDatePickerButton = findViewById(R.id.show_end_date_picker);

        showStartDatePickerButton.setOnClickListener(v -> showDatePickerDialog(startCalendar, (view, year, monthOfYear, dayOfMonth) -> {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, monthOfYear);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateSelectedStartDate();
        }));

        showEndDatePickerButton.setOnClickListener(v -> showDatePickerDialog(endCalendar, (view, year, monthOfYear, dayOfMonth) -> {
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, monthOfYear);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if (endCalendar.before(startCalendar)) {
                Toast.makeText(AddVehicleActivity.this, "End date cannot be earlier than start date", Toast.LENGTH_SHORT).show();
                endCalendar.setTimeInMillis(startCalendar.getTimeInMillis());
                updateSelectedEndDate();
            } else {
                updateSelectedEndDate();
            }
        }));


        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    // Handle the selected image URI here
                    if (result == null) return;
                    imageUploadUri = result;
                    Log.w(TAG, String.valueOf(result));
                    carImageView.setImageURI(result);
                    imageUploaded = true;
                });


        carImageView.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

    }

    public void getImageURL(Vehicle vehicle) {
        File file = new File(imageUploadUri + "-"+ new Date());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images");

        storageRef.child(file.getName()).putFile(imageUploadUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrlInDB = uri.toString();
                        Log.i(TAG, "STORED PATH: " + imageUrlInDB);

                        //callback which adds vehicle to db
                        vehicle.setCarImage(imageUrlInDB);
                        mDatabase.child(vehicle.getVehicleID()).setValue(vehicle);
                        String key = vehicleInUserDB.push().getKey();
                        vehicleInUserDB.child(key).setValue(vehicle.getVehicleID()).addOnSuccessListener(task->{
                            Log.w("HERE","add to user also");
                        });
                        Toast.makeText(AddVehicleActivity.this, "Vehicle Added Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                })
                .addOnFailureListener(e -> Log.i(TAG, "FAILED UPLOAD"));

    }






    public <T extends Enum<T>> void initDropDownMenu(AutoCompleteTextView menu, Class<T> enumType){
        T[] items = enumType.getEnumConstants();
        ArrayAdapter<T> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        menu.setAdapter(adapter);
    }

    public <T extends Enum<T>> T getResult(AutoCompleteTextView input, Class<T> enumType){
        try {
            return Enum.valueOf(enumType, input.getText().toString());
        } catch (IllegalArgumentException e) {
            input.setError("Invalid value");
            return null;
        }
    }

    private void showDatePickerDialog(Calendar calendar, DatePickerDialog.OnDateSetListener listener) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateSelectedStartDate() {
        selectedStartDateTextView.setText("Start date: " + String.format("%02d/%02d/%d",
                startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH),
                startCalendar.get(Calendar.YEAR)));
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateSelectedEndDate() {
        selectedEndDateTextView.setText("End date: " + String.format("%02d/%02d/%d",
                endCalendar.get(Calendar.MONTH) + 1, endCalendar.get(Calendar.DAY_OF_MONTH),
                endCalendar.get(Calendar.YEAR)));
    }


}
