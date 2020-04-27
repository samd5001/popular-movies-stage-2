package com.samdunkley.android.popularmovies.db;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.samdunkley.android.popularmovies.model.MovieFavourite;

@Database(entities = {MovieFavourite.class}, version = 1, exportSchema = false)
public abstract class MovieFavouriteDatabase extends RoomDatabase {

    private static final String LOG_TAG = MovieFavouriteDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DB_NAME = "popular_movies";
    private static MovieFavouriteDatabase instance;

    public static MovieFavouriteDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating db instance");
                instance = Room.databaseBuilder(context.getApplicationContext(), MovieFavouriteDatabase.class, DB_NAME).build();
            }
        }
        Log.d(LOG_TAG, "Getting db instance");
        return instance;
    }

    public abstract MovieFavouriteDao movieFavouriteDao();
}
