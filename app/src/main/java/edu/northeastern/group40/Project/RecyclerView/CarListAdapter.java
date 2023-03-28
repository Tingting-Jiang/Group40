package edu.northeastern.group40.Project.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.group40.Project.CarDetailActivity;
import edu.northeastern.group40.Project.CarListActivity;
import edu.northeastern.group40.Project.Models.Car;
import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.R;


public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.CarListViewHolder> {
    private List<Car> carList;
    private final Context mContext;
    private SelectListener listener;

    public CarListAdapter(Context mContext, List<Car> carList, SelectListener listener) {
        this.carList = carList;
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
        Car currCar = carList.get(position);
        holder.carTitle.setText(currCar.getCarTitle());
        holder.totalMiles.setText(currCar.getTotalMiles() + " miles");
        holder.distance.setText(currCar.getDistance() + "miles");
        holder.review_num.setText(currCar.getReview_result() + " by " + currCar.getReview_num());
        holder.rent_price.setText(currCar.getRentPrice() + " per day");
        //TODO: ADD AN IMAGE RESOURCE


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCarSelect(currCar);
            }
        });

    }

    @Override
    public int getItemCount() {
        return carList.size();
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
