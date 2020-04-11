package com.samdunkley.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.samdunkley.android.popularmovies.model.MovieDetails;
import com.samdunkley.android.popularmovies.utils.DateUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        MovieDetails movie = intent.getParcelableExtra(EXTRA_MOVIE);

        if (movie == null) {
            closeOnError();
            return;
        }

        populateUI(movie);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_open_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(MovieDetails movie) {

        TextView titleTv = findViewById(R.id.detail_title_tv);
        titleTv.setText(movie.getTitle());

        ImageView thumbnailIv = findViewById(R.id.detail_thumbnail_iv);
        Picasso.get().load(movie.getPosterUrl()).into(thumbnailIv);

        TextView releaseDateTv = findViewById(R.id.detail_release_tv);
        releaseDateTv.setText(DateUtils.getYearFromDateString(movie.getReleaseDate(), this));

        TextView ratingTv = findViewById(R.id.detail_rating_tv);
        ratingTv.setText(getApplicationContext().getString(R.string.detail_rating_template, movie.getRating().toString()));

        TextView overviewTv = findViewById(R.id.detail_overview_tv);
        overviewTv.setText(movie.getSynopsis());

    }
}
