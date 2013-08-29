package com.directions.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.directions.route.Routing;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MyActivity extends FragmentActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SupportMapFragment fm = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map);
        GoogleMap map = fm.getMap();

        CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(18.013610,-77.498803));
        CameraUpdate zoom=  CameraUpdateFactory.zoomTo(15);

        map.moveCamera(center);
        map.animateCamera(zoom);

        new Routing(this,map,R.color.common_signin_btn_text_dark, Routing.Start.BLUE, Routing.Destination.ORANGE).execute(new LatLng(18.015365, -77.499382), new LatLng(18.012590, -77.500659));



    }
}
