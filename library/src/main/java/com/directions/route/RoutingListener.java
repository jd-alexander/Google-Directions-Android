package com.directions.route;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public interface RoutingListener {
    public void onRoutingFailure();

    public void onRoutingStart();

    public void onRoutingSuccess(PolylineOptions mPolyOptions, List<Route> route);
}
