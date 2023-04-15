package edu.northeastern.group40.Project.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.R;


public class MyCarAdapter extends RecyclerView.Adapter<MyCarAdapter.CarListViewHolder> {
    private List<Vehicle> vehicleList;
    private SelectListener listener;

    public MyCarAdapter(List<Vehicle> vehicleList, SelectListener listener) {
        this.vehicleList = vehicleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_card, parent, false);
        return new CarListViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull CarListViewHolder holder, int position) {
        Vehicle currVehicle = vehicleList.get(position);
        holder.carTitle.setText(currVehicle.getVehicleTitle());
        holder.totalMiles.setText(currVehicle.getMileage().toString());
        holder.review_num.setText(currVehicle.getReviewResult() + " by " + currVehicle.getReviewTotalNumber() + " users");
        holder.rent_price.setText(currVehicle.getRentPrice() + " per day");
        Picasso.get()
                .load(currVehicle.getCarImage())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .fit()
                .into(holder.carImage);

        holder.cardView.setOnClickListener(v -> listener.onCarSelect(position));

    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public class CarListViewHolder extends RecyclerView.ViewHolder {
        ImageView carImage;
        TextView carTitle, totalMiles, review_num, distance, rent_price;
        CardView cardView;
        public CarListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.card_view_in_rec);
            this.carImage = itemView.findViewById(R.id.car_image);
            this.carTitle = itemView.findViewById(R.id.car_title);
            this.totalMiles = itemView.findViewById(R.id.total_miles);
            this.review_num = itemView.findViewById(R.id.review_num);
            this.distance = itemView.findViewById(R.id.distance);
            this.rent_price = itemView.findViewById(R.id.rent_price_num);

            this.distance.setVisibility(View.INVISIBLE);
        }
    }
}
