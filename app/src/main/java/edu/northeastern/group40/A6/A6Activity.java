package edu.northeastern.group40.A6;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.northeastern.group40.A6.RecyclerView.RestaurantAdapter;
import edu.northeastern.group40.A6.YelpModels.Restaurant;
import edu.northeastern.group40.A6.YelpModels.YelpSearchResult;
import edu.northeastern.group40.A6.YelpModels.YelpService;
import edu.northeastern.group40.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class A6Activity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String TAG = "A6Activity";
    private static final String API_KEY = "lycsXHp9z9AZwbfX99x-OipcWIYo_IJOtChrjwBEVPRcO5HrvVDOOTVVZ5wYKh5u3i3N4ShrT5o8ECbBG6LixqfF3yVa800kZl3aAxKwm3EH9sAao2l5vHBq2SHtY3Yx";

    private YelpService yelpService;
    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurants;
    private SearchView searchName;
    private String restaurantName;
    private TextView resultException;
    private ProgressBar progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a6);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        resultException = findViewById(R.id.resultException);
        searchName = findViewById(R.id.searchView);
        progressIndicator = findViewById(R.id.progressIndicator);
        restaurants = new ArrayList<>();

        searchName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // nothing changed
                if(query.equals(restaurantName)) return true;

                restaurants.clear();
                restaurantName = query;
                searchRestaurants(restaurantName);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // only search when submit
                return false;
            }
        });

        createRecyclerView();
        yelpService = retrofit.create(YelpService.class);
    }

    private void showRecyclerView(boolean b){
        if(b){
            resultException.setVisibility(View.GONE);
            restaurantRecyclerView.setVisibility(View.VISIBLE);
        }else{
            resultException.setVisibility(View.VISIBLE);
            restaurantRecyclerView.setVisibility(View.GONE);
        }
    }


    private void searchRestaurants(String userInput) {

        progressIndicator.setVisibility(View.VISIBLE);
        restaurantRecyclerView.setVisibility(View.GONE);
        resultException.setVisibility(View.GONE);
        yelpService.searchRestaurants("Bearer " + API_KEY, userInput, "San Jose")
                .enqueue(new Callback<YelpSearchResult>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<YelpSearchResult> call, @NonNull Response<YelpSearchResult> response) {
                        Log.i(TAG, "Success: " + response);
                        YelpSearchResult body = response.body();
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                //delay 1s on purpose to show indicator
                            }
                        }, 1000);
                        progressIndicator.setVisibility(View.GONE);
                        if (body == null) {
                            Log.w(TAG, "Did not get valid response data from Yelp");
                            resultException.setText("Service not available");
                            showRecyclerView(false);
                        } else {
                            Log.i(TAG, "Success: " + body.getRestaurants());
                            if(body.getRestaurants().isEmpty()){
                                Log.i(TAG, "no result");
                                resultException.setText("No result found");
                                showRecyclerView(false);
                            }
                            else{
                                restaurants.addAll(body.getRestaurants());
                                restaurantAdapter.notifyDataSetChanged();
                                showRecyclerView(true);
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<YelpSearchResult> call, @NonNull Throwable t) {
                        Log.i(TAG, "Failed: " + t);
                        resultException.setText("Network failed");
                        showRecyclerView(false);
                    }
                });

    }

    public void createRecyclerView(){
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        restaurantRecyclerView = findViewById(R.id.yelp_recView);
//        restaurantRecyclerView.setHasFixedSize(true);
        restaurantAdapter = new RestaurantAdapter(restaurants, this);
        restaurantRecyclerView.setAdapter(restaurantAdapter);
        restaurantRecyclerView.setLayoutManager(rLayoutManager);
        restaurantRecyclerView.addItemDecoration(
                new DividerItemDecoration(restaurantRecyclerView.getContext(),
                        DividerItemDecoration.VERTICAL));
    }
}