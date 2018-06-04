package com.dorazouri.IvoryStore;

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

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.dorazouri.IvoryStore.adapter.IvoryProductWithKey;
import com.dorazouri.IvoryStore.adapter.IvoryProductsAdapter;
import com.dorazouri.IvoryStore.adapter.ReviewsAdapter;
import com.dorazouri.IvoryStore.analytics.AnalyticsManager;
import com.dorazouri.IvoryStore.model.IvoryProduct;
import com.dorazouri.IvoryStore.model.Review;
import com.dorazouri.IvoryStore.model.User;
import com.dorazouri.IvoryStore.payment.BillingManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class IvoryDetailsActivity extends AppCompatActivity implements BillingManager.BillingUpdatesListener {

    public final String TAG = "IvoryDetailsActivity";

    private IvoryProduct ivoryProduct;
    private String key;
    private User user;
    private boolean productWasPurchased;
    private Button buyOrUseButton;
    private RecyclerView recyclerViewProductReviews;
    private List<Review> reviewsList = new ArrayList<>();
    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();
    private BillingManager mBillingManager;
    private List<IvoryProductWithKey> productsList = new ArrayList<>();
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ivory_details);

        mBillingManager = new BillingManager(this,this);

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
                "Age: " + Integer.toString(ivoryProduct.getElephantAge()));
        ((TextView) findViewById(R.id.textViewOriginContinent)).setText(
                "Origin: " + ivoryProduct.getOriginContinent());
        ((TextView) findViewById(R.id.textViewDeathReason)).setText(
                "Death Reason: " + ivoryProduct.getDeathReason());
        ((TextView) findViewById(R.id.textViewWeight)).setText(
                "Weight: " + Integer.toString(ivoryProduct.getWeight()));

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

        getAllProducts();

        buyOrUseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "buyOrUseButton.onClick() on product name = " + ivoryProduct.getName());

                if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
                    Toast.makeText(IvoryDetailsActivity.this,
                            "Anonymous cannot purchase.\nPlease log in",
                            Toast.LENGTH_SHORT).show();
                    analyticsManager.trackAnonymousTryBuyEvent(ivoryProduct);
                    return;
                }

                if (productWasPurchased) {
                    Log.d(TAG, "buyOrUseButton.onClick() >> demonstrating product");
                    demonstrateCurrentProduct(ivoryProduct.getName());
                    analyticsManager.trackProductUseEvent(ivoryProduct);
                } else {
                    //Purchase the ivoryProduct.
                    Log.d(TAG, "buyOrUseButton.onClick() >> Purchase the ivoryProduct");
                    user.getProducts().add(key);
                    user.updateTotalPurchase(ivoryProduct.getPrice());
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);

                    IvoryProductWithKey productWithKey = null;
                    for (int i=0; i<productsList.size(); i++){
                        if (productsList.get(i).getIvoryProduct().equals(ivoryProduct)){
                            productWithKey = productsList.get(i);
                        }
                    }
                    if (productWithKey == null) Log.d(TAG, "Couldn't find product: " + ivoryProduct.getName());

                    DatabaseReference productRef =
                            FirebaseDatabase.
                                    getInstance().
                                    getReference("Products").
                                    child(productWithKey.getKey());

                    productRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("available")){
                                String sku = BillingClient.SkuType.INAPP;
                                String originalProductKeyLowered = dataSnapshot.getKey().toLowerCase();
                                String productKey = originalProductKeyLowered.substring(1,originalProductKeyLowered.length());
                                mBillingManager.initiatePurchaseFlow(productKey,sku);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "onCancelled", databaseError.toException());
                        }
                    });



                    productWasPurchased = true;
                    analyticsManager.trackProductBuyEvent(ivoryProduct);
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

    private void getAllProducts() {
        productsList.clear();
        getAllProductsUsingChildListenrs();
    }

    private void getAllProductsUsingChildListenrs() {

        productsRef = FirebaseDatabase.getInstance().getReference("Products");
        productsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded(Products) >> " + snapshot.getKey());
                IvoryProductWithKey ivoryProductWithKey = new IvoryProductWithKey(snapshot.getKey(), snapshot.getValue(IvoryProduct.class));
                productsList.add(ivoryProductWithKey);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged(Products) >> " + snapshot.getKey());

                IvoryProduct ivoryProduct = snapshot.getValue(IvoryProduct.class);
                String key = snapshot.getKey();

                for (int i = 0; i < productsList.size(); i++) {
                    IvoryProductWithKey ivoryProductWithKey = (IvoryProductWithKey) productsList.get(i);
                    if (ivoryProductWithKey.getKey().equals(snapshot.getKey())) {
                        ivoryProductWithKey.setIvoryProduct(ivoryProduct);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved(Products) >> " + snapshot.getKey());
                Log.d(TAG, "onChildMoved(Products) << Doing nothing");

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved(Products) >> " + snapshot.getKey());

                for (int i = 0; i < productsList.size(); i++) {
                    IvoryProductWithKey ivoryProductWithKey = (IvoryProductWithKey) productsList.get(i);
                    if (ivoryProductWithKey.getKey().equals(snapshot.getKey())) {
                        productsList.remove(i);
                        Log.e(TAG, "onChildRemoved(Products) >> i=" + i);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled(Products) >>" + databaseError.getMessage());
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

    public void onBillingClientSetupFinished() {

        Log.e(TAG,"onBillingSetupFinished() >>");

        Log.e(TAG,"onBillingSetupFinished() <<");

    }

    public void onPurchasesUpdated(int resultCode,List<Purchase> purchases){

        Log.e(TAG,"onPurchasesUpdated() >> ");

        if (resultCode != BillingClient.BillingResponse.OK) {
            Log.e(TAG,"onPurchasesUpdated() << Error:"+resultCode);
            return;
        }

        for (Purchase purchase : purchases) {
            Log.e(TAG, "onPurchasesUpdated() >> " + purchase.toString());

            Log.d(TAG, "onPurchasesUpdated() >> " + purchase.getSku());

            if (purchase.getSku().contains("credit")) {
                Log.e(TAG, "onPurchasesUpdated() >> consuming " + purchase.getSku());
                //Only consume  one time product (subscription can't be consumed).
                mBillingManager.consumeAsync(purchase.getPurchaseToken());
            }
            //Update the server...
        }

        Log.e(TAG,"onPurchasesUpdated() <<");
    }

    public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {

        Log.e(TAG,"onConsumeFinished() >> result:"+result+" ,token:"+token);


        if (result == BillingClient.BillingResponse.OK) {
            Log.d(TAG, "Product with token:"+ token+ " was consumed successfully");
        } else {
            Log.e(TAG, "Error consuming product with token:" + token + " , error code:"+result);
        }

        Log.e(TAG,"onConsumeFinished() <<");

    }

}