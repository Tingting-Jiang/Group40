package edu.northeastern.group40.Project.RecyclerView;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import edu.northeastern.group40.Project.Models.Car;
import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.R;


public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.CarListViewHolder> {
    private List<Vehicle> vehicleList;
    private final Context mContext;
    private SelectListener listener;
    private String TAG = "CarListAdapter";

    public CarListAdapter(Context mContext, List<Vehicle> vehicleList, SelectListener listener) {
        this.vehicleList = vehicleList;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_card, parent, false);
        return new CarListViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CarListViewHolder holder, int position) {
        Vehicle currVehicle = vehicleList.get(position);
        holder.carTitle.setText(currVehicle.getTitle());
        holder.totalMiles.setText(currVehicle.getMileage() + " miles");
        holder.distance.setText( "3 miles");
        holder.review_num.setText(currVehicle.getReviewResult() + " by " + currVehicle.getReviewTotalNumber());
        holder.rent_price.setText(currVehicle.getRentPrice() + " per day");
        Picasso.get()
                .load(currVehicle.getImage())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .fit()
                .into(holder.carImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCarSelect(currVehicle);
            }
        });

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

        }
    }
}
