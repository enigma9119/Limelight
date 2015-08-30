package com.centerstage.limelight;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centerstage.limelight.data.ParcelableReview;

import java.util.ArrayList;

/**
 * A fragment to display all the reviews of a movie.
 */
public class ReviewFragment extends Fragment {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review, container, false);
    }
}
