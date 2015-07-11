package com.centerstage.limelight;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uwetrottmann.tmdb.entities.Configuration;
import com.uwetrottmann.tmdb.entities.Movie;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Smitesh on 7/8/2015.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie> mMovies;
    private Configuration mConfig;

    public static final Float PLACEHOLDER_TEXT_SIZE = 40f;

    public void setConfiguration(Configuration configuration) {
        mConfig = configuration;
    }

    public void updateAdapter(List<Movie> movies) {
        mMovies = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        Context context = holder.mMoviePoster.getContext();

        // Purpose of configuration data is to load common elements that don't change frequently separately
        // to keep the actual API responses as light as possible
        String complete_poster_path = mConfig.images.base_url + mConfig.images.poster_sizes.get(3) + movie.poster_path;  // Todo: try size 2 and 3
        BitmapDrawable placeholderText = Utils.textAsBitmapDrawable(context, movie.original_title, PLACEHOLDER_TEXT_SIZE, Color.BLACK,  // Todo: title vs original_title
                holder.mMoviePoster.getMeasuredWidth(), holder.mMoviePoster.getMeasuredHeight());

        Picasso.with(context).load(complete_poster_path)
                .placeholder(placeholderText)
                .into(holder.mMoviePoster);
    }

    @Override
    public int getItemCount() {
        if (mMovies != null)
            return mMovies.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.movie_poster)
        ImageView mMoviePoster;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
