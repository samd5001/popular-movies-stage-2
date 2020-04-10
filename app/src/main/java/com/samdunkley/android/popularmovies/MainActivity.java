package com.samdunkley.android.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.samdunkley.android.popularmovies.background.GetMovieDetailsListTask;
import com.samdunkley.android.popularmovies.model.MovieDetails;
import com.samdunkley.android.popularmovies.utils.ApiUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static String MOVIES_STATE_KEY = "movie_details";
    private static String SORT_PREFERENCE_KEY = "sortOrder";
    private static String POPULAR_API_PATH = "popular";
    private static String TOP_RATED_API_PATH = "top_rated";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MovieAdapter movieAdapter;
    private ArrayList<MovieDetails> movieDetailsList;
    private SharedPreferences prefs ;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_STATE_KEY, movieDetailsList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem sortOrder = menu.findItem(R.id.pref_sort_order);

        if (getSortPathFromPreferences() == POPULAR_API_PATH) {
            sortOrder.setTitle(R.string.sort_order_popular);
        } else {
            sortOrder.setTitle(R.string.sort_order_top_rated);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pref_sort_order:
                if (getSortPathFromPreferences() == POPULAR_API_PATH) {
                    getAndSetMovies(TOP_RATED_API_PATH);
                    prefs.edit().putString(SORT_PREFERENCE_KEY, TOP_RATED_API_PATH).commit();
                } else {
                    getAndSetMovies(POPULAR_API_PATH);
                    prefs.edit().putString(SORT_PREFERENCE_KEY, POPULAR_API_PATH).commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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

        prefs = getPreferences(MODE_PRIVATE);

        String queryPath = getSortPathFromPreferences();

        if (ApiUtils.isOnline(this.getApplicationContext())) {
            getAndSetMovies(queryPath);
        } else {
            Toast.makeText(this.getApplicationContext(), "No network connection available", Toast.LENGTH_LONG);
        }

        super.onStart();
    }

    private void getAndSetMovies(String queryPath) {
        GetMovieDetailsListTask movieTask = new GetMovieDetailsListTask(queryPath, this.movieAdapter, this.movieDetailsList, this.getApplicationContext());
        movieTask.execute();
    }

    private String getSortPathFromPreferences() {
        return prefs.getString(SORT_PREFERENCE_KEY, POPULAR_API_PATH);
    }
}
