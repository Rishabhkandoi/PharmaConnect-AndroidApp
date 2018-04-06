package com.example.shailesh.dashboard.Survey;

/**
 * Created by isha on 30/3/18.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SurveyMainActivity.class);
        startActivity(intent);
        finish();
    }

}
