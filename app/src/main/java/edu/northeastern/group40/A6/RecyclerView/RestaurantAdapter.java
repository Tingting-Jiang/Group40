package edu.northeastern.group40.A6.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.group40.A6.YelpModels.Restaurant;
import edu.northeastern.group40.R;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private List<Restaurant> restaurants;
    private final Context context;

    public RestaurantAdapter(List<Restaurant> restaurants, Context context) {
        this.restaurants = restaurants;
        this.context=context;
    }

    public void setRestaurants(List<Restaurant> restaurants){
        this.restaurants =restaurants;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.bindThisData(restaurants.get(position));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }
}
