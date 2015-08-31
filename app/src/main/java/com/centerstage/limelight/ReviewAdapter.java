package com.centerstage.limelight;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.centerstage.limelight.data.ParcelableReview;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Smitesh on 8/30/2015.
 * Adapter to display the reviews in a list.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>  {

    private ArrayList<ParcelableReview> mReviews;

    public ReviewAdapter(ArrayList<ParcelableReview> reviews) {
        mReviews = reviews;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewAdapter.ViewHolder holder, int position) {
        final ParcelableReview review = mReviews.get(position);

        holder.mAuthor.setText(review.getAuthor());
        holder.mReview.setText(review.getContent());

        holder.mReviewsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mReview.getEllipsize() == TextUtils.TruncateAt.END) {
                    holder.mReview.setEllipsize(null);
                    holder.mReview.setMaxLines(500);
                } else {
                    holder.mReview.setEllipsize(TextUtils.TruncateAt.END);
                    holder.mReview.setMaxLines(10);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.reviews_cardview)
        CardView mReviewsCardView;
        @InjectView(R.id.author)
        TextView mAuthor;
        @InjectView(R.id.review)
        TextView mReview;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
