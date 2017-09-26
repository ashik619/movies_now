package com.ashik619.nowplaying.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashik619.nowplaying.Constants;
import com.ashik619.nowplaying.NowPlayingApplication;
import com.ashik619.nowplaying.R;
import com.ashik619.nowplaying.holder.HomeViewHolder;
import com.ashik619.nowplaying.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ashik619 on 23-05-2017.
 */
public class HomeViewAdapter extends RecyclerView.Adapter<HomeViewHolder>
{
    private ArrayList<Movie> movieArrayList;
    private Context context;
    private String imageBaseUrl;
    String imageSize;

    public HomeViewAdapter(Context context, ArrayList<Movie> itemList,String imageBaseUrl,String imageSize)
    {
        this.movieArrayList = itemList;
        this.context = context;
        this.imageBaseUrl = imageBaseUrl;
        this.imageSize = imageSize;
        this.setHasStableIds(true);
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.movie_item, null);
        HomeViewHolder rcv = new HomeViewHolder(layoutView,context,movieArrayList);
        return rcv;
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position)
    {
        holder.movieName.setText(movieArrayList.get(position).name);
        if(movieArrayList.get(position).posterUrl != null) {
            String imageurl = imageBaseUrl + imageSize + movieArrayList.get(position).posterUrl;
            Picasso.with(context)
                    .load(imageurl)
                    .into(holder.posterView);
        }
    }

    @Override
    public int getItemCount() {
        return this.movieArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setMovieArrayList(ArrayList<Movie> movieArrayList){
        this.movieArrayList = movieArrayList;
    }
}
