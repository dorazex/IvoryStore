package com.example.IvoryStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.IvoryStore.model.User;

public class SignInActivity extends Activity {

    public final static String TAG = "SignInActivity";

    private FirebaseAuth mAuth;
    private EditText mEmail;
    private EditText mPass;
    private boolean mIsSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mEmail = findViewById(R.id.editTextEmail);
        mPass = findViewById(R.id.editTextPassword);

        mAuth = FirebaseAuth.getInstance();

    }

    public void onClick(View v) {

        Log.e(TAG, "onEmailPasswordAuthClick() >>");

        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();


        Task<AuthResult> authResult;

        switch (v.getId()) {
            case R.id.buttonSignin:
                //Email / Password sign-in
                authResult = mAuth.signInWithEmailAndPassword(email, pass);
                mIsSignup = false;
                break;
            case R.id.buttonSignup:
                //Email / Password sign-up
                authResult = mAuth.createUserWithEmailAndPassword(email, pass);
                mIsSignup = true;
                break;
            default:
                return;
        }
        authResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.e(TAG, "Email/Pass Auth: onComplete() >> " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.e(TAG, "Email/Pass Auth: onComplete() >> " + task.getException().getMessage());

                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                } else {

                    if (mIsSignup) {
                        createNewUser();
                    }
                    Intent intent = new Intent(getApplicationContext(), MusicPlayerMain.class);
                    startActivity(intent);
                    finish();

                }
                Log.e(TAG, "Email/Pass Auth: onComplete() <<");
            }
        });

        Log.e(TAG, "onEmailPasswordAuthClick() <<");
    }

    private void createNewUser() {

        Log.e(TAG, "createNewUser() >>");

        FirebaseUser fbUser = mAuth.getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

        if (fbUser == null) {
            Log.e(TAG, "createNewUser() << Error user is null");
            return;
        }

        userRef.child(fbUser.getUid()).setValue(new User(fbUser.getEmail(),0,null));

        Log.e(TAG, "createNewUser() <<");
    }
}
