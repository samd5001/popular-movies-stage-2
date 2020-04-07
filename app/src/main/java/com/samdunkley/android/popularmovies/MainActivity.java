package com.samdunkley.android.popularmovies;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.samdunkley.android.popularmovies.background.GetMovieDetailsListTask;
import com.samdunkley.android.popularmovies.model.MovieDetails;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static String MOVIES_STATE_KEY = "movie_details";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MovieAdapter movieAdapter;
    private ArrayList<MovieDetails> movieDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIES_STATE_KEY)) {
            movieDetailsList = new ArrayList<>();
        } else {
            movieDetailsList = savedInstanceState.getParcelableArrayList(MOVIES_STATE_KEY);
        }

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.movie_rv);

        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter(movieDetailsList);
        recyclerView.setAdapter(movieAdapter);

    }

    @Override
    protected void onStart() {
        getAndSetMovies();
        super.onStart();
    }

    private void getAndSetMovies() {
        GetMovieDetailsListTask movieTask = new GetMovieDetailsListTask("popular", this.movieAdapter, this.movieDetailsList);
        movieTask.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_STATE_KEY, movieDetailsList);
        super.onSaveInstanceState(outState);
    }
}
