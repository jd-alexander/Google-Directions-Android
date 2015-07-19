package com.directions.route;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Async Task to access the Google Direction API and return the routing data.
 * Created by Furkan Tektas on 10/14/14.
 */
public class Routing extends AbstractRouting<LatLng> {

    private final int mAvoidKinds;

    public Routing(TravelMode mTravelMode) {
        super(mTravelMode);
        mAvoidKinds = 0;
    }

    private Routing(Builder builder) {
        super(builder.travelMode);
        mAvoidKinds = builder.avoidKinds;
    }

    protected String constructURL(LatLng... points) {
        int len = points.length;

        if (len < 2)
            throw new IllegalArgumentException("Must supply at least two points to route between.");

        // First and last points as start and destination respectively
        LatLng start = points[0];
        LatLng dest = points[len - 1];

        final StringBuffer mBuf = new StringBuffer(AbstractRouting.DIRECTIONS_API_URL);
        mBuf.append("origin=");
        mBuf.append(start.latitude);
        mBuf.append(',');
        mBuf.append(start.longitude);
        mBuf.append("&destination=");
        mBuf.append(dest.latitude);
        mBuf.append(',');
        mBuf.append(dest.longitude);

        // If more than two points are supplied, use in-between points as waypoints
        if (len > 2) {
            mBuf.append("&waypoints=");
            for (int i = 1; i < len - 1; i++) {
                mBuf.append("via:"); // we don't want to parse the resulting JSON for 'legs'.
                mBuf.append(points[i].latitude);
                mBuf.append(",");
                mBuf.append(points[i].longitude);
                mBuf.append("|");
            }
            mBuf.setLength(mBuf.length() - 1);
        }

        if (mAvoidKinds > 0) {
            mBuf.append("&avoid=");
            mBuf.append(AvoidKind.getRequestParam(mAvoidKinds));
        }

        mBuf.append("&sensor=true&mode=");
        mBuf.append(_mTravelMode.getValue());


        return mBuf.toString();
    }

    public static class Builder {

        private final TravelMode travelMode;

        private int avoidKinds;

        public Builder (TravelMode travelMode) {
            this.travelMode = travelMode;
            this.avoidKinds = 0;
        }

        public Builder avoid (AvoidKind... avoids) {
            for (AvoidKind avoidKind : avoids) {
                this.avoidKinds |= avoidKind.getBitValue();
            }
            return this;
        }

        public Routing build () {
            return new Routing(this);
        }

    }

}
