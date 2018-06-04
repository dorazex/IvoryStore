package com.dorazouri.IvoryStore;

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

import com.dorazouri.IvoryStore.analytics.AnalyticsManager;
import com.dorazouri.IvoryStore.model.IvoryProduct;
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
import com.dorazouri.IvoryStore.adapter.IvoryProductWithKey;
import com.dorazouri.IvoryStore.adapter.IvoryProductsAdapter;
import com.dorazouri.IvoryStore.model.User;

import java.util.ArrayList;
import java.util.List;

public class IvoryStoreMain extends Activity {

    private final String TAG = "IvoryStoreMain";

    private DatabaseReference productsRef;
    private User myUser;
    private RecyclerView recyclerView;
    private List<IvoryProductWithKey> productsList = new ArrayList<>();
    private String lastSearchedField;
    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ivory_store_main);

        recyclerView = (RecyclerView) findViewById(R.id.productsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        TextView titleTextView = (TextView) findViewById(R.id.textViewUserID);
        if (currentUser.getEmail() == null) {
            titleTextView.setText("Anonymous");
        } else {
            titleTextView.setText(currentUser.getEmail());
        }

        if (currentUser != null) {
            DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference("Users/" + currentUser.getUid());
            myUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.d(TAG, "onDataChange(User) >> " + snapshot.getKey());
                    myUser = snapshot.getValue(User.class);
                    getAllProducts();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled(Users) >>" + databaseError.getMessage());
                }
            });

        } else {
            myUser = new User("anonymous@ivorystore.com", 0, new ArrayList<String>());
            getAllProducts();
        }

        Button signOutButton = (Button) findViewById(R.id.buttonSignOut);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private void signOut() {
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
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

    private void getAllProducts() {
        productsList.clear();
        if (myUser == null) {
            signOut();
        }
        IvoryProductsAdapter ivoryProductsAdapter = new IvoryProductsAdapter(productsList, myUser);
        recyclerView.setAdapter(ivoryProductsAdapter);
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
                recyclerView.getAdapter().notifyDataSetChanged();
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
                        recyclerView.getAdapter().notifyDataSetChanged();
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
                        recyclerView.getAdapter().notifyDataSetChanged();
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

    private void updateProductsList(DataSnapshot snapshot) {
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            IvoryProduct ivoryProduct = dataSnapshot.getValue(IvoryProduct.class);
            Log.d(TAG, "updateProductsList() >> adding ivoryProduct: " + ivoryProduct.getName());
            String key = dataSnapshot.getKey();
            productsList.add(new IvoryProductWithKey(key, ivoryProduct));
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void onSearchByTextField(String fieldName) {
        lastSearchedField = fieldName;
        String searchString = ((EditText) findViewById(R.id.editTextSearch)).getText().toString();
        String orderBy = ((RadioButton) findViewById(R.id.radioButtonByElephantAge)).isChecked()
                ? "elephantAge" : "price";
        Query querySearch;
        Log.d(TAG, "searchString=" + searchString + ",orderBy=" + orderBy);

        analyticsManager.trackSearchEvent(searchString);

        productsList.clear();
        if (searchString != null && !searchString.isEmpty()) {
            querySearch = productsRef.orderByChild(fieldName).startAt(searchString).endAt(searchString + "\uf8ff");
        } else {
            querySearch = productsRef.orderByChild(orderBy);
        }

        querySearch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange(Query) >> " + snapshot.getKey());
                updateProductsList(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled() >>" + databaseError.getMessage());
            }

        });
    }

    public void onSearchByNameButtonClick(View v) {
        onSearchByTextField("name");
    }

    public void onSearchByOriginButtonClick(View v) {
        onSearchByTextField("originContinent");
    }

    public void onRadioButtonCLick(View v) {
        switch (v.getId()) {
            case R.id.radioButtonByPrice:
                ((RadioButton) findViewById(R.id.radioButtonByPrice)).setChecked(true);
                ((RadioButton) findViewById(R.id.radioButtonByElephantAge)).setChecked(false);
                break;
            case R.id.radioButtonByElephantAge:
                ((RadioButton) findViewById(R.id.radioButtonByPrice)).setChecked(false);
                ((RadioButton) findViewById(R.id.radioButtonByElephantAge)).setChecked(true);
                break;
        }
        ((EditText) findViewById(R.id.editTextSearch)).setText("");
        onSearchByTextField(lastSearchedField);
    }
}


