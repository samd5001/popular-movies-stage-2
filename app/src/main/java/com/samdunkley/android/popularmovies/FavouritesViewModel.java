package com.samdunkley.android.popularmovies;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.samdunkley.android.popularmovies.db.MovieFavouriteDatabase;
import com.samdunkley.android.popularmovies.model.MovieFavourite;

import java.util.List;

public class FavouritesViewModel extends AndroidViewModel {

    private LiveData<List<MovieFavourite>> favourites;

    public FavouritesViewModel(@NonNull Application application) {
        super(application);
        MovieFavouriteDatabase db = MovieFavouriteDatabase.getInstance(this.getApplication());
        favourites = db.movieFavouriteDao().loadFavourites();
    }

    LiveData<List<MovieFavourite>> getFavourites() {
        return favourites;
    }
}
