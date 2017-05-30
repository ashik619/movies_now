package com.ashik619.nowplaying.holder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashik619.nowplaying.MovieDetailActivity;
import com.ashik619.nowplaying.R;
import com.ashik619.nowplaying.models.Movie;

import java.util.ArrayList;

/**
 * Created by ashik619 on 23-05-2017.
 */
public class HomeViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener
{
    public TextView movieName;
    public ImageView posterView;
    Context context;
    ArrayList<Movie> movieArrayList;

    public HomeViewHolder(View itemView, Context context, ArrayList<Movie> movieArrayList)
    {
        super(itemView);
        this.context = context;
        this.movieArrayList = movieArrayList;
        itemView.setOnClickListener(this);
        movieName = (TextView) itemView.findViewById(R.id.nameText);
        posterView = (ImageView) itemView.findViewById(R.id.bgImage);
    }

    @Override
    public void onClick(View view)
    {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra("m_id",String.valueOf(movieArrayList.get(getAdapterPosition()).id));
        context.startActivity(intent);
    }
}