package com.sublime.dragplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.sublime.dragplayer.R;
import com.sublime.dragplayer.model.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by goonerdroid
 * on 22/12/17.
 */

public class MovieAdapter extends  RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private RequestManager requestManager;
    private List<Movie> movieDataList;
    private OnItemClickListener onItemClickListener;

    public MovieAdapter(RequestManager glideRequestManager, List<Movie> movieList) {
        requestManager = glideRequestManager;
        movieDataList = movieList;
    }



    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder holder, int position) {
        Movie movie = movieDataList.get(position % movieDataList.size());
        holder.movieTitle.setText(movie.getMovieName());
        holder.movieGenre.setText(movie.getMovieGenre());
        holder.movieYear.setText(movie.getMovieYear());


        requestManager.load(movie.getMovieThumbnail()).into(holder.movieThumbImage);
        holder.bindClickListener(movie,holder.viewContainer);
    }

    @Override
    public int getItemCount() {
        return movieDataList.size() * 2;
    }

    //////////////Start of listeners///////////
    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    /**
     * Removes the listener for avoiding memory leaks.
     */
    public void removeItemClickListener(){
        this.onItemClickListener = null;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_item_container)
        LinearLayout viewContainer;
        @BindView(R.id.thumbnail)
        ImageView movieThumbImage;
        @BindView(R.id.title)
        TextView movieTitle;
        @BindView(R.id.genre)
        TextView movieGenre;
        @BindView(R.id.year)
        TextView movieYear;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindClickListener(final Movie movie, View view) {
            viewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(movie,view,getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Movie movie,View v, int adapterPosition);
    }
}
