package edu.northeastern.group40.A6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a6);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restaurants=new ArrayList<>();
        createRecyclerView();

        yelpService = retrofit.create(YelpService.class);

        yelpService.searchRestaurants("Bearer " + API_KEY, "Peet's coffee", "San Jose")
                .enqueue(new Callback<YelpSearchResult>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<YelpSearchResult> call, Response<YelpSearchResult> response) {
                        YelpSearchResult body = response.body();

                        if (body == null) {
                            Log.w(TAG, "Did not get valid response data from Yelp");
                            return;
                        } else {
                            Log.i(TAG, "Success: " + body.getRestaurants());
                            restaurantAdapter.setRestaurants(body.getRestaurants());
                            restaurantAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<YelpSearchResult> call, Throwable t) {
                        Log.i(TAG, "Failed: " + t);

                    }
                });
    }



    private void searchRestaurants(String userInput) {
        yelpService.searchRestaurants("Bearer " + API_KEY, userInput, "New York")
                .enqueue(new Callback<YelpSearchResult>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<YelpSearchResult> call, Response<YelpSearchResult> response) {
                        Log.i(TAG, "Success: " + response);
                        YelpSearchResult body = response.body();

                        if (body == null) {
                            Log.w(TAG, "Did not get valid response data from Yelp");
                            return;
                        } else {
                            Log.i(TAG, "Success: " + body.getRestaurants());

                        }
                    }

                    @Override
                    public void onFailure(Call<YelpSearchResult> call, Throwable t) {
                        Log.i(TAG, "Failed: " + t);

                    }
                });

    }

    public void createRecyclerView(){
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        restaurantRecyclerView = findViewById(R.id.yelp_recView);
        restaurantRecyclerView.setHasFixedSize(true);
        restaurantAdapter = new RestaurantAdapter(restaurants, this);
        restaurantRecyclerView.setAdapter(restaurantAdapter);
        restaurantRecyclerView.setLayoutManager(rLayoutManager);
    }
}