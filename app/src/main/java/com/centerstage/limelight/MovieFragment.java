package com.centerstage.limelight;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.centerstage.limelight.data.LimelightMovie;
import com.centerstage.limelight.data.MovieProvider;
import com.centerstage.limelight.data.ParcelableReview;
import com.centerstage.limelight.loaders.ConfigurationLoader;
import com.centerstage.limelight.loaders.MovieLoader;
import com.centerstage.limelight.loaders.ReviewsLoader;
import com.centerstage.limelight.loaders.VideosLoader;
import com.uwetrottmann.tmdb.entities.Configuration;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.Review;
import com.uwetrottmann.tmdb.entities.ReviewResultsPage;
import com.uwetrottmann.tmdb.entities.Videos;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.littlerobots.cupboard.tools.provider.UriHelper;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


/**
 * Fragment that shows all the main information about a particular movie.
 */
public class MovieFragment extends Fragment {

    private static final int CONFIG_LOADER = 0;
    private static final int MOVIE_LOADER = 1;
    public static final int VIDEOS_LOADER = 2;
    private static final int REVIEWS_LOADER = 3;

    OnMovieDataFetchedListener mCallback;

    public interface OnMovieDataFetchedListener {
        void onMovieDataFetched(LimelightMovie movie);
    }

    @InjectView(R.id.movie_title)
    TextView mMovieTitle;
    @InjectView(R.id.tagline)
    TextView mTagline;

    @InjectView(R.id.favorites_cardview)
    CardView mFavoritesCardView;
    @InjectView(R.id.favorites)
    TextView mFavorites;

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

    @InjectView(R.id.reviews_cardview)
    CardView mReviewsCardView;
    @InjectView(R.id.author_text)
    TextView mAuthorText;
    @InjectView(R.id.author)
    TextView mReviewAuthor;
    @InjectView(R.id.review)
    TextView mReview;
    @InjectView(R.id.more_text)
    TextView mMoreText;

    Palette mPalette;
    private int mTmdbId;
    private Configuration mConfiguration;
    LimelightMovie mLimelightMovie;
    Movie mMovie;
    Videos mVideos;
    ArrayList<ParcelableReview> mReviews;

    Uri mMovieUri;

    public MovieFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Get the Content Uri for the LimelightMovie table in the database
        UriHelper helper = UriHelper.with(MovieProvider.CONTENT_AUTHORITY);
        mMovieUri = helper.getUri(LimelightMovie.class);

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
        LoaderManager lm = getLoaderManager();

        if(intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {

                Long id = intent.getLongExtra(Intent.EXTRA_TEXT, 0);
                mTmdbId = id.intValue();

                lm.initLoader(CONFIG_LOADER, null, new ConfigurationLoaderCallbacks());
                lm.initLoader(MOVIE_LOADER, null, new MovieLoaderCallbacks());
                lm.initLoader(VIDEOS_LOADER, null, new VideosLoaderCallbacks());
                lm.initLoader(REVIEWS_LOADER, null, new ReviewsLoaderCallbacks());

                // If the movie exists in the database, it means it was added to favorites.
                LimelightMovie movie = cupboard().withContext(getActivity()).get(ContentUris.withAppendedId(mMovieUri, id), LimelightMovie.class);
                if (movie != null) {
                    Drawable favoritesDrawable = getResources().getDrawable(R.drawable.ic_favorite_red_500_24dp);
                    mFavorites.setCompoundDrawablesWithIntrinsicBounds(favoritesDrawable, null, null, null);
                    mFavorites.setTag("favorites");
                }

            } else if (intent.hasExtra(DetailActivity.PARCELABLE_MOVIE_EXTRA)) {

                mLimelightMovie = intent.getParcelableExtra(DetailActivity.PARCELABLE_MOVIE_EXTRA);
                buildMovieUI();

                // These movies are already in the database, so initialize with favorites drawable filled in
                Drawable favoritesDrawable = getResources().getDrawable(R.drawable.ic_favorite_red_500_24dp);
                mFavorites.setCompoundDrawablesWithIntrinsicBounds(favoritesDrawable, null, null, null);
                mFavorites.setTag("favorites");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.inject(this, rootView);

        mReviewsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLimelightMovie != null && mPalette != null) {
                    Intent intent = new Intent(getActivity(), ReviewActivity.class);

                    intent.putParcelableArrayListExtra(ReviewActivity.REVIEWS_EXTRA, mReviews);
                    intent.putExtra(ReviewActivity.MOVIE_TITLE_EXTRA, mLimelightMovie.getMovieTitle());
                    intent.putExtra(ReviewActivity.VIBRANT_COLOR_EXTRA, mPalette.getVibrantColor(R.attr.colorPrimary));
                    intent.putExtra(ReviewActivity.DARK_VIBRANT_COLOR_EXTRA, mPalette.getDarkVibrantColor(R.attr.colorPrimaryDark));

                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    // Callback function to use when color palette has been generated using the backdrop image
    public void onPaletteGenerated(Palette palette) {
        mPalette = palette;

        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
        Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();

        // If fragment is detached from activity, the activity was probably destroyed and any use
        // of activity resources will cause the app to crash. Hence, return and do nothing.
        if (!isAdded()) return;

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

        // Favorites button
        mFavoritesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable favoritesOutlineDrawable = getResources().getDrawable(R.drawable.ic_favorite_outline_black_24dp);
                Drawable favoritesDrawable = getResources().getDrawable(R.drawable.ic_favorite_red_500_24dp);

                if (mFavorites.getTag().equals("favoritesOutline")) {
                    mFavorites.setCompoundDrawablesWithIntrinsicBounds(favoritesDrawable, null, null, null);
                    mFavorites.setTag("favorites");

                    // Insert this movie into the database
                    cupboard().withContext(getActivity()).put(mMovieUri, mLimelightMovie);
                } else {
                    mFavorites.setCompoundDrawablesWithIntrinsicBounds(favoritesOutlineDrawable, null, null, null);
                    mFavorites.setTag("favoritesOutline");

                    // Delete this movie from the database
                    cupboard().withContext(getActivity()).delete(mMovieUri, mLimelightMovie);
                }
            }
        });
    }

    // Build the main UI for the detail screen
    private void buildMovieUI() {
        // Use a callback to handle using certain elements of the fetched data in activity
        mCallback.onMovieDataFetched(mLimelightMovie);

        // Set the title and tagline
        mMovieTitle.setText(mLimelightMovie.getMovieTitle());
        if (mLimelightMovie.getTagline() != null && !mLimelightMovie.getTagline().isEmpty()) {
            mTagline.setText(mLimelightMovie.getTagline());
        } else {
            mTagline.setVisibility(View.GONE);
        }

        // Set the release date
        String formattedDate = DateUtils.formatDateTime(getActivity(), mLimelightMovie.getReleaseDate().getTime(), 0);
        mReleaseDate.setText(formattedDate);

        // Set the genres and runtime
        if (!mLimelightMovie.getGenres().isEmpty() && mLimelightMovie.getGenres().get(0) != null && !mLimelightMovie.getGenres().get(0).getName().isEmpty()) {
                if (mLimelightMovie.getGenres().size() >= 2 && mLimelightMovie.getGenres().get(1) != null && !mLimelightMovie.getGenres().get(1).getName().isEmpty()) {
                mGenres.setText(String.format("%s | %s", mLimelightMovie.getGenres().get(0).getName(), mLimelightMovie.getGenres().get(1).getName()));
            } else {
                mGenres.setText(mLimelightMovie.getGenres().get(0).getName());
            }
        }

        if (mLimelightMovie.getRuntime() != null && mLimelightMovie.getRuntime() != 0) {
            String runtimeText = String.format("%d minutes", mLimelightMovie.getRuntime());
            mRuntime.setText(runtimeText);
        }

        // Set the user rating
        if (mLimelightMovie.getUserRating() != null && mLimelightMovie.getUserRating() != 0) {
            mUserRating.setText(mLimelightMovie.getUserRating().toString());
            if (mLimelightMovie.getNumRatings() != null && mLimelightMovie.getNumRatings() != 0) {
                mUserRatingCount.setText(Integer.toString(mLimelightMovie.getNumRatings()));
            } else {
                mUserRatingCount.setVisibility(View.GONE);
                mRatingsText.setVisibility(View.GONE);
            }
        } else {
            mUserRatingCardView.setVisibility(View.GONE);
        }

        mSynopsis.setText(mLimelightMovie.getSynopsis());

        // Details card
        if (!mLimelightMovie.getLanguages().isEmpty() && mLimelightMovie.getLanguages().get(0) != null) {
            mLanguage.setText(mLimelightMovie.getLanguages().get(0).getName());
        } else {
            mLanguage.setText("-");
        }

        if (mLimelightMovie.getBudget() != null && mLimelightMovie.getBudget() != 0) {
            String budgetText = String.format("$%d", mLimelightMovie.getBudget());
            mBudget.setText(budgetText);
        } else {
            mBudget.setText("-");
        }
    }

    // Initialize the parcelable Reviews object
    void initParcelableReviews(ReviewResultsPage reviewsPage) {
        mReviews = new ArrayList<>();

        if (reviewsPage != null && reviewsPage.results != null && !reviewsPage.results.isEmpty()) {

            for (int i = 0; i < reviewsPage.results.size(); i++) {
                ParcelableReview parcelableReview = new ParcelableReview();
                Review review = reviewsPage.results.get(i);

                parcelableReview.setReviewId(review.id);
                parcelableReview.setAuthor(review.author);
                parcelableReview.setContent(review.content);
                parcelableReview.setUrl(review.url);

                mReviews.add(parcelableReview);
            }
        }
    }

    // Build the UI for the single review on the detail screen
    void buildReviewUI() {
        if (!mReviews.isEmpty()) {
            mReviewAuthor.setText(mReviews.get(0).getAuthor());
            mReview.setText(mReviews.get(0).getContent());
        } else {
            mAuthorText.setVisibility(View.GONE);
            mMoreText.setVisibility(View.GONE);
            mReview.setText(R.string.no_reviews_found);
            mReview.setTypeface(null, Typeface.ITALIC);
            mReviewsCardView.setClickable(false);
        }
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
            mMovie = data;
            if (mMovie != null && mConfiguration != null && mVideos != null) {
                mLimelightMovie = Utils.convertMovieToLimelightMovie(data, mConfiguration, mVideos);
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
            if (mMovie != null && mConfiguration != null && mVideos != null) {
                mLimelightMovie = Utils.convertMovieToLimelightMovie(mMovie, data, mVideos);
                buildMovieUI();
            }
        }

        @Override
        public void onLoaderReset(Loader<Configuration> loader) {

        }
    }

    /**
     * Loader callbacks for movie videos
     */
    private class VideosLoaderCallbacks implements LoaderManager.LoaderCallbacks<Videos> {

        @Override
        public Loader<Videos> onCreateLoader(int id, Bundle args) {
            return new VideosLoader(getActivity(), mTmdbId);
        }

        @Override
        public void onLoadFinished(Loader<Videos> loader, Videos data) {
            mVideos = data;
            if (mMovie != null && mConfiguration != null && mVideos != null) {
                mLimelightMovie = Utils.convertMovieToLimelightMovie(mMovie, mConfiguration, data);
                buildMovieUI();
            }
        }

        @Override
        public void onLoaderReset(Loader<Videos> loader) {

        }
    }

    /**
     * Loader callbacks for movie reviews
     */
    private class ReviewsLoaderCallbacks implements LoaderManager.LoaderCallbacks<ReviewResultsPage> {

        @Override
        public Loader<ReviewResultsPage> onCreateLoader(int id, Bundle args) {
            return new ReviewsLoader(getActivity(), mTmdbId);
        }

        @Override
        public void onLoadFinished(Loader<ReviewResultsPage> loader, ReviewResultsPage data) {
            initParcelableReviews(data);
            buildReviewUI();
        }

        @Override
        public void onLoaderReset(Loader<ReviewResultsPage> loader) {

        }
    }
}
