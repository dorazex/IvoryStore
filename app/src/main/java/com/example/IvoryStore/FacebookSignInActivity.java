package com.example.IvoryStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.IvoryStore.analytics.AnalyticsManager;
import com.example.IvoryStore.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Demonstrate Firebase Authentication using a Facebook access token.
 */
public class FacebookSignInActivity extends Activity implements
        View.OnClickListener {

    private static final String TAG = "FacebookLogin";
    static final int GET_USER_DETAILS_REQUEST = 1;
    private TextView mStatusTextView;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private CallbackManager mCallbackManager;
    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        updateUI(mAuth.getCurrentUser());

        setContentView(R.layout.activity_facebook_sign_in);

        // Views
        mStatusTextView = findViewById(R.id.FacebookStatusTextView);
//        findViewById(R.id.FacebookSignOutButton).setOnClickListener(this);


        // [START initialize_fblogin]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.FacebookSignInButton);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
    // [END on_activity_result]

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
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
                            Toast.makeText(FacebookSignInActivity.this, "Authentication failed:" + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }
    // [END auth_with_facebook]

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

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), IvoryStoreMain.class);
            intent.putExtra("sign_in_class", this.getClass().getSimpleName());
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
//        if (i == R.id.FacebookSignOutButton) {
//            signOut();
//        }
    }
}
