package com.example.IvoryStore;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AnonymousHomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_home);
        TextView anonymousHomeTextView = (TextView) findViewById(R.id.AnonymousHomeEditText);
        anonymousHomeTextView.setText(R.string.anonymous_user_home_text);
        anonymousHomeTextView.setKeyListener(null);
    }
}
