package edu.northeastern.group40.A6.RecyclerView;


import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.group40.A6.YelpModels.Category;
import edu.northeastern.group40.A6.YelpModels.Restaurant;
import edu.northeastern.group40.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    private final ImageView imageView;
    private final TextView restaurant_name;
    private final RatingBar ratingBar;
    private final TextView tvNumReviews;
    private final TextView tvAddress;
    private final TextView tvCatogory;
    private final TextView tvPrice;
    private final String noImage;
    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageView);
        restaurant_name=itemView.findViewById(R.id.restaurant_name);
        ratingBar=itemView.findViewById(R.id.ratingBar);
        tvNumReviews=itemView.findViewById(R.id.tvNumReviews);
        tvAddress=itemView.findViewById(R.id.tvAddress);
        tvCatogory=itemView.findViewById(R.id.tvCatgory);
        tvPrice=itemView.findViewById(R.id.tvPrice);
        noImage="https://demofree.sirv.com/nope-not-here.jpg";
    }

    public String categoriesToString(List<Category> categories){
        StringBuilder sb=new StringBuilder();
        for(Category category: categories){
            sb.append(category.getTitle());
            sb.append(" & ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    public void bindThisData(Restaurant restaurantToBind){
        try{
            Picasso.get()
                .load(restaurantToBind.getImageUrl())
                .fit()
                .into(imageView);
        }catch (Exception e){
            Picasso.get()
                    .load(noImage)
                    .fit()
                    .into(imageView);
        }
        restaurant_name.setText(restaurantToBind.getName());
        ratingBar.setIsIndicator(true);
        ratingBar.setRating(restaurantToBind.getRating().floatValue());
        tvNumReviews.setText(restaurantToBind.getReviewCount().toString()+" Reviews");
        tvAddress.setText(restaurantToBind.getLocation().getAddress());
        tvCatogory.setText(categoriesToString(restaurantToBind.getCategories()));
        tvPrice.setText(restaurantToBind.getPrice());
    }
}
