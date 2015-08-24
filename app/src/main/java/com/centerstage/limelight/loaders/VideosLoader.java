package com.centerstage.limelight.loaders;

import android.content.Context;

import com.centerstage.limelight.MainActivity;
import com.uwetrottmann.tmdb.entities.Videos;

/**
 * Created by Smitesh on 8/23/2015.
 * Loader for all the videos associated with a movie.
 */
public class VideosLoader extends DataLoader<Videos> {
    private int mTmdbId;

    public VideosLoader(Context context, int tmdbId) {
        super(context);
        mTmdbId = tmdbId;
    }

    @Override
    public Videos loadInBackground() {
        return MainActivity.sTmdbService.getVideos(mTmdbId);
    }
}
