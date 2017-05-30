package com.ashik619.nowplaying;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import com.ashik619.nowplaying.adapters.GenresAdapter;
import com.ashik619.nowplaying.custom_view.CustomTextView;
import com.ashik619.nowplaying.rest.HttpServerBackend;
import com.ashik619.nowplaying.rest.RestAdapter;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class MovieDetailActivity extends AppCompatActivity {
    @BindView(R.id.posterView)
    ImageView posterView;
    @BindView(R.id.movieName)
    CustomTextView movieName;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.donutProgress)
    DonutProgress donutProgress;
    @BindView(R.id.mainLayout)
    NestedScrollView mainLayout;
    @BindView(R.id.loadingLayout)
    RelativeLayout loadingLayout;
    @BindView(R.id.overViewText)
    CustomTextView overViewText;
    @BindView(R.id.donutRating)
    DonutProgress donutRating;
    @BindView(R.id.genreslistView)
    GridView genreslistView;
    @BindView(R.id.date)
    CustomTextView date;
    @BindView(R.id.commentButton)
    Button commentButton;
    private String movieId = null;
    float popularity = 0;
    float rating = 0;
    private String name = null;
    private String posterUrl = null;
    private String backDropUrl = null;
    private String overview = "   ";
    public ArrayList<String> genresList = new ArrayList<>();
    private String dateStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        final Intent intent = getIntent();
        movieId = intent.getStringExtra("m_id");
        getMovieDetailApiCall();
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MovieDetailActivity.this,ChatActivity.class);
                intent1.putExtra("m_id",movieId);
                startActivity(intent1);
            }
        });

    }

    void getMovieDetailApiCall() {
        Log.e("MAIN", "call api");
        Call<JsonObject> call = new RestAdapter().getRestInterface()
                .getMovieDetails(movieId, Constants.API_KEY, Constants.LANG);
        new HttpServerBackend(MovieDetailActivity.this).getData(call, new HttpServerBackend.ResponseListener() {
            @Override
            public void onReturn(boolean success, JsonObject data, int message) {
                super.onReturn(success, data, message);
                if (success) {
                    posterUrl = !data.get("poster_path").isJsonNull() ? data.get("poster_path").getAsString() : null;
                    backDropUrl = !data.get("backdrop_path").isJsonNull() ? data.get("backdrop_path").getAsString() : null;
                    name = data.get("original_title").getAsString();
                    overview = overview + (!data.get("overview").isJsonNull() ? data.get("overview").getAsString() : " ");
                    popularity = data.get("popularity").getAsFloat();
                    rating = data.get("vote_average").getAsFloat();
                    JsonArray genresArray = data.get("genres").getAsJsonArray();
                    for (int i = 0; i < genresArray.size(); i++) {
                        genresList.add(genresArray.get(i).getAsJsonObject().get("name").getAsString());
                    }
                    dateStr = data.get("release_date").getAsString() + " (" + data.get("status").getAsString() + ")";
                    populateData();
                } else {
                    //showSnackBar("Please Check Network Connection",true);
                }
            }
        });
    }

    void populateData() {
        loadingLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        posterView.setVisibility(View.VISIBLE);
        String imageurl = NowPlayingApplication.getLocalPrefInstance().getImageBaseUrl()
                + NowPlayingApplication.getLocalPrefInstance().getLargeImageSize() + posterUrl;
        //String imageurl = NowPlayingApplication.getLocalPrefInstance().getImageBaseUrl()
        // + "w300" + backDropUrl;
        Picasso.with(this)
                .load(imageurl)
                .into(posterView);
        movieName.setText(name);
        donutProgress.setDonut_progress(String.valueOf(Math.round(popularity)));
        donutRating.setDonut_progress(String.valueOf(Math.round(rating * 10)));
        donutRating.setText(String.valueOf(rating));
        overViewText.setText(overview);
        if (genresList.size() != 0) {
            Log.e("MOV", "size not 0");
            genreslistView.setVisibility(View.VISIBLE);
            GenresAdapter genresAdapter = new GenresAdapter(this, genresList);
            genreslistView.setAdapter(genresAdapter);
            setGridViewHeightBasedOnChildren(this, genreslistView, 3);
        }
        date.setText(dateStr);

    }

    public static void setGridViewHeightBasedOnChildren(Context context, GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if (items > columns) {
            x = items / columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + dpToPixel(context, 30);
        ;
        gridView.setLayoutParams(params);
        gridView.requestLayout();

    }

    public static int dpToPixel(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
