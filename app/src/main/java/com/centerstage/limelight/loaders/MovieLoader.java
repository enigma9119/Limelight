package com.centerstage.limelight.loaders;

import android.content.Context;

import com.centerstage.limelight.MainActivity;
import com.uwetrottmann.tmdb.entities.Movie;

/**
 * Created by Smitesh on 8/22/2015.
 * Loader for a single movie.
 */
public class MovieLoader extends DataLoader<Movie> {
    private int mTmdbId;

    public MovieLoader(Context context, int tmdbId) {
        super(context);
        mTmdbId = tmdbId;
    }

    @Override
    public Movie loadInBackground() {
        return MainActivity.sTmdbService.getMovie(mTmdbId);
    }
}
