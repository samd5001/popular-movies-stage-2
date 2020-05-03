package com.samdunkley.android.popularmovies.adapters;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class MoviePosterHolder extends RecyclerView.ViewHolder {

    ImageView posterIv;

    MoviePosterHolder(@NonNull ImageView posterIv) {
        super(posterIv);
        this.posterIv = posterIv;
    }
}
