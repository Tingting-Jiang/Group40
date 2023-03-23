package edu.northeastern.group40.Project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;


import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group40.Project.Models.Color;
import edu.northeastern.group40.R;

public class AddVehicleActivity extends AppCompatActivity {

    private static final String TAG = "AddVehicle";
    private final String API_KEY="AIzaSyAAhr6pbohtGh_mPid3btYA2XQZ9KlJ9Bs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_add_vehicle);

        AutoCompleteTextView colorMenu=findViewById(R.id.colorMenu);
        initDropDownMenu(colorMenu,Color.class);

        Places.initialize(getApplicationContext(), API_KEY);
        PlacesClient placesClient = Places.createClient(this);

        AutoCompleteTextView placeInput = findViewById(R.id.placeInput);
        List<AutocompletePrediction> predictions = new ArrayList<>();
        PlaceAutocompleteAdapter adapter = new PlaceAutocompleteAdapter(this, predictions);
        placeInput.setAdapter(adapter);
        placeInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        placeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                                .setQuery(input)
                                .setCountry("US")
                                .build();
                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    predictions.clear();
                    predictions.addAll(response.getAutocompletePredictions());
                    predictions.removeIf(prediction -> prediction.getFullText(null).toString().split(",").length != 4);
                    adapter.notifyDataSetChanged();
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public <T extends Enum<T>> void initDropDownMenu(AutoCompleteTextView menu, Class<T> enumType){
        T[] items = enumType.getEnumConstants();
        ArrayAdapter<T> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        menu.setAdapter(adapter);
    }






}
