package com.samdunkley.android.popularmovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samdunkley.android.popularmovies.R;
import com.samdunkley.android.popularmovies.model.MovieDetails;
import com.samdunkley.android.popularmovies.model.MovieReview;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewHolder> {

    private List<MovieReview> reviewList;

    public ReviewAdapter(List<MovieReview> reviewList) {
        this.reviewList =reviewList;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View reviewView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_view, parent, false);
        return new ReviewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        MovieReview review = reviewList.get(position);

        holder.authorTv.setText(review.getAuthor());
        holder.contentTv.setText(review.getContent());

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

}