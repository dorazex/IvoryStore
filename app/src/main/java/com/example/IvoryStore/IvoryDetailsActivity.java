package com.example.IvoryStore;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.IvoryStore.adapter.ReviewsAdapter;
import com.example.IvoryStore.model.Review;
import com.example.IvoryStore.model.IvoryProduct;
import com.example.IvoryStore.model.User;

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
    private MediaPlayer mediaPlayer;
    private RecyclerView recyclerViewSongReviews;

    private DatabaseReference songReviewsRef;

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

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        StorageReference thumbRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("thumbs/" + ivoryProduct.getThumbImage());

        // Load the image using Glide
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(thumbRef)
                .into((ImageView) findViewById(R.id.imageViewSong));

        ((TextView) findViewById(R.id.textViewName)).setText(ivoryProduct.getName());
        ((TextView) findViewById(R.id.textViewArtist)).setText(ivoryProduct.getArtist());
        ((TextView) findViewById(R.id.textViewGenre)).setText(ivoryProduct.getGenre());
        buyPlay = ((Button) findViewById(R.id.buttonBuyPlay));

        buyPlay.setText("BUY $" + ivoryProduct.getPrice());
        Iterator i = user.getMySongs().iterator();
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
                    playCurrentSong(ivoryProduct.getFile());

                } else {
                    //Purchase the ivoryProduct.
                    Log.e(TAG, "buyPlay.onClick() >> Purchase the ivoryProduct");
                    user.getMySongs().add(key);
                    user.upgdateTotalPurchase(ivoryProduct.getPrice());
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

        recyclerViewSongReviews = findViewById(R.id.song_reviews);
        recyclerViewSongReviews.setHasFixedSize(true);
        recyclerViewSongReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewSongReviews.setItemAnimator(new DefaultItemAnimator());


        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviewsList);
        recyclerViewSongReviews.setAdapter(reviewsAdapter);

        songReviewsRef = FirebaseDatabase.getInstance().getReference("Songs/" + key +"/reviews");

        songReviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        Log.e(TAG, "onDataChange() >> Songs/" + key);

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Review review = dataSnapshot.getValue(Review.class);
                            reviewsList.add(review);
                        }
                        recyclerViewSongReviews.getAdapter().notifyDataSetChanged();
                        Log.e(TAG, "onDataChange(Review) <<");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
                    }
                });
        Log.e(TAG, "onCreate() <<");

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayingCurrentSong();
    }

    private void playCurrentSong(String songFile) {

        Log.e(TAG, "playCurrentSong() >> songFile=" + songFile);

        if (stopPlayingCurrentSong()) {
            Log.e(TAG, "playCurrentSong() << Stop playing current ivoryProduct");
            return;
        }

        FirebaseStorage.getInstance()
                .getReference("songs/" + songFile)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        Log.e(TAG, "onSuccess() >> " + downloadUrl.toString());

                        try {

                            mediaPlayer.setDataSource(downloadUrl.toString());
                            mediaPlayer.prepare(); // might take long! (for buffering, etc)
                            mediaPlayer.start();
                            buyPlay.setText("STOP");


                        } catch (Exception e) {
                            Log.w(TAG, "playSong() error:" + e.getMessage());
                        }

                        Log.e(TAG, "onSuccess() <<");
                    }
                });
        Log.e(TAG, "playCurrentSong() << ");
    }

    private boolean stopPlayingCurrentSong() {

        if (mediaPlayer.isPlaying()) {
            Log.e(TAG, "onSuccess() >> Stop the media player");
            //Stop the media player
            mediaPlayer.stop();
            mediaPlayer.reset();
            buyPlay.setText("PLAY");
            return true;
        }
        return false;
    }
}