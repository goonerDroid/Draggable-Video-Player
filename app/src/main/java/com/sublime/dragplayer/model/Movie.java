package com.sublime.dragplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by goonerdroid
 * on 22/12/17.
 */

public class Movie implements Parcelable{

    private String movieName;
    private int movieThumbnail;
    private String movieYear;
    private String movieGenre;


    public Movie(String movieName,int movieThumbnail,String movieYear,String movieGenre) {
        this.movieName = movieName;
        this.movieThumbnail = movieThumbnail;
        this.movieYear = movieYear;
        this.movieGenre = movieGenre;
    }

    private Movie(Parcel parcel) {
        movieName = parcel.readString();
        movieThumbnail = parcel.readInt();
        movieYear = parcel.readString();
        movieGenre = parcel.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };


    public String getMovieName() {
        return movieName;
    }

    public int getMovieThumbnail() {
        return movieThumbnail;
    }

    public String getMovieYear() {
        return movieYear;
    }

    public String getMovieGenre() {
        return movieGenre;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieName);
        parcel.writeInt(movieThumbnail);
        parcel.writeString(movieYear);
        parcel.writeString(movieGenre);
    }
}
