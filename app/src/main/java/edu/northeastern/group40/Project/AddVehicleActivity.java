package edu.northeastern.group40.Project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import edu.northeastern.group40.Project.Models.Brand;
import edu.northeastern.group40.Project.Models.Color;
import edu.northeastern.group40.Project.Models.Fuel;
import edu.northeastern.group40.Project.Models.Mileage;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.Project.Models.VehicleBodyStyle;
import edu.northeastern.group40.R;

public class AddVehicleActivity extends AppCompatActivity {

    private static final String TAG = "AddVehicle";
    private final String API_KEY="AIzaSyAAhr6pbohtGh_mPid3btYA2XQZ9KlJ9Bs";
    private List<Place.Field> placeFields;
    private Place inputPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputPlace = null;
        setContentView(R.layout.activity_project_add_vehicle);
        placeFields = Arrays.asList(Place.Field.ADDRESS, Place.Field.ID);
        AutoCompleteTextView colorMenu = findViewById(R.id.colorMenu);
        AutoCompleteTextView bodyStyleMenu = findViewById(R.id.bodyStyleMenu);
        AutoCompleteTextView brandMenu = findViewById(R.id.brandMenu);
        AutoCompleteTextView modelMenu = findViewById(R.id.modelMenu);
        AutoCompleteTextView fuelMenu = findViewById(R.id.fuelMenu);
        AutoCompleteTextView mileageMenu = findViewById(R.id.mileageMenu);
        AutoCompleteTextView capacityMenu = findViewById(R.id.capacityMenu);
        AutoCompleteTextView[] menuList = new AutoCompleteTextView[]
                {colorMenu, bodyStyleMenu, brandMenu, modelMenu, fuelMenu, mileageMenu, capacityMenu};
        for(AutoCompleteTextView menu: menuList){
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
        capacityMenu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(1,2,3,4,5,6)));

        Places.initialize(getApplicationContext(), API_KEY);
        PlacesClient placesClient = Places.createClient(this);

        AutoCompleteTextView streetInput = findViewById(R.id.placeInput);
        AutoCompleteTextView cityInput = findViewById(R.id.city);
        AutoCompleteTextView stateInput = findViewById(R.id.state);
        AutoCompleteTextView zipCodeInput = findViewById(R.id.zip_code);

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
                String[] parts = address.split(", ");
                int len = parts.length;
                String stateCode = parts[len-2];
                String[] stateAndCode = stateCode.split(" ");
                cityInput.setText(parts[len-3]);
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

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Color selectedColor = getResult(colorMenu, Color.class);
                VehicleBodyStyle selectedVehicleBodyStyle = getResult(bodyStyleMenu, VehicleBodyStyle.class);
                Brand selectedBrand = getResult(brandMenu, Brand.class);
                Brand.Model selectedModel = getResult(modelMenu, Brand.Model.class);
                Fuel selectedFuel = getResult(fuelMenu, Fuel.class);
                Mileage selectedMileage = Mileage.fromString(mileageMenu.getText().toString());
                if(selectedMileage == null) mileageMenu.setError("Choose mileage");
                Integer capacity = null;
                try {
                    capacity=Integer.valueOf(capacityMenu.getText().toString());
                } catch (IllegalArgumentException e) {
                    capacityMenu.setError("Invalid value");
                }
                if(inputPlace == null) streetInput.setError("Input valid street");
                if(selectedColor != null && selectedVehicleBodyStyle != null && selectedBrand != null
                    && selectedModel != null && selectedFuel != null && selectedMileage != null
                    && capacity != null && inputPlace != null){
                    Vehicle vehicle = new Vehicle(selectedBrand, selectedModel, selectedColor, selectedVehicleBodyStyle,
                            selectedFuel, selectedMileage, capacity, inputPlace);
                    Log.w(TAG,vehicle.toString());
                    // missing user
                }
            }
        });

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
