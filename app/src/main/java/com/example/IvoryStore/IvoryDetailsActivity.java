package com.example.IvoryStore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.IvoryStore.adapter.ReviewsAdapter;
import com.example.IvoryStore.model.IvoryProduct;
import com.example.IvoryStore.model.Review;
import com.example.IvoryStore.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class IvoryDetailsActivity extends AppCompatActivity {

    public final String TAG = "IvoryDetailsActivity";

    private IvoryProduct ivoryProduct;
    private String key;
    private User user;
    private boolean productWasPurchased;

    private Button buyOrUseButton;
    private RecyclerView recyclerViewProductReviews;

    private List<Review> reviewsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ivory_details);

        key = getIntent().getStringExtra("key");
        ivoryProduct = getIntent().getParcelableExtra("ivoryProduct");
        user = getIntent().getParcelableExtra("user");

        StorageReference imageRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("product_image/" + ivoryProduct.getImage());

        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        ((ImageView) findViewById(R.id.imageViewProduct)).setMinimumHeight(dm.heightPixels);
                        ((ImageView) findViewById(R.id.imageViewProduct)).setMinimumWidth(dm.widthPixels);
                        ((ImageView) findViewById(R.id.imageViewProduct)).setImageBitmap(bm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

        ((TextView) findViewById(R.id.textViewName)).setText(ivoryProduct.getName());
        ((TextView) findViewById(R.id.textViewElephantAge)).setText(
                "Age:" + Integer.toString(ivoryProduct.getElephantAge()));
        ((TextView) findViewById(R.id.textViewOriginContinent)).setText(
                "Origin:" + ivoryProduct.getOriginContinent());
        ((TextView) findViewById(R.id.textViewDeathReason)).setText(
                "Death Reason:" + ivoryProduct.getDeathReason());
        ((TextView) findViewById(R.id.textViewWeight)).setText(
                "Weight:" + Integer.toString(ivoryProduct.getWeight()));

        buyOrUseButton = ((Button) findViewById(R.id.buttonBuyUse));
        buyOrUseButton.setText("$" + ivoryProduct.getPrice());

        for (String pKey :
                user.getProducts()) {
            if (pKey.equals(key)) {
                productWasPurchased = true;
                buyOrUseButton.setText("USE");
                break;
            }
        }

        buyOrUseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "buyOrUseButton.onClick() on product name = " + ivoryProduct.getName());

                if (productWasPurchased) {
                    Log.d(TAG, "buyOrUseButton.onClick() >> demonstrating product");
                    demonstrateCurrentProduct(ivoryProduct.getName());
                } else {
                    //Purchase the ivoryProduct.
                    Log.d(TAG, "buyOrUseButton.onClick() >> Purchase the ivoryProduct");
                    user.getProducts().add(key);
                    user.updateTotalPurchase(ivoryProduct.getPrice());
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                    productWasPurchased = true;
                    buyOrUseButton.setText("USE");
                }
            }
        });

        FloatingActionButton writeReviewButton =
                (FloatingActionButton) findViewById(R.id.buttonNewReview);
        writeReviewButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Log.d(TAG, "writeReviewButton.onClick() >>");

                 if (!productWasPurchased) {
                     Toast.makeText(IvoryDetailsActivity.this, "Only buyers can review",
                             Toast.LENGTH_SHORT).show();
                 } else {
                     Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                     intent.putExtra("ivoryProduct", ivoryProduct);
                     intent.putExtra("key", key);
                     intent.putExtra("user", user);

                     startActivity(intent);
                     finish();
                 }
             }
         }
        );

        recyclerViewProductReviews = findViewById(R.id.product_reviews);
        recyclerViewProductReviews.setHasFixedSize(true);
        recyclerViewProductReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewProductReviews.setItemAnimator(new DefaultItemAnimator());

        recyclerViewProductReviews.setAdapter(new ReviewsAdapter(reviewsList));

        DatabaseReference productReviewsRef = FirebaseDatabase.getInstance().getReference(
                "Products/" + key + "/reviews");
        productReviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    reviewsList.add(review);
                }
                recyclerViewProductReviews.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
            }
        });

    }

    private void demonstrateCurrentProduct(String productName) {
        Log.i(TAG, "demonstrateCurrentProduct() >> product name=" + productName);
        StorageReference imageRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("product_image/demonstration/" + ivoryProduct.getImage());

        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        ((ImageView) findViewById(R.id.imageViewDemonstrateProduct)).setMinimumHeight(dm.heightPixels);
                        ((ImageView) findViewById(R.id.imageViewDemonstrateProduct)).setMinimumWidth(dm.widthPixels);
                        ((ImageView) findViewById(R.id.imageViewDemonstrateProduct)).setImageBitmap(bm);
                        ((ImageView) findViewById(R.id.imageViewDemonstrateProduct)).setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

    }

}