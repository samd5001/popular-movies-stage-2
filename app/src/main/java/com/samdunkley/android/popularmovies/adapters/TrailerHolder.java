package com.samdunkley.android.popularmovies.adapters;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewHolder extends RecyclerView.ViewHolder {

    View posterIv;

    ReviewHolder(@NonNull ImageView posterIv) {
            super(posterIv);
            this.posterIv = posterIv;
        }
}
