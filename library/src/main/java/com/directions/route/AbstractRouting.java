package com.directions.route;

/**
 * Async Task to access the Google Direction API and return the routing data
 * which is then parsed and converting to a route overlay using some classes created by Hesham Saeed.
 * @author Joel Dean
 * @author Furkan Tektas
 * Requires an instance of the map activity and the application's current context for the progress dialog.
 *
 */

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractRouting extends AsyncTask<Void, Void, List<Route>> {
    protected List<RoutingListener> alisteners;

    protected static final String DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/json?";

    /* Private member variable that will hold the RouteException instance created in the background thread */
    private RouteException mException = null;

    public enum TravelMode {
        BIKING("bicycling"),
        DRIVING("driving"),
        WALKING("walking"),
        TRANSIT("transit");

        protected String sValue;

        TravelMode(String sValue) {
            this.sValue = sValue;
        }

        protected String getValue() {
            return sValue;
        }
    }

    public enum AvoidKind {
        TOLLS (1, "tolls"),
        HIGHWAYS (1 << 1, "highways"),
        FERRIES (1 << 2, "ferries");

        private final String sRequestParam;
        private final int sBitValue;

        AvoidKind(int bit, String param) {
            this.sBitValue = bit;
            this.sRequestParam = param;
        }

        protected int getBitValue () {
            return sBitValue;
        }

        protected static String getRequestParam (int bit) {
            StringBuilder ret = new StringBuilder();
            for (AvoidKind kind : AvoidKind.values()) {
                if ((bit & kind.sBitValue) == kind.sBitValue) {
                    ret.append(kind.sRequestParam).append('|');
                }
            }
            return ret.toString();
        }
    }

    protected AbstractRouting (RoutingListener listener) {
        this.alisteners = new ArrayList<RoutingListener>();
        registerListener(listener);
    }

    public void registerListener(RoutingListener mListener) {
        if (mListener != null) {
            alisteners.add(mListener);
        }
    }

    protected void dispatchOnStart() {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingStart();
        }
    }

    protected void dispatchOnFailure(RouteException exception) {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingFailure(exception);
        }
    }

    protected void dispatchOnSuccess(List<Route> route, int shortestRouteIndex) {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingSuccess(route, shortestRouteIndex);
        }
    }

    private void dispatchOnCancelled() {
        for (RoutingListener mListener : alisteners) {
            mListener.onRoutingCancelled();
        }
    }

    /**
     * Performs the call to the google maps API to acquire routing data and
     * deserializes it to a format the map can display.
     *
     * @return an array list containing the routes
     */
    @Override
    protected List<Route> doInBackground(Void... voids) {
        List<Route> result = new ArrayList<Route>();
        try {
            result = new GoogleParser(constructURL()).parse();
        }catch(RouteException e){
            mException = e;
        }
        return result;
    }

    protected abstract String constructURL();

    @Override
    protected void onPreExecute() {
        dispatchOnStart();
    }

    @Override
    protected void onPostExecute(List<Route> result) {
        if (!result.isEmpty()) {
            int shortestRouteIndex = 0;
            int minDistance = Integer.MAX_VALUE;

            for (int i = 0; i < result.size(); i++) {
                PolylineOptions mOptions = new PolylineOptions();
                Route route = result.get(i);

                //Find the shortest route index
                if (route.getLength() < minDistance) {
                    shortestRouteIndex = i;
                    minDistance = route.getLength();
                }

                for (LatLng point : route.getPoints()) {
                    mOptions.add(point);
                }
                result.get(i).setPolyOptions(mOptions);
            }
            dispatchOnSuccess(result, shortestRouteIndex);
        } else {
            dispatchOnFailure(mException);
        }
    }//end onPostExecute method

    @Override
    protected void onCancelled() {
        dispatchOnCancelled();
    }

}
