package com.creatvt.ismail.mapapplication.rest;

import com.creatvt.ismail.mapapplication.data.RouteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIInterface {
    //https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=AIzaSyBYQIVKfi-DFA1vqAQ7r2zxIxf9WH9eli8
    @GET("maps/api/directions/json")
    Call<RouteResponse> getRoute(@Query("key") String key,@Query("origin") String origin, @Query("destination") String destination);

}
