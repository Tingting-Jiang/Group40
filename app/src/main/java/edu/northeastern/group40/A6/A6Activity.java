package edu.northeastern.group40.A6;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.northeastern.group40.A6.RecyclerView.RestaurantAdapter;
import edu.northeastern.group40.A6.YelpModels.Restaurant;
import edu.northeastern.group40.A6.YelpModels.YelpSearchResult;
import edu.northeastern.group40.A6.YelpModels.YelpService;
import edu.northeastern.group40.R;
//import retrofit2.Call;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a6);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchName = findViewById(R.id.searchView);
        restaurants = new ArrayList<>();

        searchName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                restaurants.clear();
                restaurantName = query;
                searchRestaurants(restaurantName);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!Objects.equals(newText, restaurantName)) {
                    restaurants.clear();
                    searchRestaurants(restaurantName);
                    return true;
                }
                return false;
            }
        });


        createRecyclerView();

        yelpService = retrofit.create(YelpService.class);
    }


    private void searchRestaurants(String userInput) {
        yelpService.searchRestaurants("Bearer " + API_KEY, userInput, "New York")
                .enqueue(new Callback<YelpSearchResult>() {
                    @Override
                    public void onResponse(@NonNull retrofit2.Call<YelpSearchResult> call, Response<YelpSearchResult> response) {
                        Log.i(TAG, "Success: " + response);

                        YelpSearchResult body = response.body();

                        if (body == null) {
                            Log.w(TAG, "Did not get valid response data from Yelp");
                        } else {
                            Log.i(TAG, "Success: " + body.getRestaurants());
                            restaurants.addAll(body.getRestaurants());
                            restaurantAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onFailure(@NonNull retrofit2.Call<YelpSearchResult> call, @NonNull Throwable t) {
                        Log.i(TAG, "Failed: " + t);
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    public void onResponse(Call call, Response<YelpSearchResult> response) {
                        Log.i(TAG, "Success: " + response);

                        YelpSearchResult body = response.body();

                        if (body == null) {
                            Log.w(TAG, "Did not get valid response data from Yelp");
                        } else {
                            Log.i(TAG, "Success: " + body.getRestaurants());
                            restaurants.addAll(body.getRestaurants());
                            restaurantAdapter.notifyDataSetChanged();

                        }
                    }


                    public void onFailure(Call call, Throwable t) {
                        Log.i(TAG, "Failed: " + t);

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
    }
}