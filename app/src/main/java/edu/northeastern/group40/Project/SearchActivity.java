package edu.northeastern.group40.Project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.northeastern.group40.Project.Models.AvailableDate;
import edu.northeastern.group40.Project.Models.Brand;
import edu.northeastern.group40.Project.Models.MyLocation;
import edu.northeastern.group40.R;

public class SearchActivity extends AppCompatActivity {

    Button showStartDatePickerButton;
    TextView selectedStartDateTextView;
    Button showEndDatePickerButton;
    TextView selectedEndDateTextView;
    Calendar startCalendar, endCalendar;
    AutoCompleteTextView placeInput;
    private Place inputPlace;
    private static final String API_KEY = "AIzaSyAAhr6pbohtGh_mPid3btYA2XQZ9KlJ9Bs";
//    private static final String MAPS_API_KEY = "AIzaSyB0xVMOtkpPW_4UwVdhLx8HFzZmRxChcrs";

    private LocationManager locationManager;
    private static final String enableGPS = "Enable GPS to get your Location";
    private static final int locationRequestCode = 1;
    private final AvailableDate targetDate = new AvailableDate();
    private static final String TAG = "SearchActivity";

    // TODO: save the brand and model, and location to these variables
    private Brand targetBrand;
    private Brand.Model targetModel;
    // the location contains the latitude and longitude
    private MyLocation targetLocation;
    private Button submit;



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
        submit = findViewById(R.id.btnSearch);


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
            FetchPlaceRequest request = FetchPlaceRequest.builder(prediction.getPlaceId(), Arrays.asList(Place.Field.ADDRESS, Place.Field.ID, Place.Field.LAT_LNG))
                    .build();
            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                inputPlace = response.getPlace();
                targetLocation = new MyLocation(inputPlace);
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
                        Log.e(TAG,"Exception: " + apiException );
                    }
                });
            }
        });

        // Set listeners for date pickers
        showStartDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startCalendar, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startCalendar.set(Calendar.YEAR, year);
                        startCalendar.set(Calendar.MONTH, monthOfYear);
                        startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String startDate = extractDateFromCalendar(startCalendar);
                        targetDate.setStartDate(startDate);
                        selectedStartDateTextView.setText("Start date: " + startDate);
                    }
                });
            }
        });

        showEndDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endCalendar, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endCalendar.set(Calendar.YEAR, year);
                        endCalendar.set(Calendar.MONTH, monthOfYear);
                        endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (endCalendar.before(startCalendar)) {
                            Toast.makeText(SearchActivity.this, "End date cannot be earlier than start date", Toast.LENGTH_SHORT).show();
                            endCalendar.setTimeInMillis(startCalendar.getTimeInMillis());
                            String endDate = extractDateFromCalendar(endCalendar);
                            targetDate.setEndDate(endDate);
                            selectedEndDateTextView.setText("End date: " + endDate);

                        } else {
                            String endDate = extractDateFromCalendar(endCalendar);
                            targetDate.setEndDate(endDate);
                            selectedEndDateTextView.setText("End date: " + endDate);
                        }
                    }
                });
            }
        });

        submit.setOnClickListener(v -> {
            targetBrand = getResult(brandMenu, Brand.class);
            targetModel = getResult(modelMenu, Brand.Model.class);
            if(targetDate == null) {
                Toast.makeText(SearchActivity.this, "Please choose availability", Toast.LENGTH_SHORT).show();
                return;
            }
            if(targetLocation == null) {
                Toast.makeText(SearchActivity.this, "Please choose target location", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(SearchActivity.this, CarListActivity.class);
            if(targetModel!=null){
                intent.putExtra("VehicleModel", targetModel.toString());
            }
            if(targetBrand!=null){
                intent.putExtra("VehicleBrand", targetBrand.toString());
            }
            intent.putExtra("AvailableDate", targetDate);
            intent.putExtra("destinationLocation", targetLocation);
            startActivity(intent);
        });

    }

    private void showDatePickerDialog(Calendar calendar, DatePickerDialog.OnDateSetListener listener) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
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

    @SuppressLint("DefaultLocale")
    private String extractDateFromCalendar(Calendar calendar) {
        return String.format("%02d/%02d/%d",
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR));
    }


    //  click the locate button
    public void getTheLocation(View view) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isProviderEnabled) {
            Snackbar.make(view, enableGPS, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            getUserLocation();
        }
    }

    //  request the permission to get the user location
    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(SearchActivity.this, R.string.location_access_denied, Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
        } else if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(SearchActivity.this, R.string.location_access_denied, Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, locationRequestCode);
        } else {
            Location currentGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location currentNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location currentPassiveLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (currentGPS != null) {
                locationToAdd(currentGPS);
                try {
                    targetLocation = new MyLocation(currentGPS.getLatitude(), currentGPS.getLongitude(),this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (currentNetworkLocation != null) {
                locationToAdd(currentNetworkLocation);
                try {
                    targetLocation = new MyLocation(currentNetworkLocation.getLatitude(), currentNetworkLocation.getLongitude(),this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (currentPassiveLocation != null) {
                locationToAdd(currentPassiveLocation);
                try {
                    targetLocation = new MyLocation(currentPassiveLocation.getLatitude(), currentPassiveLocation.getLongitude(),this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(SearchActivity.this, "Please input the address", Toast.LENGTH_SHORT).show();
            }
        }
    }

//      Change the location to address
    private void locationToAdd(Location location){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        if(location != null){
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    String address = addresses.get(0).getAddressLine(0);
                    String addressIncludeZip = address.substring(0,address.lastIndexOf(","));
                    String addressIncludeState = addressIncludeZip.substring(0,addressIncludeZip.lastIndexOf(" "));
                    placeInput.setText(addressIncludeState);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}