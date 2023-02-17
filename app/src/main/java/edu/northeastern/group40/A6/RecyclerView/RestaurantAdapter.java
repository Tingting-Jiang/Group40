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

    private List<Restaurant> resraurants;
    private final Context context;

    public RestaurantAdapter(List<Restaurant> resraurants, Context context) {
        this.resraurants = resraurants;
        this.context=context;
    }

    public void setRestaurants(List<Restaurant> restaurants){
        this.resraurants=restaurants;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.bindThisData(resraurants.get(position));
    }

    @Override
    public int getItemCount() {
        return resraurants.size();
    }
}
