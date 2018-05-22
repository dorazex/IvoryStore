package com.example.IvoryStore;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.IvoryStore.analytics.AnalyticsManager;
import com.example.IvoryStore.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GoogleSignInActivity extends Activity implements
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    static final int GET_USER_DETAILS_REQUEST = 1;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;
    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();

    private TextView mStatusTextView;
    private TextView mDetailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_google_sign_in);
//
//        // Views
//        mStatusTextView = findViewById(R.id.GoogleStatusTextView);
//        mDetailTextView = findViewById(R.id.GoogleDetailTextView);
//
//        // Button listeners
//        findViewById(R.id.GoogleSignInButton).setOnClickListener(this);
//        findViewById(R.id.GoogleSignOutButton).setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        } else if (requestCode == GET_USER_DETAILS_REQUEST){
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "getting result from user details request");

                Bundle extras = data.getExtras();
                String userFirstName = (String) extras.get("user_first_name");
                String userLastName = (String) extras.get("user_last_name");
                String userAge = (String) extras.get("user_age");
                String userCountry = (String) extras.get("user_country");
                String userCity = (String) extras.get("user_city");

                analyticsManager.setUserID(mAuth.getCurrentUser().getUid());
                analyticsManager.setUserProperty("first_name", userFirstName);
                analyticsManager.setUserProperty("last_name", userLastName);
                analyticsManager.setUserProperty("age", userAge);
                analyticsManager.setUserProperty("country", userCountry);
                analyticsManager.setUserProperty("city", userCity);

                Log.d(TAG, "user properties set");

                updateUI(mAuth.getCurrentUser());

            }

        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createNewUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(GoogleSignInActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void createNewUser() {
        Log.d(TAG, "creating new user");
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

        if (currentUser == null) {
            Log.e(TAG, "createNewUser() << Error user is null");
            return;
        }

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(currentUser.getUid())) {
                    userRef.child(currentUser.getUid()).setValue(new User(currentUser.getEmail(),0,null));
                    Log.d(TAG, "starting intent to get user details");
                    Intent getUserDetailsIntent = new Intent(getApplicationContext(), GetUserInfoActivity.class);
                    startActivityForResult(getUserDetailsIntent, GET_USER_DETAILS_REQUEST);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        // Check if user is signed in (non-null) and update UI accordingly.
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), IvoryStoreMain.class);
            startActivity(intent);
            finish();
        } else {
            signIn();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.GoogleSignInButton) {
            signIn();
        } else if (i == R.id.GoogleSignOutButton) {
            signOut();
        }
    }
}