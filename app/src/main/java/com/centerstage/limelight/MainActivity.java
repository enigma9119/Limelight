package com.centerstage.limelight;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity implements HomeTabsFragment.onViewPagerCreatedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String PREFS = "Prefs";
    public static final TmdbService sTmdbService = new TmdbService();

    @InjectView(R.id.tool_bar)
    Toolbar mToolbar;
    @InjectView(R.id.sliding_tabs)
    TabLayout mTabLayout;
    @InjectView(R.id.navigation_view)
    NavigationView mNavigationView;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    public void onViewPagerCreated(ViewPager viewPager) {
        mTabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        final FragmentManager fm = getSupportFragmentManager();
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
}
