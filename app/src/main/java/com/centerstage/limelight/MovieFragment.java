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

import com.centerstage.limelight.model.Genre;
import com.centerstage.limelight.model.Language;
import com.centerstage.limelight.model.LimelightMovie;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.SpokenLanguage;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Fragment that shows all the main information about a particular movie.
 */
public class MovieFragment extends Fragment {

    private final String PARCELABLE_MOVIE_KEY = "parcelable_movie";

    OnMovieDataFetchedListener mCallback;

    public interface OnMovieDataFetchedListener {
        void onMovieDataFetched(LimelightMovie movie);
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

    LimelightMovie mMovie;

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PARCELABLE_MOVIE_KEY, mMovie);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();

        if (savedInstanceState != null) {
            // Use saved movie object to build UI
            mMovie = savedInstanceState.getParcelable(PARCELABLE_MOVIE_KEY);
            buildMovieUI(mMovie);

        } else if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            int tmdbId = intent.getIntExtra(Intent.EXTRA_TEXT, 0);
            new FetchMovieTask().execute(tmdbId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.inject(this, rootView);

        return rootView;
    }

    // Callback function to use when color palette has been generated using the backdrop image
    public void onPaletteGenerated(Palette palette) {
        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
        Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();

        Drawable starDrawable = getResources().getDrawable(R.drawable.ic_star_black_36dp);

        // User rating colors
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

    // Build the main UI for the detail screen
    private void buildMovieUI(LimelightMovie movie) {
        // Use a callback to handle using certain elements of the fetched data in activity
        mCallback.onMovieDataFetched(movie);

        // Set the title and tagline
        mMovieTitle.setText(movie.getMovieTitle());
        if (movie.getTagline() != null && !movie.getTagline().isEmpty()) {
            mTagline.setText(movie.getTagline());
        } else {
            mTagline.setVisibility(View.GONE);
        }

        // Set the release date
        String formattedDate = DateUtils.formatDateTime(getActivity(), movie.getReleaseDate().getTime(), 0);
        mReleaseDate.setText(formattedDate);

        // Set the genres and runtime
        if (!movie.getGenres().isEmpty() && movie.getGenres().get(0) != null && !movie.getGenres().get(0).getName().isEmpty()) {
                if (movie.getGenres().size() >= 2 && movie.getGenres().get(1) != null && !movie.getGenres().get(1).getName().isEmpty()) {
                mGenres.setText(String.format("%s | %s", movie.getGenres().get(0).getName(), movie.getGenres().get(1).getName()));
            } else {
                mGenres.setText(movie.getGenres().get(0).getName());
            }
        }

        if (movie.getRuntime() != null && movie.getRuntime() != 0) {
            String runtimeText = String.format("%d minutes", movie.getRuntime());
            mRuntime.setText(runtimeText);
        }

        // Set the user rating
        if (movie.getUserRating() != null && movie.getUserRating() != 0) {
            mUserRating.setText(movie.getUserRating().toString());
            if (movie.getNumRatings() != null && movie.getNumRatings() != 0) {
                mUserRatingCount.setText(Integer.toString(movie.getNumRatings()));
            } else {
                mUserRatingCount.setVisibility(View.GONE);
                mRatingsText.setVisibility(View.GONE);
            }
        } else {
            mUserRatingCardView.setVisibility(View.GONE);
        }

        mSynopsis.setText(movie.getSynopsis());

        // Details card
        if (!movie.getLanguages().isEmpty() && movie.getLanguages().get(0) != null) {
            mLanguage.setText(movie.getLanguages().get(0).getName());
        } else {
            mLanguage.setText("-");
        }

        if (movie.getBudget() != null && movie.getBudget() != 0) {
            String budgetText = String.format("$%d", movie.getBudget());
            mBudget.setText(budgetText);
        } else {
            mBudget.setText("-");
        }
    }

    // Initialize the parcelable Limelight movie object
    void initLimelightMovie(Movie movie) {
        mMovie = new LimelightMovie();
        mMovie.setId(movie.id);
        mMovie.setMovieTitle(movie.original_title);
        mMovie.setTagline(movie.tagline);
        mMovie.setReleaseDate(movie.release_date);

        if (movie.genres != null) {
            ArrayList<Genre> parcelableGenres = new ArrayList<>(movie.genres.size());

            for (com.uwetrottmann.tmdb.entities.Genre genre : movie.genres) {
                Genre parcelableGenre = new Genre();
                parcelableGenre.setId(genre.id);
                parcelableGenre.setName(genre.name);

                parcelableGenres.add(parcelableGenre);
            }

            mMovie.setGenres(parcelableGenres);
        }

        mMovie.setRuntime(movie.runtime);
        mMovie.setUserRating(movie.vote_average);
        mMovie.setNumRatings(movie.vote_count);
        mMovie.setSynopsis(movie.overview);

        if (movie.spoken_languages != null) {
            ArrayList<Language> languages = new ArrayList<>(movie.spoken_languages.size());

            for (SpokenLanguage spokenLanguage : movie.spoken_languages) {
                Language language = new Language();
                language.setIso_639_1(spokenLanguage.iso_639_1);
                language.setName(spokenLanguage.name);

                languages.add(language);
            }

            mMovie.setLanguages(languages);
        }

        mMovie.setBudget(movie.budget);
        mMovie.setBackdropPath(movie.backdrop_path);
    }


    private class FetchMovieTask extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected Movie doInBackground(Integer... params) {
            return MainActivity.sTmdbService.getMovie(params[0]);
        }

        @Override
        protected void onPostExecute(Movie movie) {
            initLimelightMovie(movie);
            buildMovieUI(mMovie);
        }
    }
}
