package com.centerstage.limelight;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centerstage.limelight.loaders.MovieResultsPageLoader;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Home fragment that shows movie posters.
 */
public class HomeFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";

    public static final String KEY_SORT = "sort_mode";
    public static final int SORT_POPULAR = 0;
    public static final int SORT_RATING = 1;

    private static final int MOVIE_DATA_LOADER = 0;

    private int mTabPage;
    private MovieAdapter mMovieAdapter;
    private List<Movie> mMovies;

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    LoaderManager lm;
    MovieResultsPageLoader mMovieResultsPageLoader;

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
        mTabPage = getArguments().getInt(ARG_PAGE);
        setRetainInstance(true);

        lm = getLoaderManager();
        if (lm.getLoader(MOVIE_DATA_LOADER) != null) {
            lm.initLoader(MOVIE_DATA_LOADER, null, new MovieDataLoaderCallbacks());
        }
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
                lm.restartLoader(MOVIE_DATA_LOADER, null, new MovieDataLoaderCallbacks());
            }

        } else {
            // If there is no internet connection, load a different layout
            rootView = inflater.inflate(R.layout.no_network, container, false);
        }

        return rootView;
    }


    /**
     * Loader callbacks for movie results pages.
     */
    public class MovieDataLoaderCallbacks implements LoaderManager.LoaderCallbacks<MovieResultsPage> {

        @Override
        public Loader<MovieResultsPage> onCreateLoader(int id, Bundle args) {
            mMovieResultsPageLoader = new MovieResultsPageLoader(getActivity(), mTabPage);
            return mMovieResultsPageLoader;
        }

        @Override
        public void onLoadFinished(Loader<MovieResultsPage> loader, MovieResultsPage data) {
            if (data != null && !data.results.isEmpty()) {
                mMovies = data.results;
                mMovieAdapter.updateAdapter(mMovies);
                mMovieAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLoaderReset(Loader<MovieResultsPage> loader) {

        }
    }
}
