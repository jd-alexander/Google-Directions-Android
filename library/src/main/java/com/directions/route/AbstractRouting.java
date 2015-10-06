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


public abstract class AbstractRouting extends AsyncTask<Void, Void, ArrayList<Route>> {
    protected ArrayList<RoutingListener> _aListeners;

    protected static final String DIRECTIONS_API_URL = "http://maps.googleapis.com/maps/api/directions/json?";

    public enum TravelMode {
        BIKING("biking"),
        DRIVING("driving"),
        WALKING("walking"),
        TRANSIT("transit");

        protected String _sValue;

        private TravelMode(String sValue) {
            this._sValue = sValue;
        }

        protected String getValue() {
            return _sValue;
        }
    }

    public enum AvoidKind {
        TOLLS (1, "tolls"),
        HIGHWAYS (1 << 1, "highways"),
        FERRIES (1 << 2, "ferries");

        private final String _sRequestParam;
        private final int _sBitValue;

        private AvoidKind (int bit, String param) {
            this._sBitValue = bit;
            this._sRequestParam = param;
        }

        protected int getBitValue () {
            return _sBitValue;
        }

        protected static String getRequestParam (int bit) {
            String ret = "";
            for (AvoidKind kind : AvoidKind.values()) {
                if ((bit & kind._sBitValue) == kind._sBitValue) {
                    ret += kind._sRequestParam;
                    ret += "|";
                }
            }
            return ret;
        }
    }

    protected AbstractRouting (RoutingListener listener) {
        this._aListeners = new ArrayList<RoutingListener>();
        registerListener(listener);
    }

    public void registerListener(RoutingListener mListener) {
        if (mListener != null) {
            _aListeners.add(mListener);
        }
    }

    protected void dispatchOnStart() {
        for (RoutingListener mListener : _aListeners) {
            mListener.onRoutingStart();
        }
    }

    protected void dispatchOnFailure() {
        for (RoutingListener mListener : _aListeners) {
            mListener.onRoutingFailure();
        }
    }

    protected void dispatchOnSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        for (RoutingListener mListener : _aListeners) {
            mListener.onRoutingSuccess(route, shortestRouteIndex);
        }
    }

    private void dispatchOnCancelled() {
        for (RoutingListener mListener : _aListeners) {
            mListener.onRoutingCancelled();
        }
    }

    /**
     * Performs the call to the google maps API to acquire routing data and
     * deserializes it to a format the map can display.
     *
     * @param
     * @return
     */
    @Override
    protected ArrayList<Route> doInBackground(Void... voids) {
        return new GoogleParser(constructURL()).parse();
    }

    protected abstract String constructURL();

    @Override
    protected void onPreExecute() {
        dispatchOnStart();
    }

    @Override
    protected void onPostExecute(ArrayList<Route> result) {
        if (result == null) {
            dispatchOnFailure();
        } else {
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
        }
    }//end onPostExecute method

    @Override
    protected void onCancelled() {
        dispatchOnCancelled();
    }

}
