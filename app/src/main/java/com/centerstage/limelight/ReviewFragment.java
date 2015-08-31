package com.centerstage.limelight;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centerstage.limelight.data.ParcelableReview;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A fragment to display all the reviews of a movie.
 */
public class ReviewFragment extends Fragment {

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    ArrayList<ParcelableReview> mReviews;

    public ReviewFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(ReviewActivity.REVIEWS_EXTRA)) {
            mReviews = intent.getParcelableArrayListExtra(ReviewActivity.REVIEWS_EXTRA);
        }

        // Setup adapter
        mRecyclerView.setAdapter(new ReviewAdapter(mReviews));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        ButterKnife.inject(this, rootView);

        // Improve performance since changes in adapter content won't change size of RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return  rootView;
    }
}
