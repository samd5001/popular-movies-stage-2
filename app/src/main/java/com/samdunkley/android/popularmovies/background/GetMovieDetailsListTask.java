package com.samdunkley.android.popularmovies.background;

import android.content.Context;
import android.os.AsyncTask;

import com.samdunkley.android.popularmovies.adapters.MovieAdapter;
import com.samdunkley.android.popularmovies.model.MovieDetails;
import com.samdunkley.android.popularmovies.utils.ApiUtils;

import java.util.ArrayList;
import java.util.List;

public class GetMovieDetailsListTask extends AsyncTask<String, Void, List<MovieDetails>> {

    private String sortOrder;
    private MovieAdapter movieAdapter;
    private ArrayList<MovieDetails> movieDetailsList;
    private Context context;

    public GetMovieDetailsListTask(String sortOrder, MovieAdapter movieAdapter, ArrayList<MovieDetails> movieDetailsList, Context context) {
        this.movieAdapter = movieAdapter;
        this.sortOrder = sortOrder;
        this.movieDetailsList = movieDetailsList;
        this.context = context;
    }

    @Override
    protected List<MovieDetails> doInBackground(String... strings) {

        return ApiUtils.fetchMovies(this.sortOrder, this.context);
    }

    @Override
    protected void onPostExecute(List<MovieDetails> result) {
        this.movieDetailsList.clear();
        this.movieDetailsList.addAll(result);
        this.movieAdapter.notifyDataSetChanged();

    }
}
