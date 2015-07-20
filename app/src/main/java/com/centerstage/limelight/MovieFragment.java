package com.centerstage.limelight;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uwetrottmann.tmdb.entities.Movie;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Fragment that shows all the main information about a particular movie.
 */
public class MovieFragment extends Fragment {

    OnMovieDataFetchedListener mCallback;

    public interface OnMovieDataFetchedListener {
        void onMovieDataFetched(Movie movie);
    }

    @InjectView(R.id.movie_title)
    TextView mMovieTitle;
    @InjectView(R.id.tagline)
    TextView mTagline;

    @InjectView(R.id.release_date)
    TextView mReleaseDate;

    @InjectView(R.id.genre_runtime_cardview)
    CardView mGenreRuntimeCardView;
    @InjectView(R.id.genres)
    TextView mGenres;
    @InjectView(R.id.runtime)
    TextView mRuntime;

    @InjectView(R.id.star)
    ImageView mStar;
    @InjectView(R.id.user_rating_cardview)
    CardView mUserRatingCardView;
    @InjectView(R.id.user_rating)
    TextView mUserRating;
    @InjectView(R.id.user_rating_count)
    TextView mUserRatingCount;
    @InjectView(R.id.max_rating_text)
    TextView mMaxRatingText;
    @InjectView(R.id.ratings_text)
    TextView mRatingsText;

    @InjectView(R.id.synopsis)
    TextView mSynopsis;
    @InjectView(R.id.language)
    TextView mLanguage;
    @InjectView(R.id.budget)
    TextView mBudget;

    Movie mMovie;

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

    // Callback function to use when color palette has been generated using the backdrop image
    public void onPaletteGenerated(Palette palette) {
        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
        Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();

        Drawable starDrawable = getResources().getDrawable(R.drawable.ic_star_black_36dp);

        if (vibrantSwatch != null) {
            mUserRatingCardView.setCardBackgroundColor(palette.getVibrantColor(R.attr.colorPrimary));
            mUserRating.setTextColor(vibrantSwatch.getBodyTextColor());
            mMaxRatingText.setTextColor(vibrantSwatch.getBodyTextColor());
            mUserRatingCount.setTextColor(vibrantSwatch.getBodyTextColor());
            mRatingsText.setTextColor(vibrantSwatch.getBodyTextColor());

            starDrawable.setColorFilter(vibrantSwatch.getBodyTextColor(), PorterDuff.Mode.SRC_IN);
        }
        mStar.setImageDrawable(starDrawable);

        // Genres and runtime colors
        if (darkVibrantSwatch != null) {
            mGenreRuntimeCardView.setBackgroundColor(palette.getDarkVibrantColor(R.attr.colorPrimaryDark));
            mGenres.setTextColor(darkVibrantSwatch.getBodyTextColor());
            mRuntime.setTextColor(darkVibrantSwatch.getBodyTextColor());
        }
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

            // Set the title and tagline
            mMovieTitle.setText(movie.original_title);
            if (!movie.tagline.isEmpty()) {
                mTagline.setText(movie.tagline);
            } else {
                mTagline.setVisibility(View.GONE);
            }

            // Set the release date
            String formattedDate = DateUtils.formatDateTime(getActivity(), movie.release_date.getTime(), 0);
            mReleaseDate.setText(formattedDate);

            // Set the genres and runtime
            if (!movie.genres.isEmpty() && movie.genres.get(0) != null && !movie.genres.get(0).name.isEmpty()) {
                if (movie.genres.size() >= 2 && movie.genres.get(1) != null && !movie.genres.get(1).name.isEmpty()) {
                    mGenres.setText(String.format("%s | %s", movie.genres.get(0).name, movie.genres.get(1).name));
                } else {
                    mGenres.setText(movie.genres.get(0).name);
                }
            }

            if (movie.runtime != null && movie.runtime != 0) {
                String runtimeText = String.format("%d minutes", movie.runtime);
                mRuntime.setText(runtimeText);
            }

            // Set the user rating
            if (movie.vote_average != 0) {
                mUserRating.setText(movie.vote_average.toString());
                if (movie.vote_count != null && movie.vote_count != 0) {
                    mUserRatingCount.setText(Integer.toString(movie.vote_count));
                } else {
                    mUserRatingCount.setVisibility(View.GONE);
                    mRatingsText.setVisibility(View.GONE);
                }
            } else {
                mUserRatingCardView.setVisibility(View.GONE);
            }

            mSynopsis.setText(movie.overview);

            // Details card
            if (!movie.spoken_languages.isEmpty() && movie.spoken_languages.get(0) != null) {
                mLanguage.setText(movie.spoken_languages.get(0).name);
            } else {
                mLanguage.setText("-");
            }

            if (movie.budget != null && movie.budget != 0) {
                String budgetText = String.format("$%d", movie.budget);
                mBudget.setText(movie.budget != 0 ? budgetText : "N/A");
            } else {
                mBudget.setText("-");
            }
        }
    }
}
