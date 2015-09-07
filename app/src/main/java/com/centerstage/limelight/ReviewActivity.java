package com.centerstage.limelight;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ReviewActivity extends AppCompatActivity {

    public static final String REVIEWS_EXTRA = "com.centerstage.limelight.reviews";
    public static final String MOVIE_TITLE_EXTRA = "com.centerstage.limelight.movie_title";
    public static final String VIBRANT_COLOR_EXTRA = "com.centerstage.limelight.vibrant";
    public static final String DARK_VIBRANT_COLOR_EXTRA = "com.centerstage.limelight.dark_vibrant";

    @InjectView(R.id.review_tool_bar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        // Add up button that when clicked, goes back to movie details page
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Color the toolbar and status bar
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(VIBRANT_COLOR_EXTRA) && intent.hasExtra(DARK_VIBRANT_COLOR_EXTRA)) {
            int vibrantColor = intent.getIntExtra(VIBRANT_COLOR_EXTRA, R.attr.colorPrimary);
            int darkVibrantColor = intent.getIntExtra(DARK_VIBRANT_COLOR_EXTRA, R.attr.colorPrimaryDark);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mToolbar.setBackgroundColor(vibrantColor);
                getWindow().setStatusBarColor(darkVibrantColor);
            }
        } else {
            // Set default status bar color to app theme primary dark color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.material_deep_purple_700));
            }
        }

        // Set the movie title
        if (intent != null && intent.hasExtra(MOVIE_TITLE_EXTRA) && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(intent.getStringExtra(MOVIE_TITLE_EXTRA));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // Up button is pressed
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
