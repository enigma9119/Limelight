package com.centerstage.limelight.loaders;

import android.content.Context;

import com.centerstage.limelight.MainActivity;
import com.uwetrottmann.tmdb.entities.ReviewResultsPage;

/**
 * Created by Smitesh on 8/29/2015.
 * Loader for movie reviews.
 */
public class ReviewsLoader extends DataLoader<ReviewResultsPage> {
    private static int mPageNumber;
    private int mTmdbId;

    public ReviewsLoader(Context context, int tmdbId) {
        super(context);
        mTmdbId = tmdbId;
        mPageNumber = 1;
    }

    public static void setPageNumber(int pageNumber) {
        mPageNumber = pageNumber;
    }

    @Override
    public ReviewResultsPage loadInBackground() {
        return MainActivity.sTmdbService.getReviews(mTmdbId, mPageNumber);
    }
}
