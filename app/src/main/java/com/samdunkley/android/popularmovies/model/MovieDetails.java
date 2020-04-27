package com.samdunkley.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieDetails implements Parcelable {

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String POSTER_PATH = "poster_path";
    public static final String SYNOPSIS = "overview";
    public static final String RATING = "vote_average";
    public static final String RELEASE_DATE = "release_date";

    public static final Creator<MovieDetails> CREATOR = new Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    private Integer id;
    private String title;
    private String posterUrl;
    private String synopsis;
    private Integer rating;
    private String releaseDate;

    public MovieDetails(Integer id, String title, String posterUrl, String synopsis, Integer rating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    private MovieDetails(Parcel in) {
        if (in.readByte() == 0) {
            rating = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        posterUrl = in.readString();
        synopsis = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readInt();
        }
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(title);
        parcel.writeString(posterUrl);
        parcel.writeString(synopsis);
        if (rating == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(rating);
        }
        parcel.writeString(releaseDate);
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

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

}
