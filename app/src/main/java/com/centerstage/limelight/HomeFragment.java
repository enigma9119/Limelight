package com.centerstage.limelight;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uwetrottmann.tmdb.entities.Configuration;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Home fragment that shows movie posters.
 */
public class HomeFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final int TAB1 = 1;
    private static final int TAB2 = 2;
    private static final int TAB3 = 3;

    private int mPage;
    private MovieAdapter mMovieAdapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
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

        // Get the common configuration data
        FetchConfigurationTask configTask = new FetchConfigurationTask();
        configTask.execute();

        // Get the list of movies
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();

        return rootView;
    }

    private class FetchMoviesTask extends AsyncTask<Void, Void, MovieResultsPage> {

        @Override
        protected MovieResultsPage doInBackground(Void... params) {
            MovieResultsPage resultsPage = null;

            switch(mPage) {
                case TAB1: resultsPage = MainActivity.sTmdbService.getPopular(1); break;
                case TAB2: resultsPage = MainActivity.sTmdbService.getNowPlaying(1); break;
                case TAB3: resultsPage = MainActivity.sTmdbService.getUpcoming(1); break;
                default: Log.e(TAG, String.format("Invalid page %d returned by MoviesPagerAdapter", mPage));
            }

            return resultsPage;
        }

        @Override
        protected void onPostExecute(MovieResultsPage movieResultsPage) {
            if (movieResultsPage != null && !movieResultsPage.results.isEmpty()) {
                mMovieAdapter.updateAdapter(movieResultsPage.results);
                mMovieAdapter.notifyDataSetChanged();
            }
        }
    }

    private class FetchConfigurationTask extends AsyncTask<Void, Void, Configuration> {

        @Override
        protected Configuration doInBackground(Void... params) {
            return MainActivity.sTmdbService.getConfiguration();
        }

        @Override
        protected void onPostExecute(Configuration configuration) {
            mMovieAdapter.setConfiguration(configuration);
        }
    }
}
