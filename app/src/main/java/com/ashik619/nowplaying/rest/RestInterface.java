package com.ashik619.nowplaying.rest;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ashik619 on 11-03-2017.
 */
public interface RestInterface {

    @GET("movie/now_playing")
    Call<JsonObject> getNowPlaying(@Query("api_key") String api_key,@Query("language") String language,@Query("page") String page);

    @GET("configuration")
    Call<JsonObject> getConfiguration(@Query("api_key") String api_key);

    @GET("movie/{movie_id}")
    Call<JsonObject> getMovieDetails(@Path("movie_id") String movie_id,@Query("api_key") String api_key, @Query("language") String language);


}
