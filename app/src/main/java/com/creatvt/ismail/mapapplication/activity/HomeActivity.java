package com.creatvt.ismail.mapapplication.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.creatvt.ismail.mapapplication.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void startTrackerActivity(View view) {
        Intent startTracker = new Intent(HomeActivity.this,MapsActivity.class);
        startActivity(startTracker);
    }

    public void startRouteActivity(View view) {
        Intent startRoute = new Intent(HomeActivity.this,RouteActivity.class);
        startActivity(startRoute);
    }
}
