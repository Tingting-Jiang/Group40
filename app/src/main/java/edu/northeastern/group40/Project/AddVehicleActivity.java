package edu.northeastern.group40.Project;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


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
    private boolean uploadedImage;
    private Uri imageUploadUri;
    private String imageUrlInDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputPlace = null;
        uploadedImage = false;
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
            if (!uploadedImage) {
                Snackbar.make(carImageView, "Please upload an image of the vehicle", Snackbar.LENGTH_LONG)
                        .show();
                return;
            }
            Bitmap carImage = ((BitmapDrawable) carImageView.getDrawable()).getBitmap();
            if (selectedColor != null && selectedVehicleBodyStyle != null && selectedBrand != null
                    && selectedModel != null && selectedFuel != null && selectedMileage != null
                    && capacity != null && inputPlace != null) {
                Vehicle vehicle = new Vehicle(selectedBrand, selectedModel, selectedColor, selectedVehicleBodyStyle,

                        selectedFuel, selectedMileage, capacity, new MyLocation(inputPlace), price, title, imageUrlInDB);
                Log.w(TAG, new MyLocation(inputPlace).address);
                // missing user
            }
        });


        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    // Handle the selected image URI here
                    if (result == null) return;
                    imageUploadUri = result;
                    Log.w(TAG, String.valueOf(result));
                    carImageView.setImageURI(result);
                    uploadedImage = true;
                    getImageURL();
                });


        carImageView.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

    }

    public void getImageURL() {
        File file = new File(String.valueOf(imageUploadUri)+ "-"+ new Date());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images");

        storageRef.child(file.getName()).putFile(imageUploadUri)
                .addOnSuccessListener(taskSnapshot -> {

                    Toast.makeText(AddVehicleActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrlInDB = uri.toString();
                        Log.i(TAG, "STORED PATH: " + imageUrlInDB);
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


}
