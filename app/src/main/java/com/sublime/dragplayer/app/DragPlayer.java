package com.sublime.dragplayer.app;

import android.app.Application;

import com.sublime.dragplayer.BuildConfig;
import com.sublime.dragplayer.utils.Timber;

/**
 * Created by goonerdroid
 * on 22/12/17.
 */

public class DragPlayer extends Application {

    @Override public void onCreate() {
        super.onCreate();
        //Initializes Timber logging only on debug build :-)
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
