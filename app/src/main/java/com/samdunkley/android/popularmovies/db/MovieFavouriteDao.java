package com.samdunkley.android.popularmovies.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.samdunkley.android.popularmovies.model.MovieFavourite;

import java.util.List;

@Dao
public interface MovieFavouriteDao {

    @Query("SELECT * FROM favourites WHERE id = :movieId")
    MovieFavourite loadById(Integer movieId);

    @Query("SELECT * FROM favourites")
    LiveData<List<MovieFavourite>> loadFavourites();

    @Insert
    void insertFavourite(MovieFavourite movieFavourite);

    @Delete
    void deleteFavourite(MovieFavourite movieFavourite);
}
