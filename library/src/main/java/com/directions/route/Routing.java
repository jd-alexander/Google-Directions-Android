package com.directions.route;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Async Task to access the Google Direction API and return the routing data.
 * Created by Furkan Tektas on 10/14/14.
 */
public class Routing extends AbstractRouting<LatLng> {

    public Routing(TravelMode mTravelMode) {
        super(mTravelMode);
    }

    protected String constructURL(LatLng... points) {
        if (points.length <= 1) {
            throw new IllegalArgumentException("points length : " + points.length);
        }
        final LatLng start = points[0];
        final LatLng dest = points[points.length - 1];
        final List<LatLng> waypoints = new ArrayList<>();
        if (points.length > 2) {
            for (int i = 1; i < points.length-1; i++) {
                waypoints.add(points[i]);
            }
        }

        final StringBuffer mBuf = new StringBuffer(AbstractRouting.DIRECTIONS_API_URL);
        mBuf.append("origin=");
        mBuf.append(start.latitude);
        mBuf.append(',');
        mBuf.append(start.longitude);
        mBuf.append("&destination=");
        mBuf.append(dest.latitude);
        mBuf.append(',');
        mBuf.append(dest.longitude);
        mBuf.append("&sensor=true&mode=");
        mBuf.append(_mTravelMode.getValue());
        if (waypoints.size() > 0) {
            mBuf.append("&waypoints=");
            for (LatLng p : waypoints) {
                mBuf.append(p.latitude);
                mBuf.append(',');
                mBuf.append(p.longitude);
                mBuf.append('|');
            }
        }

        return mBuf.toString();
    }
}
