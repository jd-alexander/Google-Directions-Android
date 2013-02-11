package com.directionsdemo;

import com.directions.route.Routing;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;

public class MainActivity extends MapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapView mapView = (MapView)findViewById(R.id.mapview);
        MapController myMapController = mapView.getController();
        mapView.setBuiltInZoomControls(true);
        myMapController.setZoom(19);
        myMapController.setCenter(new GeoPoint((int)(18.013610*1E6),(int)(-77.498803*1E6)));
        new Routing(mapView,Color.GREEN).execute(new GeoPoint((int)(18.015365*1E6),(int)(-77.499382*1E6)), new GeoPoint((int)(18.012590*1E6),(int)(-77.500659*1E6)));
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
