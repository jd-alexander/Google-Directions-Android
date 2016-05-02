package com.directions.route;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Async Task to access the Google Direction API and return the routing data.
 * Created by Furkan Tektas on 10/14/14.
 */
public class Routing extends AbstractRouting {

    private final TravelMode travelMode;
    private final boolean  alternativeRoutes;
    private final List<LatLng> waypoints;
    private final int avoidKinds;
    private final boolean optimize;
    private final String language;
    private final String key;

    private Routing(Builder builder) {
        super(builder.listener);
        this.travelMode = builder.travelMode;
        this.waypoints = builder.waypoints;
        this.avoidKinds = builder.avoidKinds;
        this.optimize = builder.optimize;
        this.alternativeRoutes = builder.alternativeRoutes;
        this.language = builder.language;
        this.key = builder.key;
    }

    protected String constructURL () {
        final StringBuilder stringBuilder = new StringBuilder(AbstractRouting.DIRECTIONS_API_URL);

        // origin
        final LatLng origin = waypoints.get(0);
        stringBuilder.append("origin=")
                .append(origin.latitude)
                .append(',')
                .append(origin.longitude);

        // destination
        final LatLng destination = waypoints.get(waypoints.size() - 1);
        stringBuilder.append("&destination=")
                .append(destination.latitude)
                .append(',')
                .append(destination.longitude);

        // travel
        stringBuilder.append("&mode=").append(travelMode.getValue());

        // waypoints
        if (waypoints.size() > 2) {
            stringBuilder.append("&waypoints=");
            if(optimize)
                stringBuilder.append("optimize:true|");
            for (int i = 1; i < waypoints.size() - 1; i++) {
                final LatLng p = waypoints.get(i);
                stringBuilder.append("via:"); // we don't want to parse the resulting JSON for 'legs'.
                stringBuilder.append(p.latitude);
                stringBuilder.append(',');
                stringBuilder.append(p.longitude);
                stringBuilder.append('|');
            }
        }

        // avoid
        if (avoidKinds > 0) {
            stringBuilder.append("&avoid=");
            stringBuilder.append(AvoidKind.getRequestParam(avoidKinds));
        }

        if (alternativeRoutes) {
            stringBuilder.append("&alternatives=true");
        }

        // sensor
        stringBuilder.append("&sensor=true");

        // language
        if (language != null) {
            stringBuilder.append("&language=").append(language);
        }

        // API key
        if(key != null) {
            stringBuilder.append("&key=").append(key);
        }
        return stringBuilder.toString();
    }

    public static class Builder {

        private TravelMode travelMode;
        private boolean alternativeRoutes;
        private List<LatLng> waypoints;
        private int avoidKinds;
        private RoutingListener listener;
        private boolean optimize;
        private String language;
        private String key;

        public Builder () {
            this.travelMode = TravelMode.DRIVING;
            this.alternativeRoutes = false;
            this.waypoints = new ArrayList<>();
            this.avoidKinds = 0;
            this.listener = null;
            this.optimize = false;
            this.language = null;
            this.key = null;
        }

        public Builder travelMode (TravelMode travelMode) {
            this.travelMode = travelMode;
            return this;
        }

        public Builder alternativeRoutes (boolean alternativeRoutes) {
            this.alternativeRoutes = alternativeRoutes;
            return this;
        }

        public Builder waypoints (LatLng... points) {
            waypoints.clear();
            Collections.addAll(waypoints, points);
            return this;
        }

        public Builder waypoints (List<LatLng> waypoints) {
            this.waypoints = new ArrayList<>(waypoints);
            return this;
        }

        public Builder optimize(boolean optimize) {
            this.optimize = optimize;
            return this;
        }

        public Builder avoid (AvoidKind... avoids) {
            for (AvoidKind avoidKind : avoids) {
                this.avoidKinds |= avoidKind.getBitValue();
            }
            return this;
        }

        public Builder language (String language) {
            this.language = language;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder withListener (RoutingListener listener) {
            this.listener = listener;
            return this;
        }

        public Routing build () {
            if (this.waypoints.size() < 2) {
                throw new IllegalArgumentException("Must supply at least two waypoints to route between.");
            }
            if (this.waypoints.size() <= 2 && this.optimize) {
                throw new IllegalArgumentException("You need at least three waypoints to enable optimize");
            }
            return new Routing(this);
        }

    }

}
