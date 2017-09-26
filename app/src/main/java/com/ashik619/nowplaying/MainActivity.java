package com.ashik619.nowplaying;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;

import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager;
import com.ashik619.nowplaying.adapters.HomeViewAdapter;
import com.ashik619.nowplaying.models.Movie;
import com.ashik619.nowplaying.rest.RestAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.splunk.mint.Mint;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Movie> moviesList;
    private ArrayList<Movie> searchList;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.loadingLayout)
    RelativeLayout loadingLayout;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;


    HomeViewAdapter rcAdapter;
    private int currentPage = 1;
    private boolean isLoadMore = false;
    private int totalPages = 0;
    private boolean isSearch = false;
    private Call<JsonObject> getMoviesCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.initAndStartSession(this.getApplication(), "5c1dbf76");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        moviesList = new ArrayList<Movie>();
        searchList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        SpannedGridLayoutManager spannedGridLayoutManager = new SpannedGridLayoutManager(
                SpannedGridLayoutManager.Orientation.VERTICAL, 3);
        //recyclerView.setLayoutManager(spannedGridLayoutManager);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        getConfiguratonaApi();
    }

    void getConfiguratonaApi() {
        Call<JsonObject> call = new RestAdapter().getRestInterface().getConfiguration(Constants.API_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    JsonObject images = response.body().get("images").getAsJsonObject();
                    NowPlayingApplication.getLocalPrefInstance().setImageBaseUrl(images.get("base_url").getAsString());
                    NowPlayingApplication.getLocalPrefInstance().setImageSize(images.get("poster_sizes").getAsJsonArray().get(1).getAsString());
                    NowPlayingApplication.getLocalPrefInstance().setLargeImageSize(images.get("poster_sizes").getAsJsonArray().get(3).getAsString());
                    initializeRecyclerView();
                }else showSnackBar();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showSnackBar();
            }
        });
    }
    void initializeRecyclerView(){
        rcAdapter = new HomeViewAdapter(
                MainActivity.this, moviesList,NowPlayingApplication.getLocalPrefInstance().getImageBaseUrl(),
                NowPlayingApplication.getLocalPrefInstance().getImageSize());
        recyclerView.setAdapter(rcAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if(!isSearch) {
                                if (!isLoadMore) {
                                    isLoadMore = true;
                                    if (currentPage <= totalPages) {
                                        getMoviesListApiCall(currentPage);
                                    }
                                }
                            }
                        }
                    }, 1000);
                }
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchDialog();
            }
        });
        getMoviesListApiCall(currentPage);
    }

    void getMoviesListApiCall(int page) {
        getMoviesCall = new RestAdapter().getRestInterface()
                .getNowPlaying(Constants.API_KEY, Constants.LANG, String.valueOf(page));
        getMoviesCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    totalPages = response.body().get("total_pages").getAsInt();
                    JsonArray results = response.body().get("results").getAsJsonArray();
                    for (int i = 0; i < results.size(); i++) {
                        Movie movie = new Movie();
                        JsonObject movieJson = results.get(i).getAsJsonObject();
                        movie.id = movieJson.get("id").getAsInt();
                        movie.name = movieJson.get("title").getAsString();
                        movie.posterUrl = !movieJson.get("poster_path").isJsonNull() ? movieJson.get("poster_path").getAsString() : null;
                        movie.popularity = movieJson.get("popularity").getAsLong();
                        moviesList.add(movie);
                    }
                    currentPage = currentPage + 1;
                    loadingLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    rcAdapter.notifyDataSetChanged();
                    isLoadMore = false;
                }else showSnackBar();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showSnackBar();
            }
        });
    }
    void showSnackBar(){
        Snackbar.make(recyclerView, "Network Error", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recreate();
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
    }
    void showSearchDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.search_dialog_bg);
        dialog.show();
        final EditText userName = (EditText) dialog.findViewById(R.id.searchText);
        userName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(userName.getText().toString());
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
    }
    void search(String text){
        isSearch = true;
        loadingLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        if(getMoviesCall.isExecuted()){
            getMoviesCall.cancel();
        }
        Call<JsonObject> call = new RestAdapter().getRestInterface()
                .search(Constants.API_KEY, Constants.LANG, "1",text);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    Log.e("SRHRSLT",response.body().toString());
                    searchList.clear();
                    if(response.body().get("total_results").getAsInt()>0) {
                        JsonArray results = response.body().get("results").getAsJsonArray();
                        for (int i = 0; i < results.size(); i++) {
                            Movie movie = new Movie();
                            JsonObject movieJson = results.get(i).getAsJsonObject();
                            movie.id = movieJson.get("id").getAsInt();
                            movie.name = movieJson.get("title").getAsString();
                            movie.posterUrl = !movieJson.get("poster_path").isJsonNull() ? movieJson.get("poster_path").getAsString() : null;
                            movie.popularity = movieJson.get("popularity").getAsLong();
                            searchList.add(movie);
                        }
                        loadingLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        rcAdapter.setMovieArrayList(searchList);
                        rcAdapter.notifyDataSetChanged();
                        Log.e("LOG","here"+searchList.size());
                    }else noMovieFound();
                }else showSnackBar();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showSnackBar();
            }
        });
    }
    void noMovieFound() {
        Snackbar.make(recyclerView, "No Movies Found", Snackbar.LENGTH_SHORT)
                .show();
        loadingLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        if(moviesList!=null) {
            rcAdapter = new HomeViewAdapter(MainActivity.this, moviesList, NowPlayingApplication.getLocalPrefInstance().getImageBaseUrl(),
                    NowPlayingApplication.getLocalPrefInstance().getImageSize());
            recyclerView.setAdapter(rcAdapter);
        }else recreate();

    }

    @Override
    public void onBackPressed() {
        if(isSearch){
            isSearch = false;
            initializeRecyclerView();
        }else {
            super.onBackPressed();
        }
    }
}