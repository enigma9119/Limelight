package com.centerstage.limelight;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uwetrottmann.tmdb.entities.Movie;


/**
 * Fragment that shows all the main information about a particular movie.
 */
public class MovieFragment extends Fragment {

    public MovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
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
            Toast.makeText(getActivity().getApplicationContext(), movie.original_title, Toast.LENGTH_SHORT).show();
        }
    }
}
