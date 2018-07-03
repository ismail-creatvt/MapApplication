package com.creatvt.ismail.mapapplication;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TrackDetailsAdapter extends RecyclerView.Adapter<TrackViewHolder> {

    List<Track> trackList;
    public TrackDetailsAdapter(List<Track> trackList){
        this.trackList = trackList;
        Log.i("DateTime",trackList.get(0).getTime());
        Log.i("Latitude",Double.toString(trackList.get(0).getLatitude()));
        Log.i("Longitude",Double.toString(trackList.get(0).getLongitude()));
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item,parent,false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        Track track = trackList.get(position);
        holder.txtAddress.setText(track.getAddress());
        holder.txtTime.setText(track.getTime());
        holder.txtLatitude.setText(Double.toString(track.getLatitude()));
        holder.txtLongitude.setText(Double.toString(track.getLongitude()));
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }
}
