package com.centerstage.limelight;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Home fragment that shows movie posters.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String ARG_PAGE = "ARG_PAGE";

    public static final String KEY_SORT = "sort_mode";
    public static final int SORT_POPULAR = 0;
    public static final int SORT_RATING = 1;

    private static final int TAB1 = 1;
    private static final int TAB2 = 2;
    private static final int TAB3 = 3;

    private int mPage;
    private MovieAdapter mMovieAdapter;
    private List<Movie> mMovies;

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;

        if (Utils.isOnline(getActivity())) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.inject(this, rootView);

            // Improve performance since changes in adapter content won't change size of RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // Use a grid layout manager
            if (getActivity().getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            }

            // Specify an adapter
            mMovieAdapter = new MovieAdapter();
            mRecyclerView.setAdapter(mMovieAdapter);

            // Get the list of movies.
            // During configuration changes, the fragment will retain the mMovies object due to
            // setRetainInstance(true). If this data exists, then don't make another network call.
            // This also maintains the user's position in the RecyclerView.
            if (savedInstanceState != null && mMovies != null) {
                mMovieAdapter.updateAdapter(mMovies);
                mMovieAdapter.notifyDataSetChanged();
            } else {
                new FetchMoviesTask().execute();
            }

        } else {
            // If there is no internet connection, load a different layout
            rootView = inflater.inflate(R.layout.no_network, container, false);
        }

        return rootView;
    }


    private class FetchMoviesTask extends AsyncTask<Void, Void, MovieResultsPage> {

        @Override
        protected MovieResultsPage doInBackground(Void... params) {
            MovieResultsPage resultsPage = null;

            // Restore preferences
            SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS, 0);
            int sort_order = settings.getInt(KEY_SORT, SORT_POPULAR);

            switch(mPage) {
                case TAB1:
                    if (sort_order == SORT_POPULAR)
                        resultsPage = MainActivity.sTmdbService.getPopular(1);
                    else
                        resultsPage = MainActivity.sTmdbService.getTopRated(1);
                    break;
                case TAB2: resultsPage = MainActivity.sTmdbService.getNowPlaying(1); break;
                case TAB3: resultsPage = MainActivity.sTmdbService.getUpcoming(1); break;
                default: Log.e(TAG, String.format("Invalid page %d returned by MoviesPagerAdapter", mPage));
            }

            return resultsPage;
        }

        @Override
        protected void onPostExecute(MovieResultsPage movieResultsPage) {
            if (movieResultsPage != null && !movieResultsPage.results.isEmpty()) {
                mMovies = movieResultsPage.results;
                mMovieAdapter.updateAdapter(mMovies);
                mMovieAdapter.notifyDataSetChanged();
            }
        }
    }
}
