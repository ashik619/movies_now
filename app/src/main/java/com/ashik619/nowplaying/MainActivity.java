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
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashik619.nowplaying.adapters.HomeViewAdapter;
import com.ashik619.nowplaying.models.Movie;
import com.ashik619.nowplaying.rest.HttpServerBackend;
import com.ashik619.nowplaying.rest.RestAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    ArrayList<Movie> moviesList;
    @BindView(R.id.recycler_view)
    UltimateRecyclerView recyclerView;
    @BindView(R.id.loadingLayout)
    RelativeLayout loadingLayout;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    StaggeredGridLayoutManager _sGridLayoutManager;
    HomeViewAdapter rcAdapter;
    private int currentPage = 1;
    private boolean isLoadMore = false;
    private int totalPages = 0;
    private boolean isSearch = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        moviesList = new ArrayList<Movie>();
        recyclerView.setHasFixedSize(true);
        _sGridLayoutManager = new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(_sGridLayoutManager);
        recyclerView.enableLoadmore();
        recyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
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
        });
        getConfiguratonaApi();
    }

    void getConfiguratonaApi() {
        Call<JsonObject> call = new RestAdapter().getRestInterface().getConfiguration(Constants.API_KEY);
        new HttpServerBackend(MainActivity.this).getData(call, new HttpServerBackend.ResponseListener() {
            @Override
            public void onReturn(boolean success, JsonObject data, int message) {
                super.onReturn(success, data, message);
                if (success) {
                    JsonObject images = data.get("images").getAsJsonObject();
                    NowPlayingApplication.getLocalPrefInstance().setImageBaseUrl(images.get("base_url").getAsString());
                    NowPlayingApplication.getLocalPrefInstance().setImageSize(images.get("poster_sizes").getAsJsonArray().get(1).getAsString());
                    NowPlayingApplication.getLocalPrefInstance().setLargeImageSize(images.get("poster_sizes").getAsJsonArray().get(3).getAsString());
                    initializeRecyclerView();
                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showSearchDialog();
                        }
                    });
                    getMoviesListApiCall(currentPage);
                } else {
                    showSnackBar();
                }
            }
        });
    }
    void initializeRecyclerView(){
        rcAdapter = new HomeViewAdapter(
                MainActivity.this, moviesList,NowPlayingApplication.getLocalPrefInstance().getImageBaseUrl(),
                NowPlayingApplication.getLocalPrefInstance().getImageSize());
        recyclerView.setAdapter(rcAdapter);
    }

    void getMoviesListApiCall(int page) {
        Log.e("MAIN", "call api");
        Call<JsonObject> call = new RestAdapter().getRestInterface()
                .getNowPlaying(Constants.API_KEY, Constants.LANG, String.valueOf(page));
        new HttpServerBackend(MainActivity.this).getData(call, new HttpServerBackend.ResponseListener() {
            @Override
            public void onReturn(boolean success, JsonObject data, int message) {
                super.onReturn(success, data, message);
                if(!isSearch) {
                    if (success) {
                        totalPages = data.get("total_pages").getAsInt();
                        JsonArray results = data.get("results").getAsJsonArray();
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
                        isLoadMore = false;
                        loadingLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        rcAdapter.notifyDataSetChanged();
                    } else {
                        showSnackBar();
                    }
                }
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
        Log.e("SEARCH",text);
        isSearch = true;
        loadingLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        Call<JsonObject> call = new RestAdapter().getRestInterface()
                .search(Constants.API_KEY, Constants.LANG, "1",text);
        new HttpServerBackend(MainActivity.this).getData(call, new HttpServerBackend.ResponseListener() {
            @Override
            public void onReturn(boolean success, JsonObject data, int message) {
                super.onReturn(success, data, message);
                if (success) {
                    Log.e("SEARCH","succ");
                    if(data.get("total_results").getAsInt()>0) {
                        JsonArray results = data.get("results").getAsJsonArray();
                        ArrayList<Movie> searchList = new ArrayList<Movie>();
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
                        rcAdapter = new HomeViewAdapter(MainActivity.this, searchList, NowPlayingApplication.getLocalPrefInstance().getImageBaseUrl(),
                                NowPlayingApplication.getLocalPrefInstance().getImageSize());
                        recyclerView.setAdapter(rcAdapter);
                    }else noMovieFound();
                } else {
                    showSnackBar();
                }
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
        if(isLoadMore){
            isLoadMore = false;
            recreate();
        }
        super.onBackPressed();
    }
}