package com.creatvt.ismail.mapapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class TrackDetails extends AppCompatActivity {

    RecyclerView rvTrackList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);
        Intent data = getIntent();
        String name = data.getStringExtra("name");
        DBHelper dbHelper = new DBHelper(this);
        rvTrackList = findViewById(R.id.track_details);
        rvTrackList.setAdapter(new TrackDetailsAdapter(dbHelper.getTracks(name)));
        rvTrackList.setLayoutManager(new LinearLayoutManager(this));
        rvTrackList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }
}
