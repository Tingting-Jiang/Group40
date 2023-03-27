package edu.northeastern.group40.A6.RecyclerView;

import android.annotation.SuppressLint;
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
    private final TextView tvCategory;
    private final TextView tvPrice;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.car_image);
        restaurant_name=itemView.findViewById(R.id.car_title);
        ratingBar=itemView.findViewById(R.id.ratingBar);
        tvNumReviews=itemView.findViewById(R.id.total_miles);
        tvAddress=itemView.findViewById(R.id.review_num);
        tvCategory=itemView.findViewById(R.id.tvCategory);
        tvPrice=itemView.findViewById(R.id.tvPrice);
    }

    public String categoriesToString(List<Category> categories){
        StringBuilder sb=new StringBuilder();
        if (categories.size() > 2)
            categories = categories.subList(0 ,2);

        for(Category category: categories){
            sb.append(category.getTitle());
            sb.append(" & ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    @SuppressLint("SetTextI18n")
    public void bindThisData(Restaurant restaurantToBind){
        Picasso.get()
                .load(restaurantToBind.getImageUrl())
                .fit()
                .into(imageView);
        restaurant_name.setText(restaurantToBind.getName());
        ratingBar.setIsIndicator(true);
        ratingBar.setRating(restaurantToBind.getRating().floatValue());
        tvNumReviews.setText(restaurantToBind.getReviewCount().toString()+" Reviews");
        tvAddress.setText(restaurantToBind.getLocation().getAddress());
        tvCategory.setText(categoriesToString(restaurantToBind.getCategories()));
        tvPrice.setText(restaurantToBind.getPrice());
    }
}
