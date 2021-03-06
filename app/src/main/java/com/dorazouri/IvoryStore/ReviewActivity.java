package com.dorazouri.IvoryStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dorazouri.IvoryStore.model.IvoryProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.dorazouri.IvoryStore.model.Review;
import com.dorazouri.IvoryStore.model.User;

public class ReviewActivity extends Activity {

    private final String TAG = "ReviewActivity";

    private IvoryProduct ivoryProduct;
    private String key;
    private User user;

    private TextView userReviewTextView;
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        key = getIntent().getStringExtra("key");
        ivoryProduct = getIntent().getParcelableExtra("ivoryProduct");
        user = getIntent().getParcelableExtra("user");

        userReviewTextView = findViewById(R.id.newUserReview);

        productRef = FirebaseDatabase.getInstance().getReference("Products/" + key);
        productRef.child("/reviews/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Review review = snapshot.getValue(Review.class);
                        if (review != null) {
                            userReviewTextView.setText(review.getUserReview());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
                    }
                });
    }

    public void onSubmitClick(View v) {

        Log.d(TAG, "onSubmitClick() called to update a review");

        productRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Log.d(TAG, "doTransaction() >>");

                IvoryProduct ivoryProduct = mutableData.getValue(IvoryProduct.class);

                if (ivoryProduct == null) {
                    Log.e(TAG, "doTransaction() << ivoryProduct is null");
                    return Transaction.success(mutableData);
                }

                mutableData.setValue(ivoryProduct);
                Log.d(TAG, "doTransaction() << ivoryProduct was set");
                return Transaction.success(mutableData);

            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {

                Log.d(TAG, "onComplete() >>");

                if (databaseError != null) {
                    Log.e(TAG, "onComplete() << Error:" + databaseError.getMessage());
                    return;
                }

                if (committed) {
                    Review review = new Review(
                            userReviewTextView.getText().toString(),
                            user.getEmail());

                    productRef.child("/reviews/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(review);
                }

                Intent intent = new Intent(getApplicationContext(), IvoryDetailsActivity.class);
                intent.putExtra("ivoryProduct", ivoryProduct);
                intent.putExtra("key", key);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });
    }
}
