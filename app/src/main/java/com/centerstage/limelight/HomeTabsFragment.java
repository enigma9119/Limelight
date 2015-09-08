package com.centerstage.limelight;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Smitesh on 9/2/2015.
 * Outer Home fragment that displays the tab layout.
 */
public class HomeTabsFragment extends Fragment {

    @InjectView(R.id.viewpager)
    ViewPager mViewPager;

    OnViewPagerCreatedListener mCallback;

    public interface OnViewPagerCreatedListener {
        void onViewPagerCreated(ViewPager viewPager);
    }

    public HomeTabsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnViewPagerCreatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieDataFetchedListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hometabs, container, false);
        ButterKnife.inject(this, rootView);
        setHasOptionsMenu(true);

        mViewPager.setAdapter(new MoviesPagerAdapter(getChildFragmentManager()));
        mCallback.onViewPagerCreated(mViewPager);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_home_fragment, menu);

        // Restore preferences. Used to set initial checked state for sort mode.
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS, 0);
        int sort_order = settings.getInt(HomeFragment.KEY_SORT, HomeFragment.SORT_POPULAR);

        if (sort_order == HomeFragment.SORT_POPULAR) {
            MenuItem sortPopular = menu.findItem(R.id.sort_most_popular);
            sortPopular.setChecked(true);
        } else {
            MenuItem sortRating = menu.findItem(R.id.sort_highest_rated);
            sortRating.setChecked(true);
        }
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
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(HomeFragment.KEY_SORT, defaultValue).apply();

        // Reset the adapter to update the data and reload the tabs to update the title
        mViewPager.setAdapter(new MoviesPagerAdapter(getChildFragmentManager()));
        mCallback.onViewPagerCreated(mViewPager);
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
            SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS, 0);
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
}
