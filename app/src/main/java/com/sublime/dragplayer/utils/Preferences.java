package com.sublime.dragplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    protected SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveMovieURI(String movieURI) {
        SharedPreferences prefs = context.getSharedPreferences("Movie URI", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("URI", movieURI);
        editor.apply();
    }

    public void saveMovieSeekCount(int movieSeekCount) {
        SharedPreferences prefs = context.getSharedPreferences("Movie Seek", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("SEEK_COUNT", movieSeekCount);
        editor.apply();
    }

    public String getMovieURI() {
        return context.getSharedPreferences("Movie URI", MODE_PRIVATE).getString("URI","");
    }

    public int getMovieSeekCount() {
        return context.getSharedPreferences("Movie Seek", MODE_PRIVATE).getInt("SEEK_COUNT",0);
    }


}
