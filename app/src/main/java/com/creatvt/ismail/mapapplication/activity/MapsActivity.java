package com.creatvt.ismail.mapapplication.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creatvt.ismail.mapapplication.DBHelper;
import com.creatvt.ismail.mapapplication.R;
import com.creatvt.ismail.mapapplication.TrackDetails;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener,DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int MY_PERMISSION_REQUEST_GET_LOCATION = 111;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS/2;

    private SettingsClient mSettingsClient;
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private Marker googleMarker;
    private Polyline mPolylineGoogle;
    private Boolean mRequestingLocationUpdates;
    private Boolean isLocationTrackingEnabled = true;
    private AlertDialog alertForName,alertForComfirmName;
    private View alertView;
    private SharedPreferences preferences;
    private LinearLayout trackedBar;
    private TextView trackedName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences =  getSharedPreferences(getString(R.string.shared_preference_key),MODE_PRIVATE);
        setContentView(R.layout.activity_maps);
        trackedName = findViewById(R.id.trackedName);

        findViewById(R.id.edit_btn).setOnClickListener(this);
        // Current User Tracking Details Bar containing name,get track btn and edit name btn
        trackedBar = (LinearLayout) findViewById(R.id.trackedBar);
        if(!preferences.contains("name")) // If user not set
            trackedBar.setVisibility(View.GONE);
        else    // If user is already set
            trackedName.setText(preferences.getString("name","name"));

        //Start Stop Switch
        SwitchCompat startStop = findViewById(R.id.swhStartStop);
        startStop.setOnCheckedChangeListener(this);
        startStop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MapsActivity.this,"Tracking Toggle",Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        // Get Tracked Button
        ((ImageButton) findViewById(R.id.get_track)).setOnClickListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        googleFusedLocationSetUp();
        if(preferences.contains("isLocationTrackingEnabled") &&
                preferences.getBoolean("isLocationTrackingEnabled",false))
            startStop.setChecked(true);
        else
            startStop.setChecked(false);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       showCurrentLocation();

    }

    private void googleFusedLocationSetUp(){
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        mSettingsClient = LocationServices.getSettingsClient(this);

        createLocationRequest();
        createLocationCallback();
        buildLocationSettingsRequest();
    }

    private void createLocationRequest(){
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void buildLocationSettingsRequest(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void createLocationCallback(){
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                final double LATITUDE = mCurrentLocation.getLatitude();
                final double LONGITUDE = mCurrentLocation.getLongitude();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                try {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    Address address = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1).get(0);
                    String addr = address.getSubLocality();
                    DBHelper dbHelper = new DBHelper(MapsActivity.this);
                    dbHelper.addData(preferences.getString("name","name"),addr,LATITUDE,LONGITUDE);
                    Log.i(TAG, "Current Time : " + mLastUpdateTime);
                    drawPolylineGoogleMap(mCurrentLocation);
                }
                catch (IOException ioe){
                    Log.i(TAG,"Exception : " + ioe.getMessage());
                }
            }
        };
    }

    private void drawPolylineGoogleMap(Location mCurrentLocation) {

        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(latLng,20);
        mMap.animateCamera(zoom);

        if(googleMarker == null){
            googleMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        }
        else{
            googleMarker.setPosition(latLng);
        }

        if(mPolylineGoogle == null){
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(10);
            polylineOptions.color(R.color.colorAccent);
            polylineOptions.add(latLng);
            polylineOptions.geodesic(true);
            mPolylineGoogle = mMap.addPolyline(polylineOptions);
        }
        else{
            List<LatLng> points = mPolylineGoogle.getPoints();
            points.add(latLng);
            mPolylineGoogle.setPoints(points);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isLocationTrackingEnabled){
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSION_REQUEST_GET_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showCurrentLocation();
            }
        }
    }

   public void showCurrentLocation(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null) {
                        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(current,20);
                        mMap.animateCamera(zoom);
                    }
                }
            });
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_REQUEST_GET_LOCATION);
        }
    }

    private void startLocationUpdates(){
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressWarnings("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mRequestingLocationUpdates =true;
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();

                        switch (statusCode){
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MapsActivity.this, LocationSettingsStatusCodes.RESOLUTION_REQUIRED);
                                }
                                catch (IntentSender.SendIntentException sie){
                                    Log.i(TAG,"PendingIntent unable to execute request");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location Settings are inadequate, cannot be fixed here , Fix in Settings";
                                Toast.makeText(MapsActivity.this,errorMessage,Toast.LENGTH_LONG);
                                mRequestingLocationUpdates = false;
                                break;
                        }
                    }
                });

    }

    private void stopLocationUpdates(){
        if(!mRequestingLocationUpdates){
            Log.i(TAG,"Location update is not requested");
            return;
        }

        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.get_track){
            // User wants to see track details
            Intent intent = new Intent(this, TrackDetails.class);
            intent.putExtra("name",trackedName.getText().toString().trim());
            startActivity(intent);
        }
        if(view.getId() == R.id.edit_btn){
            showAlertToGetName();
        }
    }

    public void showAlertToGetName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        alertView = LayoutInflater.from(MapsActivity.this).inflate(R.layout.enter_name_alert, null, false);
        builder.setView(alertView);
        builder.setPositiveButton("SAVE", MapsActivity.this);
        builder.setNegativeButton("CANCEL", MapsActivity.this);
        alertForName = builder.create();
        alertForName.show();
    }
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == Dialog.BUTTON_POSITIVE){  // user pressed save then
            TextView txtName = alertView.findViewById(R.id.name_field);
            if(!TextUtils.isEmpty(txtName.getText())){ // If Text Field is not Empty
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("name",txtName.getText().toString().trim());
                editor.putBoolean("isLocationTrackingEnabled",isLocationTrackingEnabled);
                editor.commit();
                trackedName.setText(txtName.getText().toString().trim());
                trackedBar.setVisibility(View.VISIBLE);
            }
        }
        if(which == Dialog.BUTTON_NEGATIVE){
            // If user pressed cancel dismiss the alert dialog
            dialog.dismiss();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            if(!preferences.contains("name")) {
                showAlertToGetName();
            }
            else {
                trackedBar.setVisibility(View.VISIBLE);
                isLocationTrackingEnabled = true;
                startLocationUpdates();
            }
        }else{
            trackedBar.setVisibility(View.GONE);
            isLocationTrackingEnabled = false;
            stopLocationUpdates();
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
        }
    }
}
