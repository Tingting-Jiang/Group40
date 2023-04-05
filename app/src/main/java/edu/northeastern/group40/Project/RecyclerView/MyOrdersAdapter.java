package edu.northeastern.group40.Project.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.List;

import edu.northeastern.group40.Project.CarDetailActivity;
import edu.northeastern.group40.Project.CarListActivity;
import edu.northeastern.group40.Project.Models.Order;
import edu.northeastern.group40.Project.Models.SelectListener;
import edu.northeastern.group40.Project.Models.Vehicle;
import edu.northeastern.group40.Project.SearchActivity;
import edu.northeastern.group40.R;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyOrdersViewHolder>{
    private List<Order> orderList;
    private final Context mContext;
    private SelectListener listener;


    public MyOrdersAdapter(Context mContext, List<Order> orderList, SelectListener listener) {
        this.orderList = orderList;
        this.mContext = mContext;
        this.listener = listener;

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
        holder.orderTitle.setText(currOrder.getOrderedVehicle().getTitle());
        holder.orderTitle.setOnClickListener(v -> listener.onCarSelect(position));

    }

    @Override
    public int getItemCount() {
        return this.orderList.size();
    }

    public class MyOrdersViewHolder extends RecyclerView.ViewHolder {
        TextView  orderSum, orderTime;
        CardView cardView;
        Button orderTitle, orderRate, orderDetailBtn;
        Integer newRate;
//        MaterialButton orderAgainBtn;
        public MyOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            this.orderSum = itemView.findViewById(R.id.order_sum);
            this.orderTitle = itemView.findViewById(R.id.order_title);
            this.orderTime = itemView.findViewById(R.id.order_time);
            this.cardView = itemView.findViewById(R.id.order_card);
            this.orderRate = itemView.findViewById(R.id.order_rate);
            this.orderDetailBtn = itemView.findViewById(R.id.check_detail);
//            this.orderAgainBtn = itemView.findViewById(R.id.reorder);

            this.orderRate.setOnClickListener(v -> openReviewDialog());
            this.orderDetailBtn.setOnClickListener(v -> openDetailDialog(getLayoutPosition()));
//            this.orderAgainBtn.setOnClickListener((View.OnClickListener) v -> openDateSelector(getLayoutPosition()));
        }


        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        public void openReviewDialog() {
            View dialogView = LayoutInflater.from(mContext)
                    .inflate(R.layout.rating_dialog, null);

            RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.give_rating);
            ratingBar.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float touchPositionX = event.getX();
                    float width = ratingBar.getWidth();
                    float starsFloat = (touchPositionX / width) * 5.0f;
                    int stars = (int)starsFloat + 1;
                    ratingBar.setRating(stars);
                    newRate = stars;
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
                            this.orderRate.setText("Your rating: " + newRate);
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
