package com.centerstage.limelight;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.centerstage.limelight.data.LimelightMovie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DetailActivity extends AppCompatActivity implements MovieFragment.OnMovieDataFetchedListener {

    public static final String PARCELABLE_MOVIE_EXTRA = "com.centerstage.limelight.movie";

    @InjectView(R.id.movie_tool_bar)
    Toolbar mToolbar;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @InjectView(R.id.movie_backdrop)
    ImageView mBackdropImage;
    @InjectView(R.id.movie_poster)
    ImageView mMoviePoster;
    @InjectView(R.id.fab)
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the details UI if online or if movie data is stored in database
        if (Utils.isOnline(this) || (getIntent() != null && getIntent().hasExtra(PARCELABLE_MOVIE_EXTRA))) {
            setContentView(R.layout.activity_detail);
            ButterKnife.inject(this);

            setSupportActionBar(mToolbar);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new MovieFragment())
                        .commit();
            }

            // Make status bar color transparent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
            }

            // Add up button that when clicked, goes back to home page
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // Fetch the movie poster extra from the intent.
            // This is done so that the poster image doesn't need to be downloaded again. Will be
            // useful when using shared element scene transitions.
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_STREAM)) {
                byte[] byteArray = intent.getByteArrayExtra(Intent.EXTRA_STREAM);
                Bitmap posterImageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                mMoviePoster.setImageBitmap(posterImageBitmap);
            } else {
                mMoviePoster.setImageDrawable(getResources().getDrawable(R.drawable.movie_poster_placeholder));
            }

        } else {
            setContentView(R.layout.no_network);
        }
    }

    @Override
    public void onMovieDataFetched(final LimelightMovie movie) {
        final MovieFragment fragment = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.movie_details_container);

        // Load the backdrop image
        Picasso.with(this).load(movie.getBackdropPath())
                .error(R.drawable.backdrop_placeholder)
                .into(mBackdropImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Use Palette to generate colors derived from the backdrop image
                        Bitmap backdropImageBitmap = ((BitmapDrawable)mBackdropImage.getDrawable()).getBitmap();
                        Palette.from(backdropImageBitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                mCollapsingToolbar.setContentScrimColor(palette.getVibrantColor(R.attr.colorPrimary));
                                mCollapsingToolbar.setStatusBarScrimColor(palette.getDarkVibrantColor(R.attr.colorPrimaryDark));

                                // Color the Floating Action Button
                                if (palette.getVibrantSwatch() != null) {
                                    Drawable playDrawable = getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp);
                                    playDrawable.setColorFilter(palette.getVibrantSwatch().getBodyTextColor(), PorterDuff.Mode.SRC_IN);
                                    mFab.setImageDrawable(playDrawable);

                                    mFab.setBackgroundTintList(ColorStateList.valueOf(palette.getVibrantColor(R.attr.colorPrimary)));
                                }

                                // Send the palette to the movie fragment
                                if (fragment != null) fragment.onPaletteGenerated(palette);
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });

        // Set the title in the tool bar
        mCollapsingToolbar.setTitle(movie.getMovieTitle());

        // Play the movie trailer when play button is clicked
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!movie.getVideos().isEmpty()) {
                    String url = movie.getVideos().get(0).getUrl();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_trailer_found, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
