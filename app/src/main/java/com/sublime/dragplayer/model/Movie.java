package com.sublime.dragplayer.model;

/**
 * Created by goonerdroid
 * on 22/12/17.
 */

public class Movie {

    private String movieName;
    private int movieThumbnail;
    private String movieYear;
    private String movieGenre;


    public Movie() {
    }


    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public int getMovieThumbnail() {
        return movieThumbnail;
    }

    public void setMovieThumbnail(int movieThumbnail) {
        this.movieThumbnail = movieThumbnail;
    }

    public String getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(String movieYear) {
        this.movieYear = movieYear;
    }

    public String getMovieGenre() {
        return movieGenre;
    }

    public void setMovieGenre(String movieGenre) {
        this.movieGenre = movieGenre;
    }
}
