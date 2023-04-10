package edu.northeastern.group40.Project.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.List;

import edu.northeastern.group40.Project.Models.Order;
import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.R;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyOrdersViewHolder>{
    private List<Order> orderList;
    private final Context mContext;
    private SelectListener listener;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference orderDB;
    private DatabaseReference vehicleDB;
    private boolean hasRating;
    private static final String RATE_ORDER = "Rate this order";
    private static final String TAG = "MyOrdersAdapter";


    public MyOrdersAdapter(Context mContext, List<Order> orderList, SelectListener listener) {
        this.orderList = orderList;
        this.mContext = mContext;
        this.listener = listener;
        this.vehicleDB = mDatabase.child("vehicles");
        this.orderDB = mDatabase.child("orders");

    }


    @NonNull
    @Override
    public MyOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_card, parent, false);
        return new MyOrdersViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyOrdersViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Order currOrder = orderList.get(position);
        holder.orderTime.setText("Rent date: " + currOrder.getOrderDate().toString());
        holder.orderSum.setText("Total: $ " + currOrder.getOrderPriceTotal());
        holder.orderTitle.setText(currOrder.getOrderedVehicle().getVehicleTitle());
        holder.orderTitle.setOnClickListener(v -> listener.onCarSelect(position));
        hasRating = (currOrder.getReview() != 0);
        if (hasRating)
            holder.orderRate.setText("Your rating: " + currOrder.getReview());
        else {
            holder.orderRate.setText(RATE_ORDER);
        }

    }

    @Override
    public int getItemCount() {
        return this.orderList.size();
    }

    public class MyOrdersViewHolder extends RecyclerView.ViewHolder {
        TextView orderSum, orderTime;
        CardView cardView;
        Button orderTitle, orderRate, orderDetailBtn;
        Integer newRate;
        Vehicle updatedVehicle = null;

        public MyOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            this.orderSum = itemView.findViewById(R.id.order_sum);
            this.orderTitle = itemView.findViewById(R.id.order_title);
            this.orderTime = itemView.findViewById(R.id.order_time);
            this.cardView = itemView.findViewById(R.id.order_card);
            this.orderRate = itemView.findViewById(R.id.order_rate);
            this.orderDetailBtn = itemView.findViewById(R.id.check_detail);
            if (orderRate.getText().equals(RATE_ORDER)) {
                this.orderRate.setOnClickListener(v -> openReviewDialog());
            }

            this.orderDetailBtn.setOnClickListener(v -> openDetailDialog(getLayoutPosition()));
        }

        public void saveReviewResult(int newRate, String vehicleId) {
            Log.i(TAG, "TO CHANGE VEHICLE DB");
            vehicleDB.child(vehicleId).runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    Vehicle car = currentData.getValue(Vehicle.class);

                    if (car == null)
                        return Transaction.success(currentData);

                    int reviewNum = car.getReviewTotalNumber();
                    double reviewResult = Double.parseDouble(car.getReviewResult());
                    double total = reviewNum * reviewResult;
                    car.setReviewTotalNumber(reviewNum + 1);
                    car.setReviewResult(String.valueOf((total + newRate) / (reviewNum + 1)));
                    updatedVehicle = car;
                    currentData.setValue(car);
                    Log.i(TAG, "IN CHANGE VEHICLE DB");
                    updateOrderDetails(newRate);
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed,
                                       @Nullable DataSnapshot currentData) {
                }
            });

        }

        public void updateOrderDetails(int newRate) {
            Log.i(TAG, "TO CHANGE ORDER DB");
            Order order = orderList.get(getLayoutPosition());
            order.setReview(newRate);
            order.setOrderedVehicle(updatedVehicle);
            orderDB.child(order.getOrderId()).setValue(order);

        }


        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        public void openReviewDialog() {
            View dialogView = LayoutInflater.from(mContext)
                    .inflate(R.layout.rating_dialog, null);

            RatingBar ratingBar = dialogView.findViewById(R.id.give_rating);
            ratingBar.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float touchPositionX = event.getX();
                    float width = ratingBar.getWidth();
                    float starsFloat = (touchPositionX / width) * 5.0f;
                    int stars = (int) starsFloat + 1;
                    ratingBar.setRating(stars);
                    newRate = stars;
                    saveReviewResult(newRate, orderList.get(getLayoutPosition()).getOrderedVehicle().getVehicleID());
                    v.setPressed(false);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setPressed(true);
                }

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setPressed(false);
                }

                return true;
            });

            AlertDialog reviewDialog = new MaterialAlertDialogBuilder(mContext)
                    .setTitle("Rate your overall experience")
                    .setView(dialogView)
                    .setPositiveButton(R.string.submit_rating, (dialog, which) -> {
                        orderRate.setText("Your rating: " + newRate);
                        dialog.dismiss();
                    })
                    .setCancelable(false)
                    .create();
            reviewDialog.show();

        }



        @SuppressLint("SetTextI18n")
        public void openDetailDialog(int layoutPosition) {
            Order currOrder = orderList.get(layoutPosition);
            View detailsView = LayoutInflater.from(mContext)
                    .inflate(R.layout.order_detail_dialog, null);

            final TextView orderId = detailsView.findViewById(R.id.order_id);
            final TextView orderDescription = detailsView.findViewById(R.id.order_detailed_description);
            final TextView orderSum = detailsView.findViewById(R.id.order_total_price);

            orderDescription.setText(currOrder.getOrderedVehicle().toString());
            orderId.setText("Order ID: " + currOrder.getOrderId());
            orderSum.setText("Total: $ "+ currOrder.getOrderPriceTotal() );

            AlertDialog reviewDialog = new MaterialAlertDialogBuilder(mContext)
                    .setTitle("Order Details")
                    .setView(detailsView)
                    .setCancelable(true)
                    .create();
            reviewDialog.show();


        }

    }
}
