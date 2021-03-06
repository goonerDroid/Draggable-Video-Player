package com.sublime.dragplayer.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sublime.dragplayer.R;
import com.sublime.dragplayer.adapter.MovieAdapter;
import com.sublime.dragplayer.fragments.MovieDetailFragment;
import com.sublime.dragplayer.fragments.MovieTrailerFragment;
import com.sublime.dragplayer.model.Movie;
import com.sublime.dragplayer.service.PlayerService;
import com.sublime.dragplayer.utils.Preferences;
import com.sublime.dragplayer.view.DraggableListener;
import com.sublime.dragplayer.view.DraggablePanel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener{

    @BindView(R.id.recycler_view)
    RecyclerView movieRecyclerView;
    @BindView(R.id.draggable_panel)
    DraggablePanel draggablePanel;


    private static final String APPLICATION_RAW_PATH =
            "android.resource://com.sublime.dragplayer/";
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;


    private ArrayList<Movie> mMovieList;
    private MovieTrailerFragment mMovieTrailerFragment;
    private MovieDetailFragment mMovieDetailFragment;
    private Preferences mPreferences;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Al Pacino Club");
        initMovieRecyclerView();

        mMovieTrailerFragment = new MovieTrailerFragment();
        mMovieDetailFragment = new MovieDetailFragment();
        mPreferences = new Preferences(this);

        initDraggableViewListener();
        initializeDraggablePanel();
        if (!mPreferences.isDrawPermissionGranted()) manageDrawPermission();
    }

    private void manageDrawPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setMessage("We need permission to draw over other apps.");
        builder.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Check if the application has draw over other apps permission or not?
                        //This permission is by default available for API<23. But for API > 23
                        //you have to ask for the permission in runtime.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                !Settings.canDrawOverlays(MainActivity.this)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                        } else {
                            mPreferences.isDrawPermissionGranted(true);
                        }
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this,
                                "Draw over other app permission not available.",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void launchPlayerService() {
        if (mPreferences.isDrawPermissionGranted() ) {
            startService(new Intent(this, PlayerService.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == 0) {
                mPreferences.isDrawPermissionGranted(true);
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                mPreferences.isDrawPermissionGranted(false);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    private void initializeDraggablePanel() {
        mPreferences.isVideoPlaying(false);
        draggablePanel.setFragmentManager(getSupportFragmentManager());
        draggablePanel.setTopFragment(mMovieTrailerFragment);
        draggablePanel.setBottomFragment(mMovieDetailFragment);
        draggablePanel.setClickToMaximizeEnabled(true);
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.dimen.x_scale_factor, typedValue, true);
        float xScaleFactor = typedValue.getFloat();
        typedValue = new TypedValue();
        getResources().getValue(R.dimen.y_scale_factor, typedValue, true);
        float yScaleFactor = typedValue.getFloat();
        draggablePanel.setXScaleFactor(xScaleFactor);
        draggablePanel.setYScaleFactor(yScaleFactor);
        draggablePanel.setTopViewHeight(getResources().getDimensionPixelSize(R.dimen.movie_trailer_fragment));
        draggablePanel.setTopFragmentMarginRight(
                getResources().getDimensionPixelSize(R.dimen.top_fragment_margin));
        draggablePanel.setTopFragmentMarginBottom(
                getResources().getDimensionPixelSize(R.dimen.top_fragment_margin));
        draggablePanel.initializeView();
        draggablePanel.setVisibility(View.GONE);
    }

    private void initMovieRecyclerView() {
        mMovieList = new ArrayList<>();
        mMovieAdapter = new MovieAdapter(Glide.with(this), mMovieList);
        mMovieAdapter.setItemClickListener(this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        movieRecyclerView.setLayoutManager(mLayoutManager);
        movieRecyclerView.setItemAnimator(new DefaultItemAnimator());
        movieRecyclerView.setAdapter(mMovieAdapter);

        initMovieData();
    }

    private void initDraggableViewListener() {
        draggablePanel.setDraggableListener(new DraggableListener() {
            @Override public void onMaximized() {
                mPreferences.isVideoPlaying(true);
                mMovieTrailerFragment.startVideo();
            }

            @Override public void onMinimized() {
                mPreferences.isVideoPlaying(true);
            }

            @Override public void onClosedToLeft() {
                mPreferences.isVideoPlaying(false);
                mMovieTrailerFragment.pauseVideo();
            }

            @Override public void onClosedToRight() {
                mPreferences.isVideoPlaying(false);
                mMovieTrailerFragment.pauseVideo();
            }
        });
    }

    private void initMovieData() {
        int movieThumbArray[] = new int[]{R.drawable.donnie_brasco,R.drawable.godfather,
                R.drawable.scarface,R.drawable.scent_of_a_woman,R.drawable.serpico};
        String movieTitleArray[] = new String[]{"Donnie Brasco","Godfather","Scarface","Scent of a Woman",
                "Serpico"};

        String movieYearArray[] = new String[]{"1997","1972","1983","1992","1973"};

        String movieGenreArray[] = new String[]{"Crime film/Thriller","Drama/Crime","Thriller/Action",
                "Coming of age/Drama","Crime film/Thriller"};

        String movieTrailer[] = new String[]{APPLICATION_RAW_PATH + R.raw.donnie_brasco,
                APPLICATION_RAW_PATH + R.raw.godfather,APPLICATION_RAW_PATH + R.raw.scarface,
                APPLICATION_RAW_PATH + R.raw.scent_of_a_woman,APPLICATION_RAW_PATH + R.raw.serpico};

        for (int i = 0 ; i < movieTitleArray.length ; i++){
            Movie movie = new Movie(movieTitleArray[i],movieThumbArray[i],movieYearArray[i],
                    movieGenreArray[i],movieTrailer[i]);
            mMovieList.add(movie);
        }
    }

    @Override
    public void onItemClick(Movie movie, View v, int adapterPosition) {
        showMovieTrailerFragment(movie);
    }

    private void showMovieTrailerFragment(Movie movie) {
        draggablePanel.setVisibility(View.VISIBLE);
        draggablePanel.maximize();
        mMovieTrailerFragment.showFragment(movie);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPreferences.isVideoPlaying())launchPlayerService();
    }


    @Override
    public void onBackPressed() {
        if (!mPreferences.isVideoPlaying()){
            mPreferences.isVideoPlaying(false);
            super.onBackPressed();
            return;
        }
        if (draggablePanel.isMinimized()) super.onBackPressed();
        if (draggablePanel.isMaximized()) draggablePanel.minimize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMovieAdapter != null) mMovieAdapter.removeItemClickListener();
    }
}
