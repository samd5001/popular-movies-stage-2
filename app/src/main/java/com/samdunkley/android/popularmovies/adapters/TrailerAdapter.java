package com.samdunkley.android.popularmovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samdunkley.android.popularmovies.R;
import com.samdunkley.android.popularmovies.model.MovieDetails;
import com.samdunkley.android.popularmovies.model.MovieTrailer;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerHolder> {

    private List<MovieTrailer> trailerList;

    public TrailerAdapter(List<MovieTrailer> trailerList) {
        this.trailerList = trailerList;
    }

    @NonNull
    @Override
    public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View trailerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_view, parent, false);
        return new TrailerHolder(trailerView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerHolder holder, int position) {
        MovieTrailer movieTrailer = trailerList.get(position);

        holder.nameTv.setText(movieTrailer.getName());

        Picasso.get().load(movieTrailer.getThumbnailUrl()).into(holder.thumbnailIv);
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }
}
