package com.samdunkley.android.popularmovies.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samdunkley.android.popularmovies.R;

public class TrailerHolder extends RecyclerView.ViewHolder {

    public ImageView thumbnailIv;
    public TextView nameTv;


    TrailerHolder(@NonNull View trailerView) {
        super(trailerView);
        this.thumbnailIv = trailerView.findViewById(R.id.trailer_thumbnail);
        this.nameTv=trailerView.findViewById(R.id.trailer_name);
    }
}
