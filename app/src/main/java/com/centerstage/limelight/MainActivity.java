package com.centerstage.limelight;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.uwetrottmann.tmdb.entities.Configuration;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {

    public static final String PREFS = "Prefs";
    public static final TmdbService sTmdbService = new TmdbService();
    public static Configuration sConfiguration;

    @InjectView(R.id.tool_bar)
    Toolbar mToolbar;
    @InjectView(R.id.viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.sliding_tabs)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        mViewPager.setAdapter(new MoviesPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

        // Get the common configuration data from Tmdb
        new FetchConfigurationTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Restore preferences. Used to set initial checked state for sort mode.
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        int sort_order = settings.getInt(HomeFragment.KEY_SORT, HomeFragment.SORT_POPULAR);

        if (sort_order == HomeFragment.SORT_POPULAR) {
            MenuItem sortPopular = menu.findItem(R.id.sort_most_popular);
            sortPopular.setChecked(true);
        } else {
            MenuItem sortRating = menu.findItem(R.id.sort_highest_rated);
            sortRating.setChecked(true);
        }

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
        } else if (id == R.id.sort_most_popular) {

            // Update data only if sort option was changed
            if (!item.isChecked()) {
                item.setChecked(true);
                onSortOptionChanged(HomeFragment.SORT_POPULAR);
            }

            return true;

        } else if (id == R.id.sort_highest_rated) {

            // Update data only if sort option was changed
            if (!item.isChecked()) {
                item.setChecked(true);
                onSortOptionChanged(HomeFragment.SORT_RATING);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSortOptionChanged(int defaultValue) {
        // Save the new user preference for sorting
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(HomeFragment.KEY_SORT, defaultValue).apply();

        // Reset the adapter to update the data and reload the tabs to update the title
        mViewPager.setAdapter(new MoviesPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
    }


    private class MoviesPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[] { "Popular", "In Theaters", "Coming Soon" };
        private String titleTopRated = "Top Rated";

        public MoviesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return HomeFragment.newInstance(position + 1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Restore preferences
            SharedPreferences settings = getSharedPreferences(PREFS, 0);
            int sort_order = settings.getInt(HomeFragment.KEY_SORT, HomeFragment.SORT_POPULAR);

            // For the first page, if sort by rating is selected then change the page title
            if(position == 0 && sort_order == HomeFragment.SORT_RATING) {
                return titleTopRated;
            } else {
                return tabTitles[position];
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }


    private class FetchConfigurationTask extends AsyncTask<Void, Void, Configuration> {

        @Override
        protected Configuration doInBackground(Void... params) {
            return sTmdbService.getConfiguration();
        }

        @Override
        protected void onPostExecute(Configuration configuration) {
            sConfiguration = configuration;
        }
    }
}
