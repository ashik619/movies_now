package com.ashik619.nowplaying.adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ashik619.nowplaying.R;
import com.ashik619.nowplaying.custom_view.CustomTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ashik619 on 25-05-2017.
 */
public class GenresAdapter extends ArrayAdapter<String> {
    Context context;

    public GenresAdapter(Context context, ArrayList<String> list) {
        super(context, 0, list);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.genres_list_item, parent, false);
        }
        CustomTextView textView = (CustomTextView) convertView.findViewById(R.id.textView);
        Log.e("ADAP","setting");
        textView.setText(item);
        return convertView;
    }
}
