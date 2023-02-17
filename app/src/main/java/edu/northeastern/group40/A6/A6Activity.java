package edu.northeastern.group40.A6;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class A6Activity extends AppCompatActivity implements android.widget.SearchView.OnQueryTextListener, SearchView.OnQueryTextListener {

    private static final String BASE_URL = "https://api.yelp.com/v3/";
    private static final String TAG = "A6Activity";
    private static final String API_KEY = "lycsXHp9z9AZwbfX99x-OipcWIYo_IJOtChrjwBEVPRcO5HrvVDOOTVVZ5wYKh5u3i3N4ShrT5o8ECbBG6LixqfF3yVa800kZl3aAxKwm3EH9sAao2l5vHBq2SHtY3Yx";

    private YelpService yelpService;
    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurants;
    private SearchView searchName;
    private String restaurantName;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a6);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchName = findViewById(R.id.searchView);
        mProgressBar = findViewById(R.id.progressBar);
        restaurants = new ArrayList<>();
        searchName.setOnQueryTextListener(this);

        createRecyclerView();

        yelpService = retrofit.create(YelpService.class);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Show the progress indicator
        mProgressBar.setVisibility(View.VISIBLE);
        new SearchTask().execute(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private class SearchTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            // Perform the search in the background thread
            String queryString = params[0];
            restaurants.clear();
            restaurantName = queryString;

            // Simulate a long search operation by sleeping for 2 seconds
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Hide the progress indicator when the search is complete
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
            searchRestaurants(restaurantName);
            return null;
        }
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
                            restaurants.addAll(body.getRestaurants());
                            restaurantAdapter.notifyDataSetChanged();

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