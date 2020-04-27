package com.samdunkley.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourites")
public class MovieFavourite implements Parcelable {

    public static final Creator<MovieFavourite> CREATOR = new Creator<MovieFavourite>() {
        @Override
        public MovieFavourite createFromParcel(Parcel in) {
            return new MovieFavourite(in);
        }

        @Override
        public MovieFavourite[] newArray(int size) {
            return new MovieFavourite[size];
        }
    };
    @PrimaryKey
    @NonNull
    private Integer id;
    private String title;
    private String posterPath;

    public MovieFavourite(@NonNull Integer id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
    }

    protected MovieFavourite(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        posterPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(title);
        dest.writeString(posterPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }


}
