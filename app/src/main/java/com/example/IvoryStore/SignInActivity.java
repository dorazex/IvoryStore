package com.example.IvoryStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SignInActivity extends Activity {

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


        Button emailButton = (Button) findViewById(R.id.ChooseEmailSignInButton);
        Button googleButton = (Button) findViewById(R.id.ChooseGoogleSignInButton);
        Button facebookButton = (Button) findViewById(R.id.ChooseFacebookSignInButton);
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
                Intent intent = new Intent(getBaseContext(), EmailPasswordActivity.class);
                startActivity(intent);
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), GoogleSignInActivity.class);
                startActivity(intent);
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), FacebookSignInActivity.class);
                startActivity(intent);
            }
        });

        anonymousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AnonymousHomeActivity.class);
                startActivity(intent);
            }
        });

    }
}
