package com.centerstage.limelight;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uwetrottmann.tmdb.entities.Movie;

import butterknife.ButterKnife;


/**
 * Fragment that shows all the main information about a particular movie.
 */
public class MovieFragment extends Fragment {

    OnMovieDataFetchedListener mCallback;

    public interface OnMovieDataFetchedListener {
        void onMovieDataFetched(Movie movie);
    }

    public MovieFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMovieDataFetchedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieDataFetchedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.inject(this, rootView);
        Intent intent = getActivity().getIntent();

        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            int tmdbId = intent.getIntExtra(Intent.EXTRA_TEXT, 0);
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(tmdbId);
        }

        return rootView;
    }


    private class FetchMovieTask extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected Movie doInBackground(Integer... params) {
            return MainActivity.sTmdbService.getMovie(params[0]);
        }

        @Override
        protected void onPostExecute(Movie movie) {
            // Use a callback to handle using certain elements of the fetched data in activity
            mCallback.onMovieDataFetched(movie);
        }
    }
}
