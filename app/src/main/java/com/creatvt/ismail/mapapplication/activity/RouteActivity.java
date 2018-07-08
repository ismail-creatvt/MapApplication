package com.creatvt.ismail.mapapplication.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.creatvt.ismail.mapapplication.R;
import com.creatvt.ismail.mapapplication.data.OverviewPolyline;
import com.creatvt.ismail.mapapplication.data.Polyline;
import com.creatvt.ismail.mapapplication.data.RouteResponse;
import com.creatvt.ismail.mapapplication.rest.APIClient;
import com.creatvt.ismail.mapapplication.rest.APIInterface;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private static final String ROUTE_API_KEY = "AIzaSyBYQIVKfi-DFA1vqAQ7r2zxIxf9WH9eli8";
    EditText edtSource,edtDestination;
    Button btnDraw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        edtSource = findViewById(R.id.source_field);
        edtDestination = findViewById(R.id.destination_field);
        btnDraw = findViewById(R.id.btn_draw);
        btnDraw.setOnClickListener(this);
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
    }

    @Override
    public void onClick(View v) {
        if(TextUtils.isEmpty(edtSource.getText())){
            showToast("Enter Source");
            return;
        }
        if(TextUtils.isEmpty(edtDestination.getText())){
            showToast("Enter Destination");
            return;
        }

        String source = edtSource.getText().toString().trim();
        String destination = edtDestination.getText().toString().trim();

        APIInterface service = APIClient.getClient().create(APIInterface.class);

        Call<RouteResponse> call = service.getRoute(ROUTE_API_KEY,source,destination);

        call.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                try {
                    RouteResponse route = response.body();
                    OverviewPolyline overviewPolyline = route.getRoutes().get(0).getOverviewPolyline();
                    String polyline = overviewPolyline.getPoints();
                    List<LatLng> latLngList = PolyUtil.decode(polyline);
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.width(10);
                    polylineOptions.color(R.color.colorAccent);
                    polylineOptions.addAll(latLngList);
                    polylineOptions.geodesic(true);
                    mMap.addPolyline(polylineOptions);

                    // Zoom Bound
                    LatLng source = latLngList.get(0);
                    LatLng destination = latLngList.get(latLngList.size()-1);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(source);
                    builder.include(destination);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),15));

                    // Add Marker
                    mMap.addMarker(new MarkerOptions().position(source).title("Source"));
                    mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
                }catch (NullPointerException npe){
                    Log.i("RouteActivity","Null Pointer Exception");
                    Toast.makeText(RouteActivity.this,"Null Pointer Exception",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                Log.d("RouteActivity","Response Failure");
            }
        });
    }

    public void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
