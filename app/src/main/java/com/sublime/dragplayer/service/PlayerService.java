package com.sublime.dragplayer.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import com.sublime.dragplayer.R;
import com.sublime.dragplayer.app.MainActivity;
import com.sublime.dragplayer.utils.Preferences;

/**
 * Created by goonerdroid
 * on 22/12/17.
 */

public class PlayerService extends Service {

    private WindowManager mWindowManager;
    private View mView;

    @SuppressLint("InflateParams")
    @Override
    public void onCreate() {
        super.onCreate();

        mView = LayoutInflater.from(this).inflate(R.layout.popup_player_layout, null);
        Preferences preferences = new Preferences(this);
        VideoView videoView = (VideoView) mView.findViewById(R.id.video_view);
        if (!preferences.getMovieURI().isEmpty()) {//loads window if URI is present.
            //Sets the window for the video layout.
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.BOTTOM | Gravity.END;
            params.x = 40;
            params.y = 140;


            setClickListeners(mView);
            setDragListener(mView, params);

            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (mWindowManager != null) mWindowManager.addView(mView, params);

            initVideo(videoView,preferences);
        }
    }

    private void initVideo(VideoView videoView,Preferences preferences){
        Uri path = Uri.parse(preferences.getMovieURI());
        videoView.setVideoURI(path);
        if (preferences.getMovieSeekCount() > 0) {//seeks to last watched seekcount
            videoView.seekTo(preferences.getMovieSeekCount());
        }
        videoView.start();
    }

    private void setDragListener(View view, final WindowManager.LayoutParams params) {
        view.findViewById(R.id.fl_root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;
                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the mView.
                        params.x = initialX - (int) (event.getRawX() - initialTouchX);
                        params.y = initialY - (int) (event.getRawY() - initialTouchY);
                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(view, params);
                        return true;
                }
                return false;
            }
        });
    }

    private void setClickListeners(View view){
        ImageView ivCloseView = (ImageView) view.findViewById(R.id.iv_close_view);
        ivCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });

        ImageView ivOpen = (ImageView) view.findViewById(R.id.iv_open);
        ivOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayerService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                stopSelf();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) mWindowManager.removeView(mView);
    }
}
