package com.sublime.dragplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sublime.dragplayer.model.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by goonerdroid
 * on 22/12/17.
 */

public class MovieTrailerFragment extends Fragment {

    @BindView(R.id.iv_thumb)
    ImageView movieBannerImage;
    @BindView(R.id.tv_title)
    TextView movieTitle;
    @BindView(R.id.tv_genre)
    TextView movieGenre;
    @BindView(R.id.tv_year)
    TextView movieYear;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_trailer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void showFragment(Movie movie){
        movieTitle.setText(movie.getMovieName());
        movieGenre.setText(movie.getMovieGenre());
        movieYear.setText(movie.getMovieYear());
        Glide.with(this).load(movie.getMovieThumbnail()).into(movieBannerImage);
    }

}
