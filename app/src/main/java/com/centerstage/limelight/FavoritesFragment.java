package com.centerstage.limelight;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centerstage.limelight.data.LimelightMovie;
import com.centerstage.limelight.data.MovieProvider;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.littlerobots.cupboard.tools.provider.UriHelper;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Smitesh on 9/2/2015.
 * Fragment to display a list of user's favorite movies.
 */
public class FavoritesFragment extends Fragment {

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    MovieAdapter mMovieAdapter;

    Uri mMovieUri;

    public FavoritesFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.favorites);
    }

    @Override
    public void onResume() {
        super.onResume();

        // When back button is pressed from movie detail screen, the list of movies could change
        // as a favorite movie could have been removed. Hence, refresh the list.
        List<LimelightMovie> movies = cupboard().withContext(getActivity()).query(mMovieUri, LimelightMovie.class).query().list();
        mMovieAdapter.updateLimelightAdapter(movies);
        mMovieAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.inject(this, rootView);

        // Improve performance since changes in adapter content won't change size of RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a grid layout manager
        if (getActivity().getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            mLayoutManager = new GridLayoutManager(getActivity(), 3);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Get the Content Uri for the LimelightMovie table in the database
        UriHelper helper = UriHelper.with(MovieProvider.CONTENT_AUTHORITY);
        mMovieUri = helper.getUri(LimelightMovie.class);

        // Query movies from the database and setup adapter
        List<LimelightMovie> movies = cupboard().withContext(getActivity()).query(mMovieUri, LimelightMovie.class).query().list();
        mMovieAdapter = new MovieAdapter(movies);
        mRecyclerView.setAdapter(mMovieAdapter);

        return rootView;
    }
}
