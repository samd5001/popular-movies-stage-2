package com.samdunkley.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieTrailer implements Parcelable {

    public static final String KEY = "key";
    public static final String NAME = "name";

    private static final String YOUTUBE_THUMBNAIL_URL = "https://img.youtube.com/vi/";
    private static final String YOUTUBE_DEFAULT_THUMBNAIL = "/hqdefault.jpg";

    private String key;
    private String name;
    private String thumbnailUrl;

    public MovieTrailer(String key, String name) {
        this.key = key;
        this.name = name;
        thumbnailUrl = YOUTUBE_THUMBNAIL_URL + key + YOUTUBE_DEFAULT_THUMBNAIL;
    }

    protected MovieTrailer(Parcel in) {
        key = in.readString();
        name = in.readString();
        thumbnailUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(thumbnailUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
