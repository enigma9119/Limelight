package com.centerstage.limelight.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.centerstage.limelight.HomeFragment;
import com.centerstage.limelight.MainActivity;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

/**
 * Created by Smitesh on 8/22/2015.
 * Loader for movie results pages.
 */
public class MovieResultsPageLoader extends DataLoader<MovieResultsPage> {

    private static final String TAG = MovieResultsPageLoader.class.getSimpleName();

    private static int mPageNumber;
    private int mTabPage;

    public MovieResultsPageLoader(Context context, int tabPage) {
        super(context);
        mTabPage = tabPage;
        mPageNumber = 1;
    }

    public static void setPageNumber(int pageNumber) {
        mPageNumber = pageNumber;
    }

    @Override
    public MovieResultsPage loadInBackground() {
        MovieResultsPage resultsPage = null;
        final int TAB1 = 1;
        final int TAB2 = 2;
        final int TAB3 = 3;

        // Restore preferences
        SharedPreferences settings = getContext().getSharedPreferences(MainActivity.PREFS, 0);
        int sort_order = settings.getInt(HomeFragment.KEY_SORT, HomeFragment.SORT_POPULAR);

        switch(mTabPage) {
            case TAB1:
                if (sort_order == HomeFragment.SORT_POPULAR)
                    resultsPage = MainActivity.sTmdbService.getPopular(mPageNumber);
                else
                    resultsPage = MainActivity.sTmdbService.getTopRated(mPageNumber);
                break;
            case TAB2: resultsPage = MainActivity.sTmdbService.getNowPlaying(mPageNumber); break;
            case TAB3: resultsPage = MainActivity.sTmdbService.getUpcoming(mPageNumber); break;
            default: Log.e(TAG, String.format("Invalid page %d returned by MoviesPagerAdapter", mTabPage));
        }

        return resultsPage;
    }
}
