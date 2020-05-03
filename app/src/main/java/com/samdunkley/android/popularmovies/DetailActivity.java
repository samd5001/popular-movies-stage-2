package com.samdunkley.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.samdunkley.android.popularmovies.adapters.ReviewAdapter;
import com.samdunkley.android.popularmovies.adapters.TrailerAdapter;
import com.samdunkley.android.popularmovies.background.DBExecutors;
import com.samdunkley.android.popularmovies.db.MovieFavouriteDatabase;
import com.samdunkley.android.popularmovies.model.MovieDetails;
import com.samdunkley.android.popularmovies.model.MovieFavourite;
import com.samdunkley.android.popularmovies.model.MovieReview;
import com.samdunkley.android.popularmovies.model.MovieTrailer;
import com.samdunkley.android.popularmovies.utils.ApiUtils;
import com.samdunkley.android.popularmovies.utils.DateUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";
    public static final String EXTRA_FAVOURITE = "extra_favourite";
    private static final String MOVIE_STATE_KEY = "movie";
    private static final String FAVOURITE_STATE_KEY ="isFavourite";
    private static final String TRAILERS_STATE_KEY = "trailers";
    private static final String REVIEWS_STATE_KEY = "reviews";

    private MovieFavouriteDatabase favouriteDb;

    private MovieDetails movieDetails;
    private Boolean isFavourite;

    private RecyclerView trailerRv;
    private TrailerAdapter trailerAdapter;
    private ArrayList<MovieTrailer> movieTrailers;

    private RecyclerView reviewRv;
    private ReviewAdapter reviewAdapter;
    private ArrayList<MovieReview> movieReviews;

    public void setMovieDetails(MovieDetails movieDetails) {
        this.movieDetails = movieDetails;
    }

    public void setMovieTrailers(ArrayList<MovieTrailer> movieTrailers) {
        if (this.movieTrailers == null) {
            this.movieTrailers = new ArrayList<>();
        } else {
            this.movieTrailers.clear();
        }

        this.movieTrailers.addAll(movieTrailers);
        trailerAdapter.notifyDataSetChanged();
    }

    public void setMovieReviews(ArrayList<MovieReview> movieReviews) {
        if (this.movieReviews == null) {
            this.movieReviews = new ArrayList<>();
        } else {
            this.movieReviews.clear();
        }

        this.movieReviews.addAll(movieReviews);
        reviewAdapter.notifyDataSetChanged();    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        favouriteDb = MovieFavouriteDatabase.getInstance(getApplicationContext());

        trailerRv = findViewById(R.id.trailer_rv);

        trailerRv.setHasFixedSize(true);

        RecyclerView.LayoutManager trailerLayoutManager = new LinearLayoutManager(this);
        trailerRv.setLayoutManager(trailerLayoutManager);

        trailerRv.addOnItemTouchListener(new PopularMoviesTouchListener(this.getApplicationContext(), (view, position) -> openYoutubeVideo(movieTrailers.get(position).getKey())));

        reviewRv = findViewById(R.id.review_rv);

        reviewRv.setHasFixedSize(true);
        RecyclerView.LayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        reviewRv.setLayoutManager(reviewLayoutManager);



        if (savedInstanceState != null) {
            movieDetails = savedInstanceState.getParcelable(MOVIE_STATE_KEY);
            isFavourite = savedInstanceState.getBoolean(FAVOURITE_STATE_KEY);
            movieReviews = populateStateArray(savedInstanceState, REVIEWS_STATE_KEY);
            movieTrailers = populateStateArray(savedInstanceState, TRAILERS_STATE_KEY);
            setRecyclerViewsAdapters();
            populateUI();
            setupFavouriteButton(isFavourite);
            return;
        }

        movieReviews = new ArrayList<>();
        movieTrailers = new ArrayList<>();
        setRecyclerViewsAdapters();

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                movieDetails = intent.getParcelableExtra(EXTRA_MOVIE);

                if (movieDetails != null) {
                    DBExecutors.getInstance().diskIO().execute(this::checkFavouriteSet);
                    ApiUtils.fetchMovieReviews(movieDetails.getId().toString(), this);
                    ApiUtils.fetchMovieTrailers(movieDetails.getId().toString(), this);
                    populateUI();
                    return;
                }
            }

            if (intent.hasExtra(EXTRA_FAVOURITE)) {
                MovieFavourite favourite = intent.getParcelableExtra(EXTRA_FAVOURITE);

                if (favourite != null) {
                    setTitleAndPoster(favourite.getTitle(), favourite.getPosterPath());
                    ApiUtils.fetchMovieAndPopulateUI(favourite.getId().toString(), this);
                    ApiUtils.fetchMovieReviews(favourite.getId().toString(), this);
                    ApiUtils.fetchMovieTrailers(favourite.getId().toString(), this);
                    setupFavouriteButton(true);
                    return;
                }
            }
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
        outState.putParcelableArrayList(TRAILERS_STATE_KEY, movieTrailers);
        outState.putParcelableArrayList(REVIEWS_STATE_KEY, movieReviews);
        super.onSaveInstanceState(outState);
    }

    private void setTitleAndPoster(String title, String posterUrl) {
        TextView titleTv = findViewById(R.id.detail_title_tv);
        titleTv.setText(title);

        ImageView thumbnailIv = findViewById(R.id.detail_thumbnail_iv);
        Picasso.get().load(posterUrl).fit().into(thumbnailIv);
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

        DBExecutors.getInstance().diskIO().execute(() -> {
            if (isFavourite) {
                favouriteDb.movieFavouriteDao().insertFavourite(favourite);
            } else {
                favouriteDb.movieFavouriteDao().deleteFavourite(favourite);
            }
        });
    }

    private void setRecyclerViewsAdapters() {
        trailerAdapter = new TrailerAdapter(movieTrailers);
        trailerRv.setAdapter(trailerAdapter);

        reviewAdapter = new ReviewAdapter(movieReviews);
        reviewRv.setAdapter(reviewAdapter);
    }

    private void openYoutubeVideo(String key){

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_app_url) + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.youtube_watch_url) + key));
        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            this.startActivity(webIntent);
        }
    }

    private <T extends Parcelable> ArrayList<T> populateStateArray(Bundle savedInstanceState, String stateKey) {
        ArrayList<T> movieList;
        if (!savedInstanceState.containsKey(stateKey)) {
            movieList = new ArrayList<>();
        } else {
            movieList = savedInstanceState.getParcelableArrayList(stateKey);
        }
        return movieList;
    }

    private void checkFavouriteSet() {
        MovieFavourite favourite = favouriteDb.movieFavouriteDao().loadById(movieDetails.getId());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (favourite == null) {
                    setupFavouriteButton(false);
                } else {
                    setupFavouriteButton(true);
                }
            }
        });
    }
}
