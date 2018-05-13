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
import java.util.Iterator;
import java.util.List;

public class IvoryDetailsActivity extends AppCompatActivity {

    public final String TAG = "IvoryDetailsActivity";
    private IvoryProduct ivoryProduct;
    private String key;
    private User user;

    private FloatingActionButton writeReview;
    private Button buyPlay;
    private RecyclerView recyclerViewProductReviews;

    private DatabaseReference productReviewsRef;

    private List<Review> reviewsList =  new ArrayList<>();

    private boolean songWasPurchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ivory_details);

        key = getIntent().getStringExtra("key");
        ivoryProduct = getIntent().getParcelableExtra("ivoryProduct");
        user = getIntent().getParcelableExtra("user");

        StorageReference thumbRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("product_image/" + ivoryProduct.getImage());

//        // Load the image using Glide
//        Glide.with(this)
//                .using(new FirebaseImageLoader())
//                .load(thumbRef)
//                .into((ImageView) findViewById(R.id.imageViewProduct));

        ////////

        StorageReference mImageRef = thumbRef;
        final long ONE_MEGABYTE = 1024 * 1024;
        mImageRef.getBytes(ONE_MEGABYTE)
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
                // Handle any errors
            }
        });

        ////////

        ((TextView) findViewById(R.id.textViewName)).setText(ivoryProduct.getName());
        ((TextView) findViewById(R.id.textViewElephantAge)).setText(Integer.toString(ivoryProduct.getElephantAge()));
        ((TextView) findViewById(R.id.textViewOrigin)).setText(ivoryProduct.getOrigin());
        buyPlay = ((Button) findViewById(R.id.buttonBuyPlay));

        buyPlay.setText("BUY $" + ivoryProduct.getPrice());
        Iterator i = user.getProducts().iterator();
        while (i.hasNext()) {
            if (i.next().equals(key)) {
                songWasPurchased = true;
                buyPlay.setText("PLAY");
                break;
            }
        }


        buyPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Log.e(TAG, "buyPlay.onClick() >> file=" + ivoryProduct.getName());

                if (songWasPurchased) {
                    Log.e(TAG, "buyPlay.onClick() >> Playing purchased ivoryProduct");
                    //User purchased the ivoryProduct so he can play it
                    exhibitCurrentProduct(ivoryProduct.getName());

                } else {
                    //Purchase the ivoryProduct.
                    Log.e(TAG, "buyPlay.onClick() >> Purchase the ivoryProduct");
                    user.getProducts().add(key);
                    user.updateTotalPurchase(ivoryProduct.getPrice());
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                    songWasPurchased = true;
                    buyPlay.setText("PLAY");
                }
                Log.e(TAG, "playSong.onClick() <<");
            }
        });

        writeReview = (FloatingActionButton) findViewById(R.id.buttonNewReview);

        writeReview.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View view) {
                   Log.e(TAG, "writeReview.onClick() >>");


                   Intent intent = new Intent(getApplicationContext(),ReviewActivity.class);
                   intent.putExtra("ivoryProduct", ivoryProduct);
                   intent.putExtra("key", key);
                   intent.putExtra("user",user);

                   startActivity(intent);
                   finish();

                   Log.e(TAG, "writeReview.onClick() <<");
               }
           }
        );

        recyclerViewProductReviews = findViewById(R.id.product_reviews);
        recyclerViewProductReviews.setHasFixedSize(true);
        recyclerViewProductReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewProductReviews.setItemAnimator(new DefaultItemAnimator());


        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviewsList);
        recyclerViewProductReviews.setAdapter(reviewsAdapter);

        productReviewsRef = FirebaseDatabase.getInstance().getReference("Products/" + key +"/reviews");

        productReviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        Log.e(TAG, "onDataChange() >> Songs/" + key);

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Review review = dataSnapshot.getValue(Review.class);
                            reviewsList.add(review);
                        }
                        recyclerViewProductReviews.getAdapter().notifyDataSetChanged();
                        Log.e(TAG, "onDataChange(Review) <<");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
                    }
                });
        Log.e(TAG, "onCreate() <<");

    }

    private void exhibitCurrentProduct(String songFile) {

        Log.e(TAG, "exhibitCurrentProduct() >> songFile=" + songFile);

//        if (stopPlayingCurrentSong()) {
//            Log.e(TAG, "exhibitCurrentProduct() << Stop playing current ivoryProduct");
//            return;
//        }
//
//        FirebaseStorage.getInstance()
//                .getReference("songs/" + songFile)
//                .getDownloadUrl()
//                .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri downloadUrl) {
//                        Log.e(TAG, "onSuccess() >> " + downloadUrl.toString());
//
//                        try {
//
//                            mediaPlayer.setDataSource(downloadUrl.toString());
//                            mediaPlayer.prepare(); // might take long! (for buffering, etc)
//                            mediaPlayer.start();
//                            buyPlay.setText("STOP");
//
//
//                        } catch (Exception e) {
//                            Log.w(TAG, "playSong() error:" + e.getMessage());
//                        }
//
//                        Log.e(TAG, "onSuccess() <<");
//                    }
//                });
        Log.e(TAG, "exhibitCurrentProduct() << ");
    }

}