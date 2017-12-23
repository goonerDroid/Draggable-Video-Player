package com.sublime.dragplayer.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.sublime.dragplayer.utils.DraggableState;
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

    private static final String DRAGGABLE_PANEL_STATE = "draggable_panel_state";
    private static final String LAST_LOADED_MOVIE = "last_movie";
    private static final String APPLICATION_RAW_PATH =
            "android.resource://com.sublime.dragplayer/";
    private static final int DELAY_MILLIS = 50;
    private ArrayList<Movie> movieList;
    private MovieTrailerFragment movieTrailerFragment;
    private Movie movie;
    private MovieDetailFragment movieDetailFragment;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        if (getSupportActionBar() != null)getSupportActionBar().setTitle("Al Pacino Club");
        initMovieRecyclerView();
        movieTrailerFragment = new MovieTrailerFragment();
        movieDetailFragment = new MovieDetailFragment();
        preferences = new Preferences(this);
        initDraggableViewListener();
        initializeDraggablePanel();
       if (!preferences.isDrawpermissionGranted()) manageDrawPermission();
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


                            //If the draw over permission is not available open the settings screen
                            //to grant the permission.
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                        } else {
                            preferences.isDrawPermissionGranted(true);
                        }
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this,
                                "Draw over other app permission not available. Closing the application",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void launchPlayerService() {
        if (preferences.isDrawpermissionGranted()) {
            startService(new Intent(this, PlayerService.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == 0) {
                preferences.isDrawPermissionGranted(true);
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                preferences.isDrawPermissionGranted(false);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    private void initializeDraggablePanel() {
        draggablePanel.setFragmentManager(getSupportFragmentManager());
        draggablePanel.setTopFragment(movieTrailerFragment);
        draggablePanel.setBottomFragment(movieDetailFragment);
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
        movieList = new ArrayList<>();
        MovieAdapter movieAdapter = new MovieAdapter(Glide.with(this), movieList);
        movieAdapter.setItemClickListener(this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        movieRecyclerView.setLayoutManager(mLayoutManager);
        movieRecyclerView.setItemAnimator(new DefaultItemAnimator());
        movieRecyclerView.setAdapter(movieAdapter);

        initMovieData();
    }


    /**
     * Save the DraggablePanel state to restore it once the activity lifecycle be rebooted.
     *
     * @param outState bundle to put the DraggableState information.
     */
    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveDraggableState(outState);
        saveLastPlaceLoadedPosition(outState);
    }

    /**
     * Restore the DraggablePanel state.
     *
     * @param savedInstanceState bundle to get the Draggable state.
     */
    @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recoverDraggablePanelState(savedInstanceState);
        loadLastPlaceClicked(savedInstanceState);
    }

    /**
     * Get the DraggablePanelState from the saved bundle, modify the DraggablePanel visibility to
     * GONE
     * and apply the
     * DraggablePanelState to recover the last graphic state.
     */
    private void recoverDraggablePanelState(Bundle savedInstanceState) {
        final DraggableState draggableState =
                (DraggableState) savedInstanceState.getSerializable(DRAGGABLE_PANEL_STATE);
        if (draggableState == null) {
            draggablePanel.setVisibility(View.GONE);
            return;
        }
        updateDraggablePanelStateDelayed(draggableState);
    }

    /**
     * Return the view to the DraggablePanelState: minimized, maximized, closed to the right or
     * closed
     * to the left.
     *
     * @param draggableState to apply.
     */
    private void updateDraggablePanelStateDelayed(DraggableState draggableState) {
        Handler handler = new Handler();
        switch (draggableState) {
            case MAXIMIZED:
                handler.postDelayed(new Runnable() {
                    @Override public void run() {
                        draggablePanel.maximize();
                    }
                }, DELAY_MILLIS);
                break;
            case MINIMIZED:
                handler.postDelayed(new Runnable() {
                    @Override public void run() {
                        draggablePanel.minimize();
                    }
                }, DELAY_MILLIS);
                break;
            case CLOSED_AT_LEFT:
                handler.postDelayed(new Runnable() {
                    @Override public void run() {
                        movieTrailerFragment.pauseVideo();
                        draggablePanel.setVisibility(View.GONE);
                        draggablePanel.closeToLeft();
                    }
                }, DELAY_MILLIS);
                break;
            case CLOSED_AT_RIGHT:
                handler.postDelayed(new Runnable() {
                    @Override public void run() {
                        movieTrailerFragment.pauseVideo();
                        draggablePanel.setVisibility(View.GONE);
                        draggablePanel.closeToRight();
                    }
                }, DELAY_MILLIS);
                break;
            default:
                draggablePanel.setVisibility(View.GONE);
                break;
        }
    }


    private void initDraggableViewListener() {
        draggablePanel.setDraggableListener(new DraggableListener() {
            @Override public void onMaximized() {
                movieTrailerFragment.startVideo();
            }

            @Override public void onMinimized() {
                //Do Nothing
            }

            @Override public void onClosedToLeft() {
                movieTrailerFragment.pauseVideo();
            }

            @Override public void onClosedToRight() {
                movieTrailerFragment.pauseVideo();
            }
        });
    }

    /**
     * Keeps a reference of the last place loaded.
     *
     * @param outState Bundle used to store the position.
     */
    private void saveLastPlaceLoadedPosition(Bundle outState) {
        outState.putParcelable(LAST_LOADED_MOVIE, movie);
    }

    /**
     * Keeps a reference of the last DraggablePanelState.
     *
     * @param outState Bundle used to store the DraggablePanelState.
     */
    private void saveDraggableState(Bundle outState) {
        DraggableState draggableState = null;
        if (draggablePanel.isMaximized()) {
            draggableState = DraggableState.MAXIMIZED;
        } else if (draggablePanel.isMinimized()) {
            draggableState = DraggableState.MINIMIZED;
        } else if (draggablePanel.isClosedAtLeft()) {
            draggableState = DraggableState.CLOSED_AT_LEFT;
        } else if (draggablePanel.isClosedAtRight()) {
            draggableState = DraggableState.CLOSED_AT_RIGHT;
        }
        outState.putSerializable(DRAGGABLE_PANEL_STATE, draggableState);
    }

    /**
     * Apply the last place loaded to the different fragments showed inside the DraggablePanel..
     */
    private void loadLastPlaceClicked(Bundle savedInstanceState) {
        movie = savedInstanceState.getParcelable(LAST_LOADED_MOVIE);
        showMovieTrailerFragment(movie);
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
            movieList.add(movie);
        }
    }

    @Override
    public void onItemClick(Movie movie, View v, int adapterPosition) {
        this.movie = movie;
        showMovieTrailerFragment(movie);
    }

    private void showMovieTrailerFragment(Movie movie) {
        draggablePanel.setVisibility(View.VISIBLE);
        draggablePanel.maximize();
        movieTrailerFragment.showFragment(movie);

    }

    @Override
    protected void onStop() {
        super.onStop();
        launchPlayerService();
    }

    @Override
    public void onBackPressed() {
       if (draggablePanel.isMinimized()){
            super.onBackPressed();
        }else if (draggablePanel.isMaximized()) {
            draggablePanel.minimize();
        }
    }
}
