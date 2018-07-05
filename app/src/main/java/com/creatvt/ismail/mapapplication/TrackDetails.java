package com.creatvt.ismail.mapapplication;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.adapter.recyclerview.ColumnHeaderRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class TrackDetails extends AppCompatActivity {

    RecyclerView rvTrackList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);
        Intent data = getIntent();
        String name = data.getStringExtra("name");
        DBHelper dbHelper = new DBHelper(this);
        List<List<Cell>> cellList = dbHelper.getTracks(name);
        if(cellList == null){
            cellList = new ArrayList<>();
            List<Cell> row = new ArrayList<>();
            for(int i=0;i<4;i++){
                row.add(new Cell(""));
            }
            cellList.add(row);
        }
        List<ColumnHeader> columnHeaders = new ArrayList<>();
        columnHeaders.add(new ColumnHeader("Time"));
        columnHeaders.add(new ColumnHeader("Address"));
        columnHeaders.add(new ColumnHeader("Latitude"));
        columnHeaders.add(new ColumnHeader("Longitude"));

        TableView trackDetails = (TableView) findViewById(R.id.track_details);
        List<RowHeader> rowHeaders = new ArrayList<>();
        for(int i=1;i<=cellList.size();i++){
            rowHeaders.add(new RowHeader(i+""));
        }
        TrackTableAdapter adapter = new TrackTableAdapter(this);
        adapter.setTableView(trackDetails);
        trackDetails.setAdapter(adapter);
        adapter.setAllItems(columnHeaders,rowHeaders,cellList);
    }
}
