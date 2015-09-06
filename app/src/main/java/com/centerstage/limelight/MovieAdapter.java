package com.centerstage.limelight;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.centerstage.limelight.data.LimelightMovie;
import com.squareup.picasso.Picasso;
import com.uwetrottmann.tmdb.entities.Configuration;
import com.uwetrottmann.tmdb.entities.Movie;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Smitesh on 7/8/2015.
 * Adapter to populate the grid view on the home page.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie> mMovies;
    private ArrayList<LimelightMovie> mLimelightMovies;
    private Configuration mConfig;

    public static final Float PLACEHOLDER_TEXT_SIZE = 40f;

    public void updateAdapter(List<Movie> movies) {
        mMovies = movies;
    }

    public MovieAdapter(List<Movie> movies, Configuration configuration) {
        mMovies = movies;
        mConfig = configuration;
    }

    public MovieAdapter(ArrayList<LimelightMovie> movies) {
        mLimelightMovies = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // If data passed to adapter contains Movie objects, convert to parcelable LimelightMovie.
        // If data passed to adapter contains LimelightMovie objects, directly use it.
        LimelightMovie temp;
        if (mMovies != null) {
            Movie movie = mMovies.get(position);
            temp = new LimelightMovie();

            // Initialize only the values that are needed
            String completePosterPath = mConfig.images.base_url + mConfig.images.poster_sizes.get(3) + movie.poster_path;
            temp.setMovieId(movie.id);
            temp.setMovieTitle(movie.original_title);
            temp.setPosterPath(completePosterPath);
        } else {
            temp = mLimelightMovies.get(position);
        }
        final LimelightMovie limelightMovie = temp;
        final Context context = holder.mMoviePoster.getContext();

        BitmapDrawable placeholderText = Utils.textAsBitmapDrawable(context, limelightMovie.getMovieTitle(),
                PLACEHOLDER_TEXT_SIZE, Color.BLACK,
                holder.mMoviePoster.getMeasuredWidth(), holder.mMoviePoster.getMeasuredHeight());

        Picasso.with(context).load(limelightMovie.getPosterPath())
                .placeholder(placeholderText)
                .into(holder.mMoviePoster);

        holder.mMoviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only handle the click if movie poster has finished loading
                if (holder.mMoviePoster.getDrawable() != null) {
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);

                    // If movie data is coming from the database, pass the parcelable movie as an extra.
                    // If movie data needs to be loaded in MovieFragment, pass the movie id as an extra.
                    if (mLimelightMovies != null) {
                        intent.putExtra(DetailActivity.PARCELABLE_MOVIE_EXTRA, limelightMovie);
                    } else {
                        intent.putExtra(Intent.EXTRA_TEXT, limelightMovie.getMovieId());
                    }

                    if (limelightMovie.getPosterPath() != null) {
                        // Convert ImageView (movie poster) to byte array
                        Bitmap posterImageBitmap = ((BitmapDrawable) holder.mMoviePoster.getDrawable()).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        posterImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        // Pass this byte array into the intent
                        intent.putExtra(Intent.EXTRA_STREAM, byteArray);
                    }

                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mMovies != null)
            return mMovies.size();
        else if (mLimelightMovies != null)
            return mLimelightMovies.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.movie_poster)
        ImageView mMoviePoster;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
