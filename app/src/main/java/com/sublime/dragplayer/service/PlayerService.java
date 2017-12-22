package com.sublime.dragplayer.service;

import android.app.Service;
import android.content.Context;
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

    public static boolean isRunning = false;
    private WindowManager windowManager;
    private View view;

    /**
     * Static helper method to start the service if stopped
     */
    public static boolean checkServiceAndStart(Context context) {
        if (!isRunning) {
            context.startService(new Intent(context, PlayerService.class));
            return false;
        }
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popup_player_layout, null);
        initVideo(view);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,

                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.x = 40;
        params.y = 140;
        setClickListeners(view);
        setDragListener(view,params);

        windowManager.addView(view, params);
    }

    private void initVideo(View view){
        Preferences preferences = new Preferences(getApplicationContext());
        VideoView videoView = view.findViewById(R.id.video_view);
        Uri path = Uri.parse(preferences.getMovieURI());
        videoView.setVideoURI(path);
        if (preferences.getMovieSeekCount() > 0){
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
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX - (int) (event.getRawX() - initialTouchX);
                        params.y = initialY - (int) (event.getRawY() - initialTouchY);
                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(view, params);
                        return true;
                }
                return false;
            }
        });
    }

    private void setClickListeners(View view){
        ImageView ivCloseView = view.findViewById(R.id.iv_close_view);
        ivCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });

        ImageView ivOpen = view.findViewById(R.id.iv_open);
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return super.onStartCommand(null, flags, startId);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(view);
        stopForeground(true);
        isRunning = false;
    }
}
