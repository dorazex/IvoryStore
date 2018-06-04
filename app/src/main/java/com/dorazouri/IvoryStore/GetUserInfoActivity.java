package com.dorazouri.IvoryStore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dorazouri.IvoryStore.analytics.AnalyticsManager;

public class GetUserInfoActivity extends Activity {

    final Context context = this;
    private Button submitButton;
    private EditText firstName;
    private EditText lastName;
    private EditText age;
    private EditText country;
    private EditText city;
    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_info);

        analyticsManager.trackSignupEvent(this.getCallingActivity().getClassName());

        submitButton = (Button) findViewById(R.id.buttonSubmitUserProperties);
        firstName = (EditText) findViewById(R.id.editTextUserFirstName);
        lastName = (EditText) findViewById(R.id.editTextUserLastName);
        age = (EditText) findViewById(R.id.editTextUserAge);
        country = (EditText) findViewById(R.id.editTextUserCountry);
        city = (EditText) findViewById(R.id.editTextUserCity);

        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent _result = new Intent();
                _result.putExtra("user_first_name", ((EditText)findViewById(R.id.editTextUserFirstName)).getText().toString());
                _result.putExtra("user_last_name", ((EditText)findViewById(R.id.editTextUserLastName)).getText().toString());
                _result.putExtra("user_age", ((EditText)findViewById(R.id.editTextUserAge)).getText().toString());
                _result.putExtra("user_country", ((EditText)findViewById(R.id.editTextUserCountry)).getText().toString());
                _result.putExtra("user_city", ((EditText)findViewById(R.id.editTextUserCity)).getText().toString());
                setResult(Activity.RESULT_OK, _result);
                finish();
            }
        });

    }

//    @Override
//    public void onClick(View v) {
//        Intent _result = new Intent();
//        _result.putExtra("user_first_name", ((EditText)findViewById(R.id.editTextUserFirstName)).getText());
//        _result.putExtra("user_last_name", ((EditText)findViewById(R.id.editTextUserLastName)).getText());
//        _result.putExtra("user_age", ((EditText)findViewById(R.id.editTextUserAge)).getText());
//        _result.putExtra("user_country", ((EditText)findViewById(R.id.editTextUserCountry)).getText());
//        _result.putExtra("user_city", ((EditText)findViewById(R.id.editTextUserCity)).getText());
//        setResult(Activity.RESULT_OK, _result);
//        finish();
//
////
////        final EditText firstName = (EditText) userInfoPromptsView.findViewById(R.id.editTextUserFirstName);
////        final EditText lastName = (EditText) userInfoPromptsView.findViewById(R.id.editTextUserLastName);
////        final EditText age = (EditText) userInfoPromptsView.findViewById(R.id.editTextUserAge);
////        final EditText country = (EditText) userInfoPromptsView.findViewById(R.id.editTextUserCountry);
////        final EditText city = (EditText) userInfoPromptsView.findViewById(R.id.editTextUserCity);
//
//}
}
