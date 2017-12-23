package com.sublime.dragplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by goonerdroid
 * on 23/12/17.
 */

public class Preferences {

    private static final String MOVIE_URI = "movie_uri";
    private static final String MOVIE_SEEK = "movie_seek";
    private static final String PERMISSION_GRANTED = "permission_granted";
    private static final String VIDEO_PREF = "video_pref";


    private static final String KEY_URI = "key_uri";
    private static final String KEY_SEEK_COUNT = "key_seek_count";
    private static final String KEY_DRAW_PERMISSION = "key_draw_permissions";
    private static final String KEY_VIDEO_STATE = "key_video_state";


    private Context context;

    public Preferences(Context context) {
        super();
        this.context = context;
    }

    public void saveMovieURI(String movieURI) {
        SharedPreferences prefs = context.getSharedPreferences(MOVIE_URI, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_URI, movieURI);
        editor.apply();
    }

    public void saveMovieSeekCount(int movieSeekCount) {
        SharedPreferences prefs = context.getSharedPreferences(MOVIE_SEEK, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SEEK_COUNT, movieSeekCount);
        editor.apply();
    }


    public void isDrawPermissionGranted(boolean isPermissionGranted) {
        SharedPreferences prefs = context.getSharedPreferences(PERMISSION_GRANTED, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_DRAW_PERMISSION, isPermissionGranted);
        editor.apply();
    }

    public void isVideoPlaying(boolean isVideoPlaying) {
        SharedPreferences prefs = context.getSharedPreferences(VIDEO_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_VIDEO_STATE, isVideoPlaying);
        editor.apply();
    }



    public String getMovieURI() {
        return context.getSharedPreferences(MOVIE_URI, MODE_PRIVATE).getString(KEY_URI,"");
    }

    public int getMovieSeekCount() {
        return context.getSharedPreferences(MOVIE_SEEK, MODE_PRIVATE).getInt(KEY_SEEK_COUNT,0);
    }

    public boolean isDrawPermissionGranted() {
        return context.getSharedPreferences(PERMISSION_GRANTED, MODE_PRIVATE).getBoolean(KEY_DRAW_PERMISSION,false);
    }


    public boolean isVideoPlaying() {
        return context.getSharedPreferences(VIDEO_PREF, MODE_PRIVATE).getBoolean(KEY_VIDEO_STATE,false);
    }


}
