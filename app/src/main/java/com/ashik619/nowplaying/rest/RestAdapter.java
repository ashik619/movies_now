package com.ashik619.nowplaying.rest;

import com.ashik619.nowplaying.Constants;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ashik619 on 11-03-2017.
 */
public class RestAdapter {
    private String BASE_URL = Constants.API_BASE_URL;


    private Retrofit restAdapter;
    private RestInterface restInterface;

    public RestAdapter() {
    }
    public RestInterface getRestInterface() {

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(40, TimeUnit.SECONDS)
                .connectTimeout(40, TimeUnit.SECONDS)
                .build();

        restAdapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(
                        GsonConverterFactory.create(new GsonBuilder()
                                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                                .serializeNulls()
                                .create()))
                .build();
        restInterface = restAdapter.create(RestInterface.class);
        return restInterface;
    }

}
