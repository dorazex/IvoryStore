package com.example.IvoryStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.IvoryStore.model.IvoryProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.example.IvoryStore.model.Review;
import com.example.IvoryStore.model.User;

public class ReviewActivity extends Activity {

    private final String TAG = "ReviewActivity";
    private IvoryProduct ivoryProduct;
    private String key;
    private User user;
    private int prevRating = -1;

    private TextView userReview;
    private DatabaseReference songRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        key = getIntent().getStringExtra("key");
        ivoryProduct = getIntent().getParcelableExtra("ivoryProduct");
        user = getIntent().getParcelableExtra("user");

        userReview = findViewById(R.id.new_user_review);


        songRef = FirebaseDatabase.getInstance().getReference("Products/" + key);

        songRef.child("/reviews/" +  FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange(Review) >> " + snapshot.getKey());

                Review review = snapshot.getValue(Review.class);
                if (review != null) {
                    userReview.setText(review.getUserReview());
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


                IvoryProduct ivoryProduct = mutableData.getValue(IvoryProduct.class);

                if (ivoryProduct == null ) {
                    Log.e(TAG, "doTransaction() << ivoryProduct is null" );
                    return Transaction.success(mutableData);
                }

//                if (prevRating == -1) {
//                    // Increment the review count and rating only in case the user enters a new review
//                    ivoryProduct.incrementReviewCount();
//                } else{
//                }

                mutableData.setValue(ivoryProduct);
                Log.e(TAG, "doTransaction() << ivoryProduct was set");
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
                            user.getEmail());

                    songRef.child("/reviews/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(review);
                }


                Intent intent = new Intent(getApplicationContext(),IvoryDetailsActivity.class);
                intent.putExtra("ivoryProduct", ivoryProduct);
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
