package com.sublime.dragplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sublime.dragplayer.adapter.MovieAdapter;
import com.sublime.dragplayer.model.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView movieRecyclerView;

    private ArrayList<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        if (getSupportActionBar() != null)getSupportActionBar().setTitle("Al Pacino Club");
        initMovieRecyclerView();
    }

    private void initMovieRecyclerView() {
        movieList = new ArrayList<>();
        MovieAdapter movieAdapter = new MovieAdapter(Glide.with(this), movieList);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        movieRecyclerView.setLayoutManager(mLayoutManager);
        movieRecyclerView.setItemAnimator(new DefaultItemAnimator());
        movieRecyclerView.setAdapter(movieAdapter);

        initMovieData();
    }

    private void initMovieData() {
        int movieThumbArray[] = new int[]{R.drawable.donnie_brasco,R.drawable.godfather,
                R.drawable.scarface,R.drawable.scent_of_a_woman,R.drawable.serpico};
        String movieTitleArray[] = new String[]{"Donnie Brasco","Godfather","Scarface","Scent of a Woman",
                "Serpico"};

        String movieYearArray[] = new String[]{"1997","1972","1983","1992","1973"};

        String movieGenreArray[] = new String[]{"Crime film/Thriller","Drama/Crime","Thriller/Action",
                "Coming of age/Drama","Crime film/Thriller"};

        for (int i = 0 ; i < movieTitleArray.length ; i++){
            Movie movie = new Movie();
            movie.setMovieName(movieTitleArray[i]);
            movie.setMovieThumbnail(movieThumbArray[i]);
            movie.setMovieYear(movieYearArray[i]);
            movie.setMovieGenre(movieGenreArray[i]);
            movieList.add(movie);
        }
    }
}
