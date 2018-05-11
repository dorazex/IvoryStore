package com.example.IvoryStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread sleepThread = new Thread(){
            @Override
            public void run(){
                try{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    sleep(3000);

                    Intent intent = new Intent(getApplicationContext(),
                            user == null ? SignInActivity.class : IvoryStoreMain.class);
                    startActivity(intent);
                    finish();
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        sleepThread.start();
    }
}
