package edu.northeastern.group40.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import edu.northeastern.group40.R;

public class SearchActivity extends AppCompatActivity {

    Button showStartDatePickerButton;
    TextView selectedStartDateTextView;
    Button showEndDatePickerButton;
    TextView selectedEndDateTextView;
    Calendar startCalendar, endCalendar;
    private Place inputPlace;
    private static final String API_KEY="AIzaSyAAhr6pbohtGh_mPid3btYA2XQZ9KlJ9Bs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        AutoCompleteTextView placeInput = findViewById(R.id.placeForSearch);

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
}