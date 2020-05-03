package com.samdunkley.android.popularmovies.utils;

import android.content.Context;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.samdunkley.android.popularmovies.DetailActivity;
import com.samdunkley.android.popularmovies.R;
import com.samdunkley.android.popularmovies.background.VolleyRequestSingleton;
import com.samdunkley.android.popularmovies.model.MovieDetails;
import com.samdunkley.android.popularmovies.model.MovieReview;
import com.samdunkley.android.popularmovies.model.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApiUtils {
    private static final String API_KEY_NAME = "api_key";
    private static final String BASE_POSTER_DB_URL = "https://image.tmdb.org/t/p/";
    private static final String BASE_QUERY_URL = "https://api.themoviedb.org/3/movie";
    private static final String RESULTS_JSON_KEY = "results";
    private static final String SIZE = "w185/";
    private static final String TRAILER_API_PATH = "videos";
    private static final String REVIEW_API_PATH = "reviews";
    public static final String SITE_KEY ="site";
    private static final String YOUTUBE_VIDEO = "YouTube";
    public static final String TYPE_KEY ="type";
    private static final String TRAILER_VIDEO = "Trailer";



    private static String buildPosterUrlString(String path) {
        return BASE_POSTER_DB_URL + SIZE + path;
    }

    private static String buildMovieQueryUrl(String queryPath, Context context) throws MalformedURLException {
        return Uri.parse(BASE_QUERY_URL)
                .buildUpon()
                .appendPath(queryPath)
                .appendQueryParameter(API_KEY_NAME, context.getString(R.string.movie_db_api_key))
                .build()
                .toString();
    }

    private static String buildMovieDataUrl(String movieId, String dataPath, Context context) throws MalformedURLException {
        return Uri.parse(BASE_QUERY_URL)
                .buildUpon()
                .appendPath(movieId)
                .appendPath(dataPath)
                .appendQueryParameter(API_KEY_NAME, context.getString(R.string.movie_db_api_key))
                .build()
                .toString();
    }

    public static List<MovieDetails> fetchMovies(String sortType, Context context) {
        try {
            JSONObject movieListJson = getMovieListJson(new URL(buildMovieQueryUrl(sortType, context)));
            if (movieListJson == null)
                return new ArrayList<>();
            JSONArray movieJsonArray = movieListJson.optJSONArray(RESULTS_JSON_KEY);
            ArrayList<MovieDetails> arrayList = new ArrayList<>();
            if (movieJsonArray != null)
                for (int i = 0; i < movieJsonArray.length(); i++) {
                    JSONObject movieDetailsJson = movieJsonArray.optJSONObject(i);
                    if (movieDetailsJson != null)
                        arrayList.add(createMovieDetailsFromJson(movieDetailsJson));
                }
            return arrayList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void fetchMovieAndPopulateUI(String movieId, DetailActivity activity) {
        try {
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    buildMovieQueryUrl(movieId, activity.getApplicationContext()),
                    null,
                    response -> {
                        MovieDetails movieDetails = createMovieDetailsFromJson(response);
                        activity.setMovieDetails(movieDetails);
                        activity.populateUI();

                    },
                    error -> {
                        error.printStackTrace();

                    });

            VolleyRequestSingleton.getInstance(activity.getApplicationContext()).addToRequestQueue(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void fetchMovieTrailers(String movieId, DetailActivity activity) {
        try {
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    buildMovieDataUrl(movieId, TRAILER_API_PATH, activity.getApplicationContext()),
                    null,
                    response -> {
                        JSONArray trailersJsonArray = response.optJSONArray(RESULTS_JSON_KEY);
                        if (trailersJsonArray != null) {
                            ArrayList<MovieTrailer> trailers = new ArrayList<>();

                            for (int i = 0; i < trailersJsonArray.length(); i++) {
                                JSONObject trailerJson = trailersJsonArray.optJSONObject(i);
                                if (trailerJson != null && TRAILER_VIDEO.equalsIgnoreCase(trailerJson.optString(TYPE_KEY))
                                    && YOUTUBE_VIDEO.equalsIgnoreCase(trailerJson.optString(SITE_KEY))) {
                                    trailers.add(new MovieTrailer(trailerJson.optString(MovieTrailer.KEY), trailerJson.optString(MovieTrailer.NAME)));
                                }
                            }

                            activity.setMovieTrailers(trailers);
                        }
                    },
                    error -> {
                        error.printStackTrace();

                    });

            VolleyRequestSingleton.getInstance(activity.getApplicationContext()).addToRequestQueue(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void fetchMovieReviews(String movieId, DetailActivity activity) {
        try {
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    buildMovieDataUrl(movieId, REVIEW_API_PATH, activity.getApplicationContext()),
                    null,
                    response -> {
                            JSONArray reviewsJsonArray = response.optJSONArray(RESULTS_JSON_KEY);
                            if (reviewsJsonArray != null) {
                                ArrayList<MovieReview> reviews = new ArrayList<>();

                                for (int i = 0; i < reviewsJsonArray.length(); i++) {
                                    JSONObject reviewJson = reviewsJsonArray.optJSONObject(i);
                                    if (reviewJson != null) {
                                        reviews.add(new MovieReview(reviewJson.optString(MovieReview.AUTHOR), reviewJson.optString(MovieReview.CONTENT)));
                                    }
                                }

                                activity.setMovieReviews(reviews);
                            }

                    },
                    error -> {
                        error.printStackTrace();
                    });

            VolleyRequestSingleton.getInstance(activity.getApplicationContext()).addToRequestQueue(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static MovieDetails createMovieDetailsFromJson(JSONObject movieDetailsJson) {
        return new MovieDetails(
                movieDetailsJson.optInt(MovieDetails.ID),
                movieDetailsJson.optString(MovieDetails.TITLE),
                buildPosterUrlString(movieDetailsJson.optString(MovieDetails.POSTER_PATH)),
                movieDetailsJson.optString(MovieDetails.SYNOPSIS),
                movieDetailsJson.optInt(MovieDetails.RATING),
                movieDetailsJson.optString(MovieDetails.RELEASE_DATE));
    }

    private static JSONObject getMovieListJson(URL queryUrl) {
        try {
            HttpURLConnection connection = (HttpURLConnection) queryUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\A");
            boolean bool = scanner.hasNext();
            if (bool)
                try {
                    return new JSONObject(scanner.next());
                } catch (JSONException jSONException) {
                    jSONException.printStackTrace();
                }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isOnline(Context paramContext) {
        NetworkInfo networkInfo = ((ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
}
