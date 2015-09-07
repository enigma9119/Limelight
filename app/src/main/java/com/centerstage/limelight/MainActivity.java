package com.centerstage.limelight;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.centerstage.limelight.data.LimelightMovie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity implements HomeTabsFragment.onViewPagerCreatedListener,
        MovieFragment.OnMovieDataFetchedListener, MovieAdapter.OnItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String PREFS = "Prefs";
    public static final TmdbService sTmdbService = new TmdbService();

    @InjectView(R.id.main_tool_bar)
    Toolbar mToolbar;
    @InjectView(R.id.sliding_tabs)
    TabLayout mTabLayout;
    @InjectView(R.id.navigation_view)
    NavigationView mNavigationView;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    CollapsingToolbarLayout mCollapsingToolbar;
    ImageView mBackdropImage;
    FloatingActionButton mFab;

    public boolean mTwoPane;

    @Override
    public void onViewPagerCreated(ViewPager viewPager) {
        mTabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        final FragmentManager fm = getSupportFragmentManager();
        setSupportActionBar(mToolbar);

        if (findViewById(R.id.movie_details_container) != null) {
            // Details container is only present in large screen layouts. Hence, activity must be in two-pane mode.
            mTwoPane = true;

            // Initialize the UI elements from details screen
            mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            mBackdropImage = (ImageView) findViewById(R.id.movie_backdrop);
            mFab = (FloatingActionButton) findViewById(R.id.fab);

            // Initialize the detail view on the right side to an empty fragment
            if (savedInstanceState == null) {
                fm.beginTransaction().replace(R.id.movie_details_container, new EmptyFragment()).commit();
            }
        } else {
            mTwoPane = false;
        }

        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment == null) {
            fm.beginTransaction().replace(R.id.container, new HomeTabsFragment()).commit();
        } else if (fragment instanceof HomeTabsFragment) {
            mTabLayout.setVisibility(View.VISIBLE);
        } else if (fragment instanceof FavoritesFragment) {
            mTabLayout.setVisibility(View.GONE);
        }

        // Handle item click in the navigation drawer
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                // Toggle checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                // Close drawer on item click
                mDrawerLayout.closeDrawers();

                // Check to see which item was clicked
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        mTabLayout.setVisibility(View.VISIBLE);
                        fm.beginTransaction().replace(R.id.container, new HomeTabsFragment()).commit();
                        break;
                    case R.id.favorites:
                        mTabLayout.setVisibility(View.GONE);
                        fm.beginTransaction().replace(R.id.container, new FavoritesFragment()).commit();
                        break;
                    default:
                        Log.e(TAG, "Invalid item clicked in drawer: " + Integer.toString(menuItem.getItemId()));
                }

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        // Display hamburger icon
        actionBarDrawerToggle.syncState();
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
                if (movie.getTrailer() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getTrailer()));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_trailer_found, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onItemClick(List<LimelightMovie> databaseMovies, LimelightMovie item, ImageView poster) {
        if (!mTwoPane) {
            // Single Pane Layout
            Intent intent = new Intent(this, DetailActivity.class);

            // If movie data is coming from the database, pass the parcelable movie as an extra.
            // If movie data needs to be loaded in MovieFragment, pass the movie id as an extra.
            if (databaseMovies != null) {
                intent.putExtra(DetailActivity.PARCELABLE_MOVIE_EXTRA, item);
            } else {
                intent.putExtra(Intent.EXTRA_TEXT, item.getMovieId());
            }

            if (item.getPosterPath() != null) {
                // Convert ImageView (movie poster) to byte array
                byte[] byteArray = Utils.convertImageViewToByteArray(poster);

                // Pass this byte array into the intent
                intent.putExtra(Intent.EXTRA_STREAM, byteArray);
            }

            startActivity(intent);

        } else {
            // Two Pane Layout
            Bundle args = new Bundle();

            // If movie data is coming from the database, pass the parcelable movie as an extra.
            // If movie data needs to be loaded in MovieFragment, pass the movie id as an extra.
            if (databaseMovies != null) {
                args.putParcelable(DetailActivity.PARCELABLE_MOVIE_EXTRA, item);
            } else {
                args.putLong(Intent.EXTRA_TEXT, item.getMovieId());
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, MovieFragment.newInstance(args))
                    .commit();
        }
    }
}
