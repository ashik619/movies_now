package com.ashik619.nowplaying;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

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
    StaggeredGridLayoutManager _sGridLayoutManager;
    HomeViewAdapter rcAdapter;
    private int currentPage = 1;
    private boolean isLoadMore = false;
    private int totalPages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        moviesList = new ArrayList<Movie>();
        //recyclerView.setHasFixedSize(true);
        _sGridLayoutManager = new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(_sGridLayoutManager);
        rcAdapter = new HomeViewAdapter(
                MainActivity.this, moviesList,NowPlayingApplication.getLocalPrefInstance().getImageBaseUrl(),
                NowPlayingApplication.getLocalPrefInstance().getImageSize());
        recyclerView.setAdapter(rcAdapter);
        recyclerView.enableLoadmore();
        recyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if(!isLoadMore){
                            isLoadMore = true;
                            if(currentPage<=totalPages) {
                                getMoviesListApiCall(currentPage);
                            }
                        }
                    }
                }, 1000);
            }
        });
        getConfiguratonaApi();
    }

    void getConfiguratonaApi() {
        Log.e("MAIN", "call api");
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
                    getMoviesListApiCall(currentPage);
                } else {
                    //showSnackBar("Please Check Network Connection",true);
                }
            }
        });
    }

    void getMoviesListApiCall(int page) {
        Log.e("MAIN", "call api");
        Call<JsonObject> call = new RestAdapter().getRestInterface()
                .getNowPlaying(Constants.API_KEY, Constants.LANG, String.valueOf(page));
        new HttpServerBackend(MainActivity.this).getData(call, new HttpServerBackend.ResponseListener() {
            @Override
            public void onReturn(boolean success, JsonObject data, int message) {
                super.onReturn(success, data, message);
                if (success) {
                    totalPages = data.get("total_pages").getAsInt();
                    JsonArray results = data.get("results").getAsJsonArray();
                    for (int i = 0; i < results.size(); i++) {
                        Movie movie = new Movie();
                        JsonObject movieJson = results.get(i).getAsJsonObject();
                        movie.id = movieJson.get("id").getAsInt();
                        movie.name = movieJson.get("title").getAsString();
                        movie.posterUrl = !movieJson.get("poster_path").isJsonNull() ? movieJson.get("poster_path").getAsString() : null;
                        //movie.posterUrl = movieJson.get("poster_path") != null ? movieJson.get("poster_path").getAsString() : null;
                        movie.popularity = movieJson.get("popularity").getAsLong();
                        moviesList.add(movie);
                    }
                    currentPage = currentPage+1;
                    isLoadMore = false;
                    loadingLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    rcAdapter.notifyDataSetChanged();
                } else {
                    //showSnackBar("Please Check Network Connection",true);
                }
            }
        });
    }
}
