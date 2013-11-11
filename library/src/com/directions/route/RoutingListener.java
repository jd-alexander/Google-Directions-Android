package com.directions.route;

import com.google.android.gms.maps.model.PolylineOptions;

public interface RoutingListener {
  public void onFailure();
  public void onStart();
  public void onSuccess(PolylineOptions mPolyOptions);
}