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

import com.squareup.picasso.Picasso;
import com.uwetrottmann.tmdb.entities.Movie;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Smitesh on 7/8/2015.
 * Adapter to populate the grid view on the home page.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie> mMovies;

    public static final Float PLACEHOLDER_TEXT_SIZE = 40f;

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Movie movie = mMovies.get(position);
        final Context context = holder.mMoviePoster.getContext();

        // Purpose of configuration data is to load common elements that don't change frequently separately
        // to keep the actual API responses as light as possible
        String complete_poster_path = MainActivity.sConfiguration.images.base_url +
                MainActivity.sConfiguration.images.poster_sizes.get(3) +
                movie.poster_path;
        BitmapDrawable placeholderText = Utils.textAsBitmapDrawable(context, movie.original_title,
                PLACEHOLDER_TEXT_SIZE, Color.BLACK,
                holder.mMoviePoster.getMeasuredWidth(), holder.mMoviePoster.getMeasuredHeight());

        Picasso.with(context).load(complete_poster_path)
                .placeholder(placeholderText)
                .into(holder.mMoviePoster);

        holder.mMoviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only handle the click if movie poster has finished loading
                if (holder.mMoviePoster.getDrawable() != null) {
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT, movie.id);

                    if (movie.poster_path != null) {
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
