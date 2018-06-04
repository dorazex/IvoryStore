package com.dorazouri.IvoryStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dorazouri.IvoryStore.analytics.AnalyticsManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SignInActivity extends Activity {
    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), IvoryStoreMain.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_sign_in);

        // set up remote config
        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        // set up defaults for remote parameters
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        // fetch parameters from firebase
        mFirebaseRemoteConfig.fetch(5)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(SignInActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        ImageButton emailButton = (ImageButton) findViewById(R.id.ChooseEmailSignInButton);
        ImageButton googleButton = (ImageButton) findViewById(R.id.ChooseGoogleSignInButton);
        ImageButton facebookButton = (ImageButton) findViewById(R.id.ChooseFacebookSignInButton);
        Button anonymousButton = (Button) findViewById(R.id.ChooseAnonymousSignInButton);

        // Enable/Disable anonymous user "sign in" based on remote config
        Boolean allow_anonymous_user = mFirebaseRemoteConfig.getBoolean("allow_anonymous_user");
        if (allow_anonymous_user){
            anonymousButton.setVisibility(View.VISIBLE);
        }else{
            anonymousButton.setVisibility(View.GONE);
        }

        // Set appropriate listeners for each button of different login method

        emailButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                analyticsManager.trackLoginEvent(EmailPasswordActivity.class.getSimpleName());
                Intent intent = new Intent(getApplicationContext(), EmailPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                analyticsManager.trackLoginEvent(GoogleSignInActivity.class.getSimpleName());
                Intent intent = new Intent(getApplicationContext(), GoogleSignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                analyticsManager.trackLoginEvent(FacebookSignInActivity.class.getSimpleName());
                Intent intent = new Intent(getApplicationContext(), FacebookSignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        anonymousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                analyticsManager.trackLoginEvent(AnonymousHomeActivity.class.getSimpleName());
                Intent intent = new Intent(getApplicationContext(), AnonymousHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
