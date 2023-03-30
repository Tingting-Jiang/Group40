package edu.northeastern.group40.Project;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.lang.Class;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.northeastern.group40.Project.Models.Brand;
import edu.northeastern.group40.R;
import io.reactivex.rxjava3.annotations.NonNull;

public class SearchActivity extends AppCompatActivity{

    Button showStartDatePickerButton;
    TextView selectedStartDateTextView;
    Button showEndDatePickerButton;
    TextView selectedEndDateTextView;
    Calendar startCalendar, endCalendar;
    AutoCompleteTextView placeInput;
    private Place inputPlace;
    private static final String API_KEY="AIzaSyAAhr6pbohtGh_mPid3btYA2XQZ9KlJ9Bs";
    private static final String MAPS_API_KEY="AIzaSyB0xVMOtkpPW_4UwVdhLx8HFzZmRxChcrs";

//    map
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private Button mapButton;
    private Geocoder geocoder;
    private Location currentLocation;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Initialize views and calendars
        showStartDatePickerButton = findViewById(R.id.show_start_date_picker_button);
        selectedStartDateTextView = findViewById(R.id.selected_start_date_text_view);
        startCalendar = Calendar.getInstance();
        inputPlace = null;

        showEndDatePickerButton = findViewById(R.id.show_end_date_picker_button);
        selectedEndDateTextView = findViewById(R.id.selected_end_date_text_view);
        endCalendar = Calendar.getInstance();
        placeInput = findViewById(R.id.placeForSearch);


        AutoCompleteTextView brandMenu = findViewById(R.id.brandMenu);
        AutoCompleteTextView modelMenu = findViewById(R.id.modelMenu);
        AutoCompleteTextView[] menuList = new AutoCompleteTextView[]
                {brandMenu, modelMenu};
        for(AutoCompleteTextView menu: menuList){
            menu.setOnItemClickListener((parent, view, position, id) -> menu.setError(null));
        }
        initDropDownMenu(brandMenu, Brand.class);
        brandMenu.setOnItemClickListener((parent, view, position, id) -> {
            brandMenu.setError(null);
            Brand brand = (Brand) parent.getItemAtPosition(position);
            ArrayAdapter<Brand.Model> modelAdapter =
                    new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, brand.getModels());
            modelMenu.setAdapter(modelAdapter);
        });


//       ask for location permission and get the current location
        // Initialize views
        mapButton = findViewById(R.id.btnMap);
        // Initialize geocoder
        geocoder = new Geocoder(this, Locale.getDefault());
        // Set onClickListener for mapButton
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check location permission
                if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request location permission
                    ActivityCompat.requestPermissions(SearchActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_LOCATION);
                } else {
                    // Permission already granted, get current location
                    getCurrentLocation();
                }
            }
        });


        Places.initialize(getApplicationContext(), API_KEY);
        PlacesClient placesClient = Places.createClient(this);

        List<AutocompletePrediction> predictions = new ArrayList<>();
        PlaceAutocompleteAdapter placeAdapter = new PlaceAutocompleteAdapter(this, predictions);
        placeInput.setAdapter(placeAdapter);

        placeInput.setOnItemClickListener((parent, view, position, id) -> {
            placeInput.setError(null);
            AutocompletePrediction prediction = (AutocompletePrediction) parent.getItemAtPosition(position);
            String locationText = String.valueOf(prediction.getFullText(null));
            locationText = locationText.substring(0, locationText.lastIndexOf(","));
            placeInput.setText(locationText);
            FetchPlaceRequest request = FetchPlaceRequest.builder(prediction.getPlaceId(), Arrays.asList(Place.Field.ADDRESS, Place.Field.ID))
                    .build();
            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                inputPlace = response.getPlace();
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                }
            });
        });


        placeInput.addTextChangedListener(new TextWatcher() {
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
                    }
                });
            }
        });

        // Set listeners for date pickers
        showStartDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startCalendar, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startCalendar.set(Calendar.YEAR, year);
                        startCalendar.set(Calendar.MONTH, monthOfYear);
                        startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateSelectedStartDate();
                    }
                });
            }
        });

        showEndDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endCalendar, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endCalendar.set(Calendar.YEAR, year);
                        endCalendar.set(Calendar.MONTH, monthOfYear);
                        endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (endCalendar.before(startCalendar)) {
                            Toast.makeText(SearchActivity.this, "End date cannot be earlier than start date", Toast.LENGTH_SHORT).show();
                            endCalendar.setTimeInMillis(startCalendar.getTimeInMillis());
                            updateSelectedEndDate();
                        } else {
                            updateSelectedEndDate();
                        }
                    }
                });
            }
        });
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

    public <T extends Enum<T>> void initDropDownMenu(AutoCompleteTextView menu, Class<T> enumType){
        T[] items = enumType.getEnumConstants();
        ArrayAdapter<T> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        menu.setAdapter(adapter);
    }

    //        request the permission to get the user location
    private void getCurrentLocation() {
        // Check if location is enabled on the device
        if (!isLocationEnabled()) {
            Toast.makeText(SearchActivity.this, "Please enable location services", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get last known location
        if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            currentLocation = LocationUtils.getLastKnownLocation(SearchActivity.this);
        }

        // Display current address
        if (currentLocation != null) {
            try {
                List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(),
                        currentLocation.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    String address = addresses.get(0).getAddressLine(0);
                    String addressIncludeZip = address.substring(0,address.lastIndexOf(","));
                    String state = addressIncludeZip.substring(0,addressIncludeZip.lastIndexOf(" "));
                    placeInput.setText(state);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(SearchActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle location permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get current location
                getCurrentLocation();
            } else {
                // Permission denied, show message
                Toast.makeText(SearchActivity.this, "Location permission denied, you need to input the location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Check if location services are enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // Prompt user to enable location services
    private void showLocationSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public static class LocationUtils {
        // Get last known location
        public static Location getLastKnownLocation(Context context) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Location location = locationManager.getLastKnownLocation(provider);
                    if (location != null && (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy())) {
                        bestLocation = location;
                    }
                }
            }
            return bestLocation;
        }
    }
}