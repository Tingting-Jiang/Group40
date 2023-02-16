package edu.northeastern.group40.A6.YelpModels;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface YelpService {
        @GET("businesses/search")
        Call<YelpSearchResult> searchRestaurants(
                @Header("Authorization") String authHeader,
                @Query("term") String searchTerm,
                @Query("location") String searchLocation);

}
