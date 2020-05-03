package com.samdunkley.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.samdunkley.android.popularmovies.adapters.MovieAdapter;
import com.samdunkley.android.popularmovies.background.GetMovieDetailsListTask;
import com.samdunkley.android.popularmovies.model.MovieDetails;
import com.samdunkley.android.popularmovies.model.MovieFavourite;
import com.samdunkley.android.popularmovies.utils.ApiUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String MOVIES_STATE_KEY = "movie_details";
    private static final String SORT_PREFERENCE_KEY = "sortOrder";
    private static final String POPULAR_API_PATH = "popular";
    private static final String TOP_RATED_API_PATH = "top_rated";
    private static final String FAVOURITES = "favourites";

    private MovieAdapter movieAdapter;
    private ArrayList<MovieDetails> movieDetailsList;
    private ArrayList<MovieFavourite> movieFavouritesList;

    private SharedPreferences prefs;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (getSortPathFromPreferences()) {
            case POPULAR_API_PATH:
                MenuItem popular = menu.findItem(R.id.pref_sort_popular);
                popular.setChecked(true);
                break;
            case TOP_RATED_API_PATH:
                MenuItem topRated = menu.findItem(R.id.pref_sort_highest);
                topRated.setChecked(true);
                break;
            case FAVOURITES:
                MenuItem favourites = menu.findItem(R.id.pref_sort_favourite);
                favourites.setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.pref_sort_popular:
                return handleOptionClick(POPULAR_API_PATH);
            case R.id.pref_sort_highest:
                return handleOptionClick(TOP_RATED_API_PATH);
            case R.id.pref_sort_favourite:
                return handleOptionClick(FAVOURITES);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean handleOptionClick(String sortOrder) {
        if (FAVOURITES.equals(sortOrder)) {
            setMoviesFromDB();
        } else {
            getAndSetMoviesFromApi(sortOrder);
        }
        prefs.edit().putString(SORT_PREFERENCE_KEY, sortOrder).apply();
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_STATE_KEY, movieDetailsList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadFromSavedInstanceState(savedInstanceState);

        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.movie_rv);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter(movieDetailsList);
        recyclerView.setAdapter(movieAdapter);

        recyclerView.addOnItemTouchListener(new PopularMoviesTouchListener(this.getApplicationContext(), (view, position) -> {
            if (FAVOURITES.equals(getSortPathFromPreferences())) {
                launchDetailActivity(movieFavouritesList.get(position));
            } else {
                launchDetailActivity(movieDetailsList.get(position));
            }
        }));
    }

    @Override
    protected void onStart() {

        prefs = getPreferences(MODE_PRIVATE);

        String queryPath = getSortPathFromPreferences();

        if (!FAVOURITES.equals(queryPath)) {
            getAndSetMoviesFromApi(queryPath);
        }

        setupFavouritesViewModel();

        super.onStart();
    }

    private void getAndSetMoviesFromApi(String queryPath) {
        if (ApiUtils.isOnline(this.getApplicationContext())) {
            GetMovieDetailsListTask movieTask = new GetMovieDetailsListTask(queryPath, this.movieAdapter, this.movieDetailsList, this.getApplicationContext());
            movieTask.execute();
        } else {
            Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
        }
    }

    private String getSortPathFromPreferences() {
        return prefs.getString(SORT_PREFERENCE_KEY, POPULAR_API_PATH);
    }

    private void launchDetailActivity(MovieDetails movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    private void launchDetailActivity(MovieFavourite movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_FAVOURITE, movie);
        startActivity(intent);
    }

    private void setupFavouritesViewModel() {
        FavouritesViewModel viewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);
        viewModel.getFavourites().observe(this, favourites -> {
            movieFavouritesList.clear();

            movieFavouritesList.addAll(favourites);

            String queryPath = getSortPathFromPreferences();

            if (FAVOURITES.equals(queryPath)) {
                setMoviesFromDB();
            }
        });
    }

    private void setMoviesFromDB() {

            movieDetailsList.clear();

        for (MovieFavourite favourite : movieFavouritesList) {
            movieDetailsList.add(new MovieDetails(favourite.getId(), favourite.getTitle(), favourite.getPosterPath(), null, null, null));
        }
        movieAdapter.notifyDataSetChanged();
    }

    private void loadFromSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIES_STATE_KEY)) {
            movieDetailsList = new ArrayList<>();
        } else {
                movieDetailsList = savedInstanceState.getParcelableArrayList(MOVIES_STATE_KEY);
            }

        movieFavouritesList = new ArrayList<>();
    }
}
