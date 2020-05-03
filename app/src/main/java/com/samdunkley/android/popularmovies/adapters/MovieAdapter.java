package com.samdunkley.android.popularmovies;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samdunkley.android.popularmovies.model.MovieDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MoviePosterHolder> {

    private List<MovieDetails> movieData;

    MovieAdapter(List<MovieDetails> movieData) {
        this.movieData = movieData;
    }

    @NonNull
    @Override
    public MoviePosterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView posterView = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_view, parent, false);
        return new MoviePosterHolder(posterView);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviePosterHolder holder, int position) {
        MovieDetails movieDetails = movieData.get(position);
        ImageView posterIv = holder.posterIv;

        Picasso.get().load(movieDetails.getPosterUrl()).fit().into(posterIv);
    }

    @Override
    public int getItemCount() {
        return movieData.size();
    }

}
