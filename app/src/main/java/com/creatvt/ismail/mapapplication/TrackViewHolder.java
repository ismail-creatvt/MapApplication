package com.creatvt.ismail.mapapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class TrackViewHolder extends RecyclerView.ViewHolder{
    TextView txtAddress,txtLatitude,txtLongitude,txtTime;
    public TrackViewHolder(View view) {
        super(view);
        txtAddress = (TextView) view.findViewById(R.id.address_cell);
        txtLatitude = (TextView) view.findViewById(R.id.latitude_cell);
        txtLongitude = (TextView) view.findViewById(R.id.longitude_cell);
        txtTime = (TextView) view.findViewById(R.id.time_cell);
    }
}
