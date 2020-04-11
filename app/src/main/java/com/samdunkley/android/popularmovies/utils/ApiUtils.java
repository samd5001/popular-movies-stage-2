package com.samdunkley.android.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.samdunkley.android.popularmovies.R;
import com.samdunkley.android.popularmovies.model.MovieDetails;

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

    private static final String BASE_MOVIE_DB_URL = "https://image.tmdb.org/t/p/";

    private static final String BASE_QUERY_URL = "https://api.themoviedb.org/3/movie";

    private static final String RESULTS_JSON_KEY = "results";

    private static final String SIZE = "w185/";

    private static String buildPosterUrlString(String path) {
        return BASE_MOVIE_DB_URL + SIZE + path;
    }

    private static URL buildUrl(String queryPath, Context context) throws MalformedURLException {
        return new URL(Uri.parse(BASE_QUERY_URL)
                .buildUpon()
                .appendPath(queryPath)
                .appendQueryParameter(API_KEY_NAME, context.getString(R.string.movie_db_api_key))
                .build()
                .toString());
    }

    public static List<MovieDetails> fetchMovies(String sortType, Context context) {
        try {
            JSONObject movieListJson = getMovieListJson(buildUrl(sortType, context));
            if (movieListJson == null)
                return new ArrayList<>();
            JSONArray movieJsonArray = movieListJson.optJSONArray(RESULTS_JSON_KEY);
            ArrayList<MovieDetails> arrayList = new ArrayList();
            if (movieJsonArray != null)
                for (int i = 0; i < movieJsonArray.length(); i++) {
                    JSONObject movieDetailsJson = movieJsonArray.optJSONObject(i);
                    if (movieDetailsJson != null)
                        arrayList.add(new MovieDetails(
                                movieDetailsJson.optString(MovieDetails.TITLE),
                                buildPosterUrlString(movieDetailsJson.optString(MovieDetails.POSTER_PATH)),
                                movieDetailsJson.optString(MovieDetails.SYNOPSIS),
                                movieDetailsJson.optInt(MovieDetails.RATING),
                                movieDetailsJson.optString(MovieDetails.RELEASE_DATE)));
                }
            return arrayList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
