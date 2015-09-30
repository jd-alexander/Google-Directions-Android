package com.directions.route;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public interface RoutingListener {
    public void onRoutingFailure();

    public void onRoutingStart();

    public void onRoutingSuccess(ArrayList<Route> route,int shortestRouteIndex);

    public void onRoutingCancelled();
}
