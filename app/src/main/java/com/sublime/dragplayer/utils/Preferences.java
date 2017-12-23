package com.sublime.dragplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by goonerdroid
 * on 23/12/17.
 */

public class Preferences {

    private Context context;

    public Preferences(Context context) {
        super();
        this.context = context;
    }

    public void saveMovieURI(String movieURI) {
        SharedPreferences prefs = context.getSharedPreferences("MOVIE_URI", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("URI", movieURI);
        editor.apply();
    }

    public void saveMovieSeekCount(int movieSeekCount) {
        SharedPreferences prefs = context.getSharedPreferences("MOVIE_SEEK", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("SEEK_COUNT", movieSeekCount);
        editor.apply();
    }


    public void isDrawPermissionGranted(boolean isPermissionGranted) {
        SharedPreferences prefs = context.getSharedPreferences("PERMISSION_GRANTED", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("DRAW_PERMISSION", isPermissionGranted);
        editor.apply();
    }

    public void isVideoPlaying(boolean isVideoPlaying) {
        SharedPreferences prefs = context.getSharedPreferences("VIDEO_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("VIDEO_STATE", isVideoPlaying);
        editor.apply();
    }



    public String getMovieURI() {
        return context.getSharedPreferences("MOVIE_URI", MODE_PRIVATE).getString("URI","");
    }

    public int getMovieSeekCount() {
        return context.getSharedPreferences("MOVIE_SEEK", MODE_PRIVATE).getInt("SEEK_COUNT",0);
    }

    public boolean isDrawPermissionGranted() {
        return context.getSharedPreferences("PERMISSION_GRANTED", MODE_PRIVATE).getBoolean("DRAW_PERMISSION",false);
    }


    public boolean isVideoPlaying() {
        return context.getSharedPreferences("VIDEO_PREF", MODE_PRIVATE).getBoolean("VIDEO_STATE",false);
    }


}
