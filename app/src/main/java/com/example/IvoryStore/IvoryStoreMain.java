package com.example.IvoryStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.IvoryStore.model.IvoryProduct;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.IvoryStore.adapter.IvoryProductWithKey;
import com.example.IvoryStore.adapter.IvoryProductsAdapter;
import com.example.IvoryStore.model.User;

import java.util.ArrayList;
import java.util.List;

public class IvoryStoreMain extends Activity {

    private final String TAG = "IvoryStoreMain";

    private DatabaseReference productsRef;
    private User myUser;

    private RecyclerView recyclerView;
    private List<IvoryProductWithKey> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(savedInstanceState);
//        signInClassName = getIntent().getExtras().getString("sign_in_class");


        setContentView(R.layout.activity_ivory_store_main);

        recyclerView = (RecyclerView) findViewById(R.id.songs_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        TextView titleTextView = (TextView) findViewById(R.id.textViewUserID);
        if (currentUser.getEmail() == null){
            titleTextView.setText("Anonymous");
        } else {
            titleTextView.setText(currentUser.getEmail());
        }

        if (currentUser != null) {
            DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference("Users/" + currentUser.getUid());

            myUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    Log.e(TAG, "onDataChange(User) >> " + snapshot.getKey());

                    myUser = snapshot.getValue(User.class);

                    getAllSongs();

                    Log.e(TAG, "onDataChange(User) <<");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.e(TAG, "onCancelled(Users) >>" + databaseError.getMessage());
                }
            });

            Log.e(TAG, "onCreate() <<");
        } else {
            myUser = new User("anonymous@ivorystore.com", 0, new ArrayList<String>());
            getAllSongs();
        }

        Button signOutButton = (Button) findViewById(R.id.buttonSignOut);
        signOutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                    String providerId = user.getProviderId();
                    switch (providerId) {
                        case "google.com":
                            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
                            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
                            mGoogleSignInClient.signOut();
                            break;
                        case "facebook.com":
                            LoginManager.getInstance().logOut();
                            break;
                        default:
                            break;
                    }
                }
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getBaseContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getAllSongs() {


        songsList.clear();
        IvoryProductsAdapter ivoryProductsAdapter = new IvoryProductsAdapter(songsList, myUser);
        recyclerView.setAdapter(ivoryProductsAdapter);

        //getAllSongsUsingValueListenrs();
        getAllSongsUsingChildListenrs();


    }
    private void getAllSongsUsingValueListenrs() {

        productsRef = FirebaseDatabase.getInstance().getReference("Products");

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange(Songs) >> " + snapshot.getKey());

                updateSongsList(snapshot);

                Log.e(TAG, "onDataChange(Songs) <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Songs) >>" + databaseError.getMessage());
            }
        });
    }
    private void getAllSongsUsingChildListenrs() {

        productsRef = FirebaseDatabase.getInstance().getReference("Products");

        productsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName){


                Log.e(TAG, "onChildAdded(Songs) >> " + snapshot.getKey());

                IvoryProductWithKey ivoryProductWithKey = new IvoryProductWithKey(snapshot.getKey(),snapshot.getValue(IvoryProduct.class));
                songsList.add(ivoryProductWithKey);
                recyclerView.getAdapter().notifyDataSetChanged();

                Log.e(TAG, "onChildAdded(Songs) <<");

            }
            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName){

                Log.e(TAG, "onChildChanged(Songs) >> " + snapshot.getKey());

                IvoryProduct ivoryProduct =snapshot.getValue(IvoryProduct.class);
                String key = snapshot.getKey();

                for (int i = 0 ; i < songsList.size() ; i++) {
                    IvoryProductWithKey ivoryProductWithKey = (IvoryProductWithKey) songsList.get(i);
                    if (ivoryProductWithKey.getKey().equals(snapshot.getKey())) {
                        ivoryProductWithKey.setIvoryProduct(ivoryProduct);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        break;
                    }
                }

                Log.e(TAG, "onChildChanged(Songs) <<");

            }
            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName){

                Log.e(TAG, "onChildMoved(Songs) >> " + snapshot.getKey());


                Log.e(TAG, "onChildMoved(Songs) << Doing nothing");

            }
            @Override
            public void onChildRemoved(DataSnapshot snapshot){

                Log.e(TAG, "onChildRemoved(Songs) >> " + snapshot.getKey());

                IvoryProduct ivoryProduct =snapshot.getValue(IvoryProduct.class);
                String key = snapshot.getKey();

                for (int i = 0 ; i < songsList.size() ; i++) {
                    IvoryProductWithKey ivoryProductWithKey = (IvoryProductWithKey) songsList.get(i);
                    if (ivoryProductWithKey.getKey().equals(snapshot.getKey())) {
                        songsList.remove(i);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        Log.e(TAG, "onChildRemoved(Songs) >> i="+i);
                        break;
                    }
                }

                Log.e(TAG, "onChildRemoved(Songs) <<");

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Songs) >>" + databaseError.getMessage());
            }
        });

    }

     private void updateSongsList(DataSnapshot snapshot) {


        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            IvoryProduct ivoryProduct = dataSnapshot.getValue(IvoryProduct.class);
            Log.e(TAG, "updateSongList() >> adding ivoryProduct: " + ivoryProduct.getName());
            String key = dataSnapshot.getKey();
            songsList.add(new IvoryProductWithKey(key, ivoryProduct));
        }
         recyclerView.getAdapter().notifyDataSetChanged();

     }


    public void onSearchButtonClick(View v) {

        String searchString = ((EditText)findViewById(R.id.edit_text_search_song)).getText().toString();
        String orderBy = ((RadioButton)findViewById(R.id.radioButtonByReviews)).isChecked() ? "reviewsCount" : "price";
        Query searchSong;

        Log.e(TAG, "onSearchButtonClick() >> searchString="+searchString+ ",orderBy="+orderBy);

        songsList.clear();

        if (searchString != null && !searchString.isEmpty()) {
            searchSong = productsRef.orderByChild("name").startAt(searchString).endAt(searchString + "\uf8ff");
        } else {
            searchSong = productsRef.orderByChild(orderBy);
        }


        searchSong.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange(Query) >> " + snapshot.getKey());

                updateSongsList(snapshot);

                Log.e(TAG, "onDataChange(Query) <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled() >>" + databaseError.getMessage());
            }

        });
        Log.e(TAG, "onSearchButtonClick() <<");
    }

    public void onRadioButtonCLick(View v) {
        switch (v.getId()) {
            case R.id.radioButtonByPrice:
                ((RadioButton)findViewById(R.id.radioButtonByReviews)).setChecked(false);
                break;
            case R.id.radioButtonByReviews:
                ((RadioButton)findViewById(R.id.radioButtonByPrice)).setChecked(false);
                break;
        }
    }
}


