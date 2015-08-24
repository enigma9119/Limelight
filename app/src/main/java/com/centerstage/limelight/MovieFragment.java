package com.centerstage.limelight;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.centerstage.limelight.data.Genre;
import com.centerstage.limelight.data.Language;
import com.centerstage.limelight.data.LimelightMovie;
import com.centerstage.limelight.loaders.ConfigurationLoader;
import com.centerstage.limelight.loaders.MovieLoader;
import com.uwetrottmann.tmdb.entities.Configuration;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.SpokenLanguage;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Fragment that shows all the main information about a particular movie.
 */
public class MovieFragment extends Fragment {

    private static final int CONFIG_LOADER = 0;
    private static final int MOVIE_LOADER = 1;

    OnMovieDataFetchedListener mCallback;

    public interface OnMovieDataFetchedListener {
        void onMovieDataFetched(LimelightMovie movie, Configuration configuration);
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

    private int mTmdbId;
    private Configuration mConfiguration;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();

        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mTmdbId = intent.getIntExtra(Intent.EXTRA_TEXT, 0);
        }

        LoaderManager lm = getLoaderManager();
        lm.initLoader(CONFIG_LOADER, null, new ConfigurationLoaderCallbacks());
        lm.initLoader(MOVIE_LOADER, null, new MovieLoaderCallbacks());
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
    private void buildMovieUI() {
        // Use a callback to handle using certain elements of the fetched data in activity
        mCallback.onMovieDataFetched(mMovie, mConfiguration);

        // Set the title and tagline
        mMovieTitle.setText(mMovie.getMovieTitle());
        if (mMovie.getTagline() != null && !mMovie.getTagline().isEmpty()) {
            mTagline.setText(mMovie.getTagline());
        } else {
            mTagline.setVisibility(View.GONE);
        }

        // Set the release date
        String formattedDate = DateUtils.formatDateTime(getActivity(), mMovie.getReleaseDate().getTime(), 0);
        mReleaseDate.setText(formattedDate);

        // Set the genres and runtime
        if (!mMovie.getGenres().isEmpty() && mMovie.getGenres().get(0) != null && !mMovie.getGenres().get(0).getName().isEmpty()) {
                if (mMovie.getGenres().size() >= 2 && mMovie.getGenres().get(1) != null && !mMovie.getGenres().get(1).getName().isEmpty()) {
                mGenres.setText(String.format("%s | %s", mMovie.getGenres().get(0).getName(), mMovie.getGenres().get(1).getName()));
            } else {
                mGenres.setText(mMovie.getGenres().get(0).getName());
            }
        }

        if (mMovie.getRuntime() != null && mMovie.getRuntime() != 0) {
            String runtimeText = String.format("%d minutes", mMovie.getRuntime());
            mRuntime.setText(runtimeText);
        }

        // Set the user rating
        if (mMovie.getUserRating() != null && mMovie.getUserRating() != 0) {
            mUserRating.setText(mMovie.getUserRating().toString());
            if (mMovie.getNumRatings() != null && mMovie.getNumRatings() != 0) {
                mUserRatingCount.setText(Integer.toString(mMovie.getNumRatings()));
            } else {
                mUserRatingCount.setVisibility(View.GONE);
                mRatingsText.setVisibility(View.GONE);
            }
        } else {
            mUserRatingCardView.setVisibility(View.GONE);
        }

        mSynopsis.setText(mMovie.getSynopsis());

        // Details card
        if (!mMovie.getLanguages().isEmpty() && mMovie.getLanguages().get(0) != null) {
            mLanguage.setText(mMovie.getLanguages().get(0).getName());
        } else {
            mLanguage.setText("-");
        }

        if (mMovie.getBudget() != null && mMovie.getBudget() != 0) {
            String budgetText = String.format("$%d", mMovie.getBudget());
            mBudget.setText(budgetText);
        } else {
            mBudget.setText("-");
        }
    }

    // Initialize the parcelable Limelight movie object
    void initLimelightMovie(Movie movie) {
        mMovie = new LimelightMovie();
        mMovie.setMovieId(movie.id);
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


    /**
     * Loader callbacks for a single movie.
     */
    public class MovieLoaderCallbacks implements LoaderManager.LoaderCallbacks<Movie> {

        @Override
        public Loader<Movie> onCreateLoader(int id, Bundle args) {
            return new MovieLoader(getActivity(), mTmdbId);
        }

        @Override
        public void onLoadFinished(Loader<Movie> loader, Movie data) {
            initLimelightMovie(data);
            if (mMovie != null && mConfiguration != null) {
                buildMovieUI();
            }
        }

        @Override
        public void onLoaderReset(Loader<Movie> loader) {

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
            if (mMovie != null && mConfiguration != null) {
                buildMovieUI();
            }
        }

        @Override
        public void onLoaderReset(Loader<Configuration> loader) {

        }
    }
}
