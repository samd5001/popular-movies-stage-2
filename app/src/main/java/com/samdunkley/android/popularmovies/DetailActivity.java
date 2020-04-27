package com.samdunkley.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.samdunkley.android.popularmovies.background.DBExecutors;
import com.samdunkley.android.popularmovies.db.MovieFavouriteDatabase;
import com.samdunkley.android.popularmovies.model.MovieDetails;
import com.samdunkley.android.popularmovies.model.MovieFavourite;
import com.samdunkley.android.popularmovies.utils.ApiUtils;
import com.samdunkley.android.popularmovies.utils.DateUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";
    public static final String EXTRA_FAVOURITE = "extra_favourite";
    private static final String MOVIE_STATE_KEY = "movie";
    private static final String FAVOURITE_STATE_KEY ="isFavourite";

    private MovieFavouriteDatabase favouriteDb;

    private MovieDetails movieDetails;
    private Boolean isFavourite;

    public void setMovieDetails(MovieDetails movieDetails) {
        this.movieDetails = movieDetails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            this.movieDetails = savedInstanceState.getParcelable(MOVIE_STATE_KEY);
            this.isFavourite = savedInstanceState.getBoolean(FAVOURITE_STATE_KEY);
            populateUI();
            setupFavouriteButton(isFavourite);
            return;
        }

        Intent intent = getIntent();

        if (intent == null || (!intent.hasExtra((EXTRA_MOVIE)) && !intent.hasExtra((EXTRA_FAVOURITE)))) {
            closeOnError();
        }

        favouriteDb = MovieFavouriteDatabase.getInstance(getApplicationContext());

        movieDetails = intent.getParcelableExtra(EXTRA_MOVIE);

        if (movieDetails != null) {
            populateUI();
            setupFavouriteButton(false);
            return;
        }

        MovieFavourite favourite = intent.getParcelableExtra(EXTRA_FAVOURITE);

        if (favourite != null) {
            setTitleAndPoster(favourite.getTitle(), favourite.getPosterPath());
            ApiUtils.fetchMovieAndPopulateUI(favourite.getId().toString(), this);
            setupFavouriteButton(true);
            return;
        }

        closeOnError();

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_open_error_message, Toast.LENGTH_SHORT).show();
    }

    public void populateUI() {

        setTitleAndPoster(movieDetails.getTitle(), movieDetails.getPosterUrl());

        TextView releaseDateTv = findViewById(R.id.detail_release_tv);
        releaseDateTv.setText(DateUtils.getYearFromDateString(movieDetails.getReleaseDate(), this));

        TextView ratingTv = findViewById(R.id.detail_rating_tv);
        ratingTv.setText(getApplicationContext().getString(R.string.detail_rating_template, movieDetails.getRating().toString()));

        TextView overviewTv = findViewById(R.id.detail_overview_tv);
        overviewTv.setText(movieDetails.getSynopsis());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE_STATE_KEY, movieDetails);
        outState.putBoolean(FAVOURITE_STATE_KEY, isFavourite);
        super.onSaveInstanceState(outState);
    }

    private void setTitleAndPoster(String title, String posterUrl) {
        TextView titleTv = findViewById(R.id.detail_title_tv);
        titleTv.setText(title);

        ImageView thumbnailIv = findViewById(R.id.detail_thumbnail_iv);
        Picasso.get().load(posterUrl).into(thumbnailIv);
    }

    private void setupFavouriteButton(boolean isFavourite) {
        ToggleButton favouriteTb = findViewById(R.id.favourite_button);
        this.isFavourite= isFavourite;
        favouriteTb.setChecked(isFavourite);
        setFavouriteButton(favouriteTb, isFavourite);
        favouriteTb.setOnCheckedChangeListener((button, checked) -> {
            toggleFavourite(movieDetails, checked);
            setFavouriteButton(favouriteTb, checked);
        });
    }

    private void setFavouriteButton(ToggleButton favouriteTb, boolean isFavourite) {
        if (isFavourite) {
            favouriteTb.setBackgroundResource(R.drawable.baseline_star_24);
        } else {
            favouriteTb.setBackgroundResource(R.drawable.baseline_star_outline_24);
        }
    }

    private void toggleFavourite(MovieDetails movie, boolean isFavourite) {
        MovieFavourite favourite = new MovieFavourite(movie.getId(), movie.getTitle(), movie.getPosterUrl());

        DBExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (isFavourite) {
                    favouriteDb.movieFavouriteDao().insertFavourite(favourite);
                } else {
                    favouriteDb.movieFavouriteDao().deleteFavourite(favourite);
                }
            }
        });
    }
}
