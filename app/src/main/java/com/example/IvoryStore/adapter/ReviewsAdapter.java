package com.example.IvoryStore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.IvoryStore.R;
import com.example.IvoryStore.model.Review;

import java.util.List;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private final String TAG = "ReviewsAdapter";
    private List<Review> reviewsList;

    public ReviewsAdapter(List<Review> reviewsList) {

        this.reviewsList = reviewsList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(parent.getContext(),itemView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Log.d(TAG,String.format("onBindViewHolder called for review in position %s", position));

        Review review = reviewsList.get(position);
        holder.getUserMailTextView().setText(review.getUserEmail());
        holder.getUserReviewTextView().setText(review.getUserReview());
    }


    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView userReviewTextView;
        private TextView userMailTextView;

        private ReviewViewHolder(Context context, View view) {

            super(view);
            userReviewTextView = (TextView) view.findViewById(R.id.userReview);
            userMailTextView = (TextView) view.findViewById(R.id.userEmail);

        }

        private TextView getUserReviewTextView() {
            return userReviewTextView;
        }

        public void setUserReviewTextView(TextView userReviewTextView) {
            this.userReviewTextView = userReviewTextView;
        }

        private TextView getUserMailTextView() {
            return userMailTextView;
        }

        public void setUserMailTextView(TextView userMailTextView) {
            this.userMailTextView = userMailTextView;
        }

    }
}
