package com.example.IvoryStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.example.IvoryStore.model.Review;
import com.example.IvoryStore.model.Song;
import com.example.IvoryStore.model.User;

public class ReviewActivity extends Activity {

    private final String TAG = "ReviewActivity";
    private Song song;
    private String key;
    private User user;
    private int prevRating = -1;

    private TextView userReview;
    private RatingBar userRating;
    private DatabaseReference songRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        key = getIntent().getStringExtra("key");
        song = getIntent().getParcelableExtra("song");
        user = getIntent().getParcelableExtra("user");

        userReview = findViewById(R.id.new_user_review);
        userRating = findViewById(R.id.new_user_rating);


        songRef = FirebaseDatabase.getInstance().getReference("Songs/" + key);

        songRef.child("/reviews/" +  FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange(Review) >> " + snapshot.getKey());

                Review review = snapshot.getValue(Review.class);
                if (review != null) {
                    userReview.setText(review.getUserReview());
                    userRating.setRating(review.getUserRating());
                    prevRating = review.getUserRating();
                }

                Log.e(TAG, "onDataChange(Review) <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
            }
        });

        Log.e(TAG, "onCreate() <<");

    }

    public void onSubmitClick(View v) {

        Log.e(TAG, "onSubmitClick() >>");


        songRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Log.e(TAG, "doTransaction() >>" );


                Song song = mutableData.getValue(Song.class);

                if (song == null ) {
                    Log.e(TAG, "doTransaction() << song is null" );
                    return Transaction.success(mutableData);
                }

                if (prevRating == -1) {
                    // Increment the review count and rating only in case the user enters a new review
                    song.incrementReviewCount();
                    song.incrementRating((int)userRating.getRating());
                } else{
                    song.incrementRating((int)userRating.getRating() - prevRating);
                }

                mutableData.setValue(song);
                Log.e(TAG, "doTransaction() << song was set");
                return Transaction.success(mutableData);

            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {

                Log.e(TAG, "onComplete() >>" );

                if (databaseError != null) {
                    Log.e(TAG, "onComplete() << Error:" + databaseError.getMessage());
                    return;
                }

                if (committed) {
                    Review review = new Review(
                            userReview.getText().toString(),
                            (int)userRating.getRating(),
                            user.getEmail());

                    songRef.child("/reviews/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(review);
                }


                Intent intent = new Intent(getApplicationContext(),IvoryDetailsActivity.class);
                intent.putExtra("song", song);
                intent.putExtra("key", key);
                intent.putExtra("user",user);
                startActivity(intent);
                finish();

                Log.e(TAG, "onComplete() <<" );
            }
        });



        Log.e(TAG, "onSubmitClick() <<");
    }

}
