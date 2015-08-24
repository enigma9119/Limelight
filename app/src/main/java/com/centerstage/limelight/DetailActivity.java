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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.centerstage.limelight.data.LimelightMovie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.uwetrottmann.tmdb.entities.Configuration;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DetailActivity extends AppCompatActivity implements MovieFragment.OnMovieDataFetchedListener {

    @InjectView(R.id.tool_bar)
    Toolbar mToolbar;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @InjectView(R.id.app_bar)
    AppBarLayout mAppBar;
    @InjectView(R.id.movie_backdrop)
    ImageView mBackdropImage;
    @InjectView(R.id.movie_poster)
    ImageView mMoviePoster;
    @InjectView(R.id.fab)
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.isOnline(this)) {
            setContentView(R.layout.activity_detail);
            ButterKnife.inject(this);

            setSupportActionBar(mToolbar);

            // Make backdrop image height = (1/2.6) of parent view height for portrait orientation
            // and (2/3) of parent view height for landscape orientation.
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
            float imageHeight;
            if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                imageHeight = getResources().getDisplayMetrics().heightPixels / (float) 2.6;
            } else {
                imageHeight = getResources().getDisplayMetrics().heightPixels * (float) (2.0 / 3.0);
            }
            lp.height = (int) imageHeight;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieDataFetched(final LimelightMovie movie, final Configuration configuration) {
        // Load the backdrop image
        String complete_backdrop_path = configuration.images.base_url +
                configuration.images.backdrop_sizes.get(1) +
                movie.getBackdropPath();

        Picasso.with(this).load(complete_backdrop_path)
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
                                MovieFragment fragment = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.movie_fragment);
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
                if (movie.getTrailer() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getTrailer()));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, No Trailer Found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
