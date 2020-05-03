package com.samdunkley.android.popularmovies.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samdunkley.android.popularmovies.R;

public class ReviewHolder extends RecyclerView.ViewHolder {

    TextView authorTv;
    TextView contentTv;

    ReviewHolder(@NonNull View view) {
            super(view);
            this.authorTv = view.findViewById(R.id.review_author_tv);
            this.contentTv = view.findViewById(R.id.review_content_tv);
        }
}
