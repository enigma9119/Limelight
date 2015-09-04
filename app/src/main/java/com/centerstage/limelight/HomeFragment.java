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

import com.centerstage.limelight.loaders.ConfigurationLoader;
import com.centerstage.limelight.loaders.MovieResultsPageLoader;
import com.uwetrottmann.tmdb.entities.Configuration;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Inner Home fragment that shows a list of movie posters.
 */
public class HomeFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";

    public static final String KEY_SORT = "sort_mode";
    public static final int SORT_POPULAR = 0;
    public static final int SORT_RATING = 1;

    private static final int CONFIG_LOADER = 0;
    private static final int MOVIE_DATA_LOADER = 1;

    private int mTabPage;
    private List<Movie> mMovies;
    private Configuration mConfiguration;

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;

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
        mMovies = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Utils.isOnline(getActivity())) {
            LoaderManager lm = getLoaderManager();
            lm.initLoader(CONFIG_LOADER, null, new ConfigurationLoaderCallbacks());
            lm.initLoader(MOVIE_DATA_LOADER, null, new MovieDataLoaderCallbacks());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;

        if (Utils.isOnline(getActivity())) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            ButterKnife.inject(this, rootView);

            // Restart loader so that when sort options are changed, new data is loaded
            getLoaderManager().restartLoader(MOVIE_DATA_LOADER, null, new MovieDataLoaderCallbacks());

            // Improve performance since changes in adapter content won't change size of RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // Use a grid layout manager
            if (getActivity().getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                mLayoutManager = new GridLayoutManager(getActivity(), 2);
            } else {
                mLayoutManager = new GridLayoutManager(getActivity(), 3);
            }
            mRecyclerView.setLayoutManager(mLayoutManager);

        } else {
            // If there is no internet connection, load a different layout
            rootView = inflater.inflate(R.layout.no_network, container, false);
        }

        return rootView;
    }

    public void setupAdapter() {
        if (getActivity() == null || mRecyclerView == null) return;

        if (!mMovies.isEmpty() && mConfiguration != null) {
            if (mRecyclerView.getAdapter() == null) {
                mRecyclerView.setAdapter(new MovieAdapter(mMovies, mConfiguration));
            } else {
                MovieAdapter adapter = (MovieAdapter) mRecyclerView.getAdapter();
                adapter.updateAdapter(mMovies);
                adapter.notifyDataSetChanged();
            }
        } else {
            mRecyclerView.setAdapter(null);
        }
    }


    /**
     * Loader callbacks for movie results pages.
     */
    public class MovieDataLoaderCallbacks implements LoaderManager.LoaderCallbacks<MovieResultsPage> {

        @Override
        public Loader<MovieResultsPage> onCreateLoader(int id, Bundle args) {
            return new MovieResultsPageLoader(getActivity(), mTabPage);
        }

        @Override
        public void onLoadFinished(Loader<MovieResultsPage> loader, MovieResultsPage data) {
            if (data != null && !data.results.isEmpty()) {
                mMovies = data.results;
                setupAdapter();
            }
        }

        @Override
        public void onLoaderReset(Loader<MovieResultsPage> loader) {

        }
    }

    /**
     * Loader callbacks for tmdb configuration data
     */
    public class ConfigurationLoaderCallbacks implements LoaderManager.LoaderCallbacks<Configuration> {

        @Override
        public Loader<Configuration> onCreateLoader(int id, Bundle args) {
            return new ConfigurationLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<Configuration> loader, Configuration data) {
            mConfiguration = data;
            setupAdapter();
        }

        @Override
        public void onLoaderReset(Loader<Configuration> loader) {

        }
    }
}
